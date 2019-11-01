package ist.meic.sirs.securechildlocator;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String kidName;
    private String sessionEmail;
    private String sessionKey;
    private String sessionPhoneID;
    GPSTracker gps;
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sessionEmail = getIntent().getStringExtra("EMAIL").replace( "\n", "" );
        sessionKey=getIntent().getStringExtra("SESSIONKEY").replace( "\n", "" );
        sessionPhoneID = getIntent().getStringExtra("ID").replace( "\n", "" );
        kidName = getIntent().getStringExtra("KIDNAME").replace( "\n", "" );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public boolean getKidLocation() {
        String result="9;" + sessionKey +";"+sessionPhoneID+";"+sessionEmail+ ";" + kidName + ";"+Utils.getTime();
        SSLClient ssl =new SSLClient(getApplicationContext());
        String read= Utils.readWriteSSL(getApplicationContext(), result, ssl, null);
        if (!read.contains("ERROR")) {
            ssl.closeSocket();
            String[] location = read.split(",");
            latitude = Double.parseDouble(location[0]);
            longitude = Double.parseDouble(location[1]);
            return true;
        }
        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(getKidLocation()) {
            LatLng kid = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(kid).title(kidName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(kid));
        }
    }
}
