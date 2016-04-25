package sample.google.com.cloudvision;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GetLocationActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String url = "http://openweathermap.org/";
    ProgressDialog progressDialog;
    String desc;

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlocation);



        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
        LinearLayout descButton = (LinearLayout) findViewById(R.id.relative);
        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGPSEnabled(getBaseContext())) {
                    new Description().execute();
                    Runnable task = new Runnable() {
                        @Override
                        public void run() {
                            if (mLastLocation != null) {
                                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                                intent.putExtra("weather", desc);
                                intent.putExtra("latitude", mLastLocation.getLatitude());
                                intent.putExtra("longitude", mLastLocation.getLongitude());
                                startActivity(intent);
                                finish();
                            }

                            if (mLastLocation == null) {
                            }

                        }
                    };
                    worker.schedule(task, 3, TimeUnit.SECONDS);
                }
                else{
                    Toast.makeText(GetLocationActivity.this, "Please open your gps!!", Toast.LENGTH_SHORT).show();
                finish();}




    }});}
    public static boolean isGPSEnabled(Context mContext)
    {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnected(Bundle arg0) {

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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private class Description extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GetLocationActivity.this);
            progressDialog.setTitle("Description");
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document document = Jsoup.connect(url).get();
                Elements element = document.select("div[class=weather-widget]");
                desc = element.first().text();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            String[] results = desc.split(" ");
            desc="";
            for(int i=0; i<results.length; i++){
                if(results[i].equals("Wind") || results[i].equals("Pressure") || results[i].equals("Humidity") || results[i].equals("Cloudiness")){
                    desc +="\n";
                    desc += " " + results[i] + ": ";
                }
                else if(results[i].equals("Geo") || results[i].equals("Sunrise")){
                    break;
                }

                else
                if(i>0)
                    desc += " " + results[i];

            }
            progressDialog.dismiss();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

