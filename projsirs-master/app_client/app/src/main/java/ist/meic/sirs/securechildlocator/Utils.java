package ist.meic.sirs.securechildlocator;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;

public class Utils {
    public static String SHA256 (String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte[] digest = md.digest();
            return Base64.encodeToString(digest, Base64.DEFAULT);


            //return digest.toString().replace("\n", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, +2);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(c.getTime());
    }
    public static void errorHandling(String error, Context context, Button button) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, error, duration);
        toast.show();
        button.setEnabled(true);
    }

    public static String getPhoneID(TelephonyManager tm, ContentResolver contentResolver, Context context){
        //get android unique id
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {}
        
        
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }
    public static String readWriteSSL(Context context, String result, SSLClient ssl, Button button){
        ssl.writeToServer(result);
        try {
            ssl.setTimeout(2000);
        } catch (SocketException e) {
            return "ERROR";
        }
        String read=ssl.readFromServer();
        if(read.contains("Error")) {
            if(button!=null)
                Utils.errorHandling(read, context,button);
            ssl.closeSocket();
            return "ERROR";
        }
        return read;
    }
    public static Intent createIntent(Context context, Class c,String sessionEmail,String sessionKey,String sessionPhoneID){
        Intent intent = new Intent(context, c);
        intent.putExtra("EMAIL", sessionEmail);
        intent.putExtra("SESSIONKEY", sessionKey);
        intent.putExtra("ID", sessionPhoneID);
        return intent;
    }
}
