package ist.meic.sirs.securechildlocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RequestAuthentication extends AppCompatActivity {
    @InjectView(R.id.button_confirm) Button _buttonConfirm;
    @InjectView(R.id.password_input) TextView _passwordInput;

    private String sessionEmail;
    private String sessionKey;
    private String sessionPhoneID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_authentication);
        ButterKnife.inject(this);

        sessionEmail = getIntent().getStringExtra("EMAIL").replace( "\n", "" );
        sessionKey = getIntent().getStringExtra("SESSIONKEY").replace( "\n", "" );
        sessionPhoneID=getIntent().getStringExtra("ID").replace( "\n", "" );

        _buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPass();
            }

        });
    }

    public boolean validate() {
        boolean valid = true;

        String input_password = _passwordInput.getText().toString().replace( "\n", "" );

        if (input_password.isEmpty() || input_password.length() < 4 || input_password.length() > 10) {
            _passwordInput.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordInput.setError(null);
        }

        return valid;
    }

    public void confirmPass(){
        try{

            String input_password = Utils.SHA256(_passwordInput.getText().toString()).replace("\n","");
            if (!validate()) {
                return;
            }

            String result="13;" + sessionKey +";"+sessionPhoneID+";"+sessionEmail+";"+ input_password+";" +Utils.getTime();
            SSLClient ssl =new SSLClient(getApplicationContext());
            String read = Utils.readWriteSSL(getApplicationContext(),result,ssl,_buttonConfirm);
            if (read.contains("ERROR")) {
                _passwordInput.setText("");
                return;
            }
            ssl.closeSocket();
            Intent intent = new Intent(getBaseContext(),SelectLegalGuardian.class);
            //send session varables
            intent.putExtra("EMAIL",sessionEmail);
            intent.putExtra("SESSIONKEY", sessionKey);
            intent.putExtra("ID", sessionPhoneID);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
