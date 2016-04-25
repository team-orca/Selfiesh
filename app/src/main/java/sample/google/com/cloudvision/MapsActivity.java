package sample.google.com.cloudvision;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    TextView mTextView;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String weather;
    private String place;
    ProgressDialog progressDialog;
    String currentDateTimeString;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maplocation);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        weather = intent.getStringExtra("weather");
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        TextView descTxt = (TextView)findViewById(R.id.desctxt);
        mTextView = (TextView) findViewById(R.id.textview1);
        mTextView.setText(currentDateTimeString);
        descTxt.setText(weather);
        place="Ankara";
        Button button =(Button)findViewById(R.id.database_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Description().execute();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private class Description extends AsyncTask<Void, Void, Void> {
        DataBase db;
        Connection con;
        Statement st;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapsActivity.this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            progressDialog.dismiss();
            String myUrl = "jdbc:mysql://db4free.net:3306/fish";
            String username = "fish_user";
            String pass = "Aliveli1010*";
            try{
                Class.forName("com.mysql.jdbc.Driver").newInstance();}
            catch(Exception e){
                e.printStackTrace();
            }
            try {
                db = new DataBase();
                con = db.retConnection(myUrl, username, pass);
                st = con.createStatement();
            }
            catch (SQLException e){
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "SqlException", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        String sql;
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sql = "INSERT INTO avlar " + "VALUES (0,'EMPTY','"+currentDateTimeString+"','"
                    + Double.toString(latitude)+"','"+Double.toString(longitude)+"','"+weather+"','ENDTIME')";
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        st.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            };
            worker.schedule(task, 3, TimeUnit.SECONDS);

        }
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng currentLoc = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Your Current Position"));
        CameraUpdate center = CameraUpdateFactory.newLatLng(currentLoc);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

}
