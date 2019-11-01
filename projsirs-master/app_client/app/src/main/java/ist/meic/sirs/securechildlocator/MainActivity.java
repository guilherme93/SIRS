package ist.meic.sirs.securechildlocator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private String sessionKey;
    private String phoneID;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.INTERNET, android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
 
    if(!hasPermissions(this, PERMISSIONS)){
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }
 

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final String deviceId = Utils.getPhoneID((TelephonyManager) getBaseContext().getSystemService(this.TELEPHONY_SERVICE),getContentResolver(),getApplicationContext());
        //get android unique id

        phoneID = Utils.SHA256(deviceId).replace("\n","");

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

       public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
         for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
               }
        }
    }
        return true;
    }
    
    public void login(){
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        try {
            String email= Utils.SHA256(_emailText.getText().toString()).replace("\n","");
            String password= Utils.SHA256(_passwordText.getText().toString()).replace("\n","");
            String result="2;" + email + ";" + password+";"+ phoneID + ";" + Utils.getTime();
            SSLClient ssl =new SSLClient(getApplicationContext());
            String read=Utils.readWriteSSL(getApplicationContext(),result,ssl,_loginButton);
            if(!read.equals("ERROR")){
                String tokens[]=read.split(",");
                sessionKey= tokens[1];
                ssl.closeSocket();
                progressDialog.show();
            }
            else
                return;

        } catch (Exception e) {
            e.printStackTrace();
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        try {
            String email = Utils.SHA256(_emailText.getText().toString()).replace("\n","");
            String result = "5;" +sessionKey +";"+ phoneID + ";" + email + ";" + Utils.getTime();
            SSLClient ssl = new SSLClient(getApplicationContext());
            String read=Utils.readWriteSSL(getApplicationContext(), result, ssl, _loginButton);
            Intent intent=null;
            if(read.contains("ERROR")){
                return;
            }
            else if (read.equals("new")) {
                intent = new Intent(getBaseContext(), SetupActivity.class);
            } else if (read.equals("legal")) {
                intent = new Intent(getBaseContext(), HomeActivity.class);
            } else if (read.equals("child")) {
                intent = new Intent(getBaseContext(), HomeKidActivity.class);
            }
            //send session varables
            if(intent!=null) {
                ssl.closeSocket();
                intent.putExtra("EMAIL", email);
                intent.putExtra("SESSIONKEY", sessionKey);
                intent.putExtra("ID",phoneID);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
