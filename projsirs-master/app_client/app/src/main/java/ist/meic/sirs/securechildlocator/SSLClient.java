package ist.meic.sirs.securechildlocator;

import android.content.Context;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import java.io.*;
import java.net.SocketException;
import java.security.KeyStore;

public class SSLClient {
    private SSLSocket sslsocket=null;
    private BufferedWriter  bufferedwriter=null;
    private BufferedReader bufferedReader=null;
   public SSLClient(Context context) {
       try {
           KeyStore tsTrust = KeyStore.getInstance("BKS");
           InputStream instream = context.getResources().openRawResource(R.raw.truststore);
           tsTrust.load(instream, "EpW5eE[bxwHu".toCharArray());
           TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
           tmf.init(tsTrust);

           instream = context.getResources().openRawResource(R.raw.client);
           KeyStore ksStore = KeyStore.getInstance("BKS");
           ksStore.load(instream, "sirs".toCharArray());

           KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
           kmfactory.init(ksStore, "sirs".toCharArray());

           SSLContext sslContext = SSLContext.getInstance("TLS");
           sslContext.init(kmfactory.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());

           SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();

           sslsocket = (SSLSocket) sslsocketfactory.createSocket("46.101.3.38", 9999);
           sslsocket.setEnabledCipherSuites(new String[] {"TLS_DHE_RSA_WITH_AES_128_CBC_SHA256"});
           OutputStream outputstream = sslsocket.getOutputStream();
           OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
           bufferedwriter = new BufferedWriter(outputstreamwriter);
           InputStream inputstream = sslsocket.getInputStream();
           InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
           bufferedReader = new BufferedReader(inputstreamreader);

       } catch (Exception exception) {
           exception.printStackTrace();
       }
   }
    public void writeToServer(String write){
        try {
            bufferedwriter.write(write + '\n');
            bufferedwriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String readFromServer () {
        String read=null;
        try {
            read= bufferedReader.readLine();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return read;
    }
    public void closeSocket(){
        if(sslsocket  != null){
            try {
                sslsocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTimeout(int timeout) throws SocketException {
        sslsocket.setSoTimeout(timeout);
    }
}