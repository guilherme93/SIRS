package org.child.secure.locator.maven;


import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import com.sun.net.ssl.internal.ssl.Provider;
import java.io.IOException;
import java.security.Security;
import java.util.Vector;


public class sslServer extends Thread
{
    private Vector<ClientHandlerThread> connectedClients = new Vector<ClientHandlerThread>(20, 5);
    private static int dataPort=9999;
    private static boolean running=true;
    public static void  main(String[] arstring) {
    	sslServer s = new sslServer();
	    SSLServerSocket sslDataTraffic = null;
	    SSLServerSocketFactory sslFac = null;
	
	    try
	    {
	    	 Security.addProvider(new Provider());	 
		 System.setProperty("javax.net.ssl.keyStore","serverKey.keystore");
		 System.setProperty("javax.net.ssl.keyStorePassword","EpW5eE[bxwHu");
		 sslFac=(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	      
	    }
	    catch (Exception e) {
	    	System.out.println(e.toString() + " ::: " + e.getCause());
	    }
	
	    try
	    {
	        System.out.print("Creating data socket......... ");
	        sslDataTraffic = (SSLServerSocket) sslFac.createServerSocket(dataPort);
	        System.out.println("DONE. Est. on:" + dataPort);
	    }
	    catch (IOException e)
	    {
	        System.out.println("FAILED.");
	        System.out.println(e.toString() + " ::: " + e.getCause());
	        System.exit(-1);
	    }
	    
	    while (running) {
		try {
			SSLSocket sslDataTrafficSocketInstance = (SSLSocket) sslDataTraffic.accept();
			sslDataTrafficSocketInstance.setEnabledCipherSuites(new String[] {"TLS_DHE_RSA_WITH_AES_128_CBC_SHA256"});
			ClientHandlerThread c = new ClientHandlerThread(sslDataTrafficSocketInstance,s,s.connectedClients.size());
			c.start();
			s.connectedClients.add(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
	            
          }
   }
   public ClientHandlerThread getThread(int index) {return connectedClients.get(index);}
}
