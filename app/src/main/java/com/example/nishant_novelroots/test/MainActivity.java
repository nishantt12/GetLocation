package com.example.nishant_novelroots.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
public class MainActivity extends ActionBarActivity implements View.OnClickListener,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
Button update;
    TextView lat,lng;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    Location mCurrentLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        createClient();
    }

    private void createClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }




    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    private void updateLocation(Location location) {
        Log.e("updateLocation","Re create" );
        if (location != null) {
            Log.e("updateLocation","not null" );
            lat.setText(String.valueOf(location.getLatitude()));
            lng.setText(String.valueOf(location.getLongitude()));
        }
    }


    private void init()
{
    update = (Button) findViewById(R.id.update);
    lat = (TextView) findViewById(R.id.lat);
    lng = (TextView) findViewById(R.id.lng);
    update.setOnClickListener(this);
}

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.update:
                updateLocation();
                break;
            default:
                break;
        }

    }
private void updateLocation()
{
if(showDialog())
{
    if(isOnline()) {
        startLocationUpdates();
    }
    else
    {
        Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
    }
}
}

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("onconnected","onconnected" );
       startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("onconnectedS","onconnected" );
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("onconnectedF","onconnected" );
    }
    protected void startLocationUpdates() {

        if(mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            Log.e("startLocationUpdates","Location Update" );
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        else
        {
            Log.e("startLocationUpdates","Re create" );
            createClient();
            mGoogleApiClient.connect();
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }
    private boolean showDialog()
    {
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false;
        if(lm==null)
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Location service not enabled");
            dialog.setPositiveButton("Open location services", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    paramDialogInterface.cancel();
                }
            });
            dialog.show();
return false;
        }
        else
        {
            return true;
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
