package ist.meic.sirs.securechildlocator;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeActivity extends AppCompatActivity {

    @InjectView(R.id.greeting_text) TextView _greetingText;
    @InjectView(R.id.button_find) Button _buttonFind;
    @InjectView(R.id.button_manage) Button _buttonManage;
    @InjectView(R.id.button_logout) Button _buttonLogout;

    private String sessionEmail;
    private String sessionKey;
    private String sessionPhoneID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        sessionEmail = getIntent().getStringExtra("EMAIL").replace( "\n", "" );
        sessionKey = getIntent().getStringExtra("SESSIONKEY").replace( "\n", "" );
        sessionPhoneID=getIntent().getStringExtra("ID").replace( "\n", "" );
        
        String result="6;" + sessionKey +";"+sessionPhoneID+";"+sessionEmail+ ";" +Utils.getTime();
        SSLClient ssl =new SSLClient(getApplicationContext());
        String read= Utils.readWriteSSL(getApplicationContext(), result, ssl, null);
        if(read=="ERROR"){
            return;
        }
        ssl.closeSocket();
        _greetingText.setText("Hello " + read ); //add username


        _buttonFind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                
                Intent intent = new Intent(getBaseContext(), SelectFind.class);
                //send session varables
                intent.putExtra("EMAIL",sessionEmail);
                intent.putExtra("SESSIONKEY", sessionKey);
                intent.putExtra("ID", sessionPhoneID);
                startActivity(intent);
            }
        });

        _buttonManage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SelectLegalGuardian.class);
                //send session varables
                intent.putExtra("EMAIL",sessionEmail);
                intent.putExtra("SESSIONKEY", sessionKey);
                intent.putExtra("ID", sessionPhoneID);
                startActivity(intent);
            }
        });

        _buttonLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SSLClient ssl =new SSLClient(getApplicationContext());
                String result="10;" + sessionKey +";"+sessionPhoneID+";"+sessionEmail+";"+Utils.getTime();
                String read=Utils.readWriteSSL(getApplicationContext(),result,ssl,_buttonLogout);
                        if(read=="ERROR"){
                    return;
                }
                ssl.closeSocket();
                finish();
            }
        });
    }


}
