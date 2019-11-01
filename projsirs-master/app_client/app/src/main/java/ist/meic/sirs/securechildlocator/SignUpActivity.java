package ist.meic.sirs.securechildlocator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private String sessionKey;
    private String sessionEmail;
    private String  phoneID;
    @InjectView(R.id.input_name) EditText _usernameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

        final String deviceId = Utils.getPhoneID((TelephonyManager) getBaseContext().getSystemService(this.TELEPHONY_SERVICE),getContentResolver(),getApplicationContext());
        phoneID = Utils.SHA256(deviceId).replace("\n","");

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");

        try {


            String username= _usernameText.getText().toString().replace( "\n", "" );
            sessionEmail= Utils.SHA256(_emailText.getText().toString()).replace("\n","");
            String password= Utils.SHA256(_passwordText.getText().toString()).replace("\n","");
            //check if email is unique
            String result="0;" +sessionEmail+";"+Utils.getTime();
            SSLClient ssl =new SSLClient(getApplicationContext());
            String read=Utils.readWriteSSL(getApplicationContext(),result,ssl,_signupButton);
            if(read==("Error")) {
                return;
            }

            else{
                result = "1;" + username + ";" + sessionEmail + ";" + password +";"+ phoneID + ";" + Utils.getTime();
                read=Utils.readWriteSSL(getApplicationContext(),result,ssl,_signupButton);
                if(read==("Error")) {
                    return;
                }
                String tokens[]=read.split(",");
                sessionKey= tokens[1];
                if(sessionKey == null){
                    Utils.errorHandling("Something went wrong, plase try again", getApplicationContext(),_signupButton);
                    ssl.closeSocket();
                    return;
                }
                ssl.closeSocket();
            }
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(getBaseContext(), SetupActivity.class);
        //send session variables
        intent.putExtra("EMAIL", sessionEmail);
        intent.putExtra("SESSIONKEY", sessionKey);
        intent.putExtra("ID", phoneID);
        startActivity(intent);
        finish();

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _usernameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3 || !name.matches("[a-zA-Z]+")){
            _usernameText.setError("at least 3 characters, only a-z caractheres are allowed");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

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
