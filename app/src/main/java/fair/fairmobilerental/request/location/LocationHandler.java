package fair.fairmobilerental.request.location;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import fair.fairmobilerental.R;
import fair.fairmobilerental.response.ResponseBank;
import fair.fairmobilerental.tools.DataDrop;

/**
 * Created by Randy Richardson on 5/4/18.
 */

public class LocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final long LOCUPDTIME = 60000;
    private final float LOCUPDDIST = 0;

    private Activity activity;
    private Location currLoc;
    private GoogleApiClient googleApiClient;
    private LocationManager fineManager, coarseManager;
    private DataDrop<String> drop;

    public LocationHandler(Activity activity, DataDrop<String> drop) {
        this.activity = activity;
        this.drop = drop;
        build();
    }

    public String getCurrLatLng() {

        if(currLoc == null && coarseManager != null) {
            initUpdates();
            return null;
        }
        return String.valueOf(currLoc.getLatitude() + "," + currLoc.getLongitude());
    }

    private void build() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
        googleApiClient.connect();
    }

    public void decompose() {
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    LocationListener coarseListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currLoc = location;
            drop.dropData(ResponseBank.LOCAT, String.valueOf(currLoc.getLatitude() + "," + currLoc.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    LocationListener fineListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currLoc = location;
            drop.dropData(ResponseBank.LOCAT, String.valueOf(currLoc.getLatitude() + "," + currLoc.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public static int getLocatPermit(final Activity activity) {

        if(PermissionChecker.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                || PermissionChecker.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            AlertDialog.Builder noLocation = new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.no_location))
                    .setMessage(activity.getString(R.string.no_location_msg));
            noLocation.setPositiveButton(activity.getString(R.string.positive_click), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 0);
                }
            }).setNegativeButton(activity.getString(R.string.negative_click), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setCancelable(false);

            noLocation.show();
        }
        else {
            return PackageManager.PERMISSION_GRANTED;
        }

        return PackageManager.PERMISSION_DENIED;
    }

    private void initUpdates() {

        if(fineManager == null) {
            fineManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            coarseManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        }

//        Instance of class is never created without permission, no check needed here
        coarseManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCUPDTIME, LOCUPDDIST, coarseListener);
        fineManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCUPDTIME, LOCUPDDIST, fineListener);
    }
}
