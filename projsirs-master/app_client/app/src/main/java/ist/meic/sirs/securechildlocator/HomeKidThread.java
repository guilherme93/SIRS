package ist.meic.sirs.securechildlocator;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HomeKidThread implements Runnable {

    private Context context;
    private String sessionEmail;
    private String sessionKey;
    private String sessionPhoneID;
    private GPSTracker gps;
    double latitude = 200.0; //doesnt exist (caps at 180)
    double longitude = 200.0; //doesnt exist (caps at 180)

    public HomeKidThread(Context context, String sessionKey, String sessionPhoneID, String sessionEmail, GPSTracker gps){
        this.sessionEmail = sessionEmail;
        this.sessionKey = sessionKey;
        this.sessionPhoneID = sessionPhoneID;
        this.gps = gps;
        this.context = context;
    }

    public void run() {
        SSLClient ssl = new SSLClient(context);
        String result = "11;" + sessionKey + ";" + sessionPhoneID + ";" + sessionEmail + ";" + Utils.getTime();
        String read = Utils.readWriteSSL(context,result,ssl,null);
        if (!read.contains("ERROR")) {
            //TODO
        }

        while (true) {
            waitForRequest(ssl);
        }
    }

    private void waitForRequest(SSLClient ssl) {
        try {
            String read = null;
            read =ssl.readFromServer();
            System.err.println(read);
            String result;
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                result = "12;" + sessionKey + ";" + sessionPhoneID + ";" + sessionEmail + ";" + latitude
                        + ";" + longitude + ";" + Utils.getTime();
            }
            else if ((latitude != 200.0) && (longitude != 200.0)) {
                result = "12;" + sessionKey + ";" + sessionPhoneID + ";" + sessionEmail + ";" + latitude
                        + ";" + longitude + ";" + Utils.getTime();
            }
            else {
                result = "error";
            }
            System.err.println(result);
            ssl.writeToServer(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
