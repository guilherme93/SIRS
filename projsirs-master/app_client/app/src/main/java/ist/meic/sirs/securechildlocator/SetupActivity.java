package ist.meic.sirs.securechildlocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SetupActivity extends AppCompatActivity {

    @InjectView(R.id.button_legalguardian) Button _legalguardianbutton;
    @InjectView(R.id.button_child) Button _childbutton;
    @InjectView(R.id.phone_name)  TextView _phone_name;

    private String sessionEmail;
    private String sessionKey;
    private String sessionPhone;

    public void goToHome(Intent intent){
        intent.putExtra("EMAIL", sessionEmail);
        intent.putExtra("SESSIONKEY", sessionKey);
        intent.putExtra("ID", sessionPhone);
        startActivity(intent);
        finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ButterKnife.inject(this);

        //get sessions variables
        sessionEmail = getIntent().getStringExtra("EMAIL");
        sessionKey=getIntent().getStringExtra("SESSIONKEY");
        sessionPhone= getIntent().getStringExtra("ID");
        //get android unique id

       _legalguardianbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String phone_name =  _phone_name.getText().toString().replace( "\n", "" );
                    String result = "4;"+ sessionKey+";"+ sessionPhone +";" + sessionEmail + ";"+ phone_name +";"+ Utils.getTime();

                    SSLClient ssl = new SSLClient(getApplicationContext());
                    String read=Utils.readWriteSSL(getApplicationContext(),result,ssl,_legalguardianbutton);
                    if(read=="ERROR")
                        return;
                    ssl.closeSocket();
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    goToHome(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        _childbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String phone_name =  _phone_name.getText().toString().replace( "\n", "" );
                    //send to server
                    String result = "3;"+ sessionKey+";"+ sessionPhone +";" + sessionEmail + ";"+ phone_name +";"+ Utils.getTime();
                    SSLClient ssl = new SSLClient(getApplicationContext());
                    String read=Utils.readWriteSSL(getApplicationContext(),result,ssl,_childbutton);
                    if(read=="ERROR")
                        return;
                    ssl.closeSocket();
                    Intent intent = new Intent(getBaseContext(), HomeKidActivity.class);
                    goToHome(intent);

                } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
    }
}
