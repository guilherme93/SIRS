package ist.meic.sirs.securechildlocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SelectLegalGuardian extends AppCompatActivity {
    private String[] LegalNames;
    private String sessionEmail;
    private String sessionKey;
    private String sessionPhoneID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_legal_guardian);
        sessionEmail = getIntent().getStringExtra("EMAIL").replace( "\n", "" );
        sessionKey=getIntent().getStringExtra("SESSIONKEY").replace( "\n", "" );
        sessionPhoneID = getIntent().getStringExtra("ID").replace( "\n", "" );

        String result="14;" + sessionKey +";"+sessionPhoneID+";"+sessionEmail+";"+Utils.getTime();
        SSLClient ssl =new SSLClient(getApplicationContext());
        String read= Utils.readWriteSSL(getApplicationContext(), result, ssl, null);
        if(read=="ERROR")
            return;
        ssl.closeSocket();

        if (!read.isEmpty()) {
            String[] phonesNames = read.split(",");
            final ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, phonesNames);
            ListView listView = (ListView) findViewById(R.id.legalguardin_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    // String item = adapter.getItem(position).toString();
                    removeUser(adapter.getItem(position).toString());
                }
            });
        }
        else {
            // got to Home
            Toast.makeText(getBaseContext(), "No Accounts Found", Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("EMAIL", sessionEmail);
            intent.putExtra("SESSIONKEY", sessionKey);
            intent.putExtra("ID", sessionPhoneID);
            startActivity(intent);
            finish();
        }
    }
    public void removeUser(String legalName){
        legalName.replace( "\n", "" );
        String result="15;" + sessionKey +";"+sessionPhoneID+";"+sessionEmail+";"+legalName+";"+Utils.getTime();
        SSLClient ssl =new SSLClient(getApplicationContext());
        String read= Utils.readWriteSSL(getApplicationContext(), result, ssl, null);
        if(read=="ERROR")
            return;
        ssl.closeSocket();
        Toast.makeText(getBaseContext(), "Removed User Successfully", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        intent.putExtra("EMAIL",sessionEmail);
        intent.putExtra("SESSIONKEY", sessionKey);
        intent.putExtra("ID", sessionPhoneID);
        startActivity(intent);
        finish();
    }
}
