package fair.fairmobilerental.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import fair.fairmobilerental.KeyBank;
import fair.fairmobilerental.R;
import fair.fairmobilerental.request.TaskWorker;
import fair.fairmobilerental.response.ResponseBank;
import fair.fairmobilerental.tools.DataDrop;

/**
 * Created by Randy Richardson on 5/1/18.
 */

public class SearchNav extends LinearLayout implements View.OnClickListener {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Activity activity;
    private DataDrop<String> drop;

    private Place place;
    private Location currLoc;
    private LocationManager locatManager;

    private Calendar cal;
    private DatePickerDialog dropOffPicker, pickUpPicker;

    private LinearLayout searchGroup, locationLay;
    private RelativeLayout actionDown;
    private TextView location, pickUp, dropOff;
    private ImageView myLocat;
    private Button search;

    public SearchNav(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


//    Add singleton injection for codeability
    public void build(@NonNull Activity activity, @NonNull DataDrop<String> drop) {
        this.activity = activity;
        this.drop = drop;
        if(location == null) {
            initUi();
        }
    }

    public void hide() {
        searchGroup.setVisibility(GONE);
        actionDown.setVisibility(VISIBLE);
    }


    public void setPlace(Place place) {
        this.place = place;
        currLoc = null;
        location.setText(place.getName().toString());
    }

    private void initUi() {
        location = findViewById(R.id.location_text);
        myLocat = findViewById(R.id.my_loc);
        pickUp = findViewById(R.id.pick_up);
        dropOff = findViewById(R.id.drop_off);
        searchGroup = findViewById(R.id.search_group);
        locationLay = findViewById(R.id.search_field_lay);
        actionDown = findViewById(R.id.action_down);
        search = findViewById(R.id.search);

        myLocat.setOnClickListener(this);
        pickUp.setOnClickListener(this);
        dropOff.setOnClickListener(this);
        locationLay.setOnClickListener(this);
        actionDown.setOnClickListener(this);
        search.setOnClickListener(this);

        cal = Calendar.getInstance();

        pickUpPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                pickUp.setText(dateFormat.format(calendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


        dropOffPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                dropOff.setText(dateFormat.format(calendar.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public Location getLocat() {

//        With next version move this check to separate method or class

        if(PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                || PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            AlertDialog.Builder noLocation = new AlertDialog.Builder(getContext()).setTitle(getContext().getString(R.string.no_location))
                    .setMessage(getContext().getString(R.string.no_location_msg));
            noLocation.setPositiveButton(getContext().getString(R.string.positive_click), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 0);
                }
            }).setNegativeButton(getContext().getString(R.string.negative_click), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setCancelable(false);

            noLocation.show();
            return null;
        }

        locatManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        if(locatManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locatManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (locatManager.getBestProvider(criteria, true).equals(LocationManager.GPS_PROVIDER)) {
                return locatManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                return locatManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        else {
            AlertDialog.Builder noLocation = new AlertDialog.Builder(getContext()).setTitle(getContext().getString(R.string.no_location))
                                                                                    .setMessage(getContext().getString(R.string.no_location_msg));
            noLocation.show();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_field_lay:
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    activity.startActivityForResult(builder.build(activity), KeyBank.REQCODELOCAT);
                }
                catch(GooglePlayServicesRepairableException e) {
                    Log.d("Error0", e.getMessage());
                }
                catch(GooglePlayServicesNotAvailableException e) {
                    Log.d("Error1", e.getMessage());
                }
                catch(Exception e) {
                    Log.d("Error2", e.getMessage());
                }
                break;
            case R.id.my_loc:
                currLoc = getLocat();
                if(currLoc != null) {
                    location.setText(String.valueOf(currLoc.getLatitude() + ", " + currLoc.getLongitude()));
                    place = null;
                }
                break;
            case R.id.pick_up:
                pickUpPicker.show();
                break;
            case R.id.drop_off:
                dropOffPicker.show();
                break;
            case R.id.action_down:
                searchGroup.setVisibility(VISIBLE);
                actionDown.setVisibility(GONE);
                break;
            case R.id.search:
                if(location.getText().toString().length() > 0
                        && pickUp.getText().toString().length() > 0
                        && dropOff.getText().toString().length() > 0) {

                    Bundle bundle = new Bundle();

                    if(currLoc != null) {
                        bundle.putString(ResponseBank.LAT, String.valueOf(currLoc.getLatitude()));
                        bundle.putString(ResponseBank.LONG, String.valueOf(currLoc.getLongitude()));
                    }
                    else {
                        bundle.putString(ResponseBank.LAT, String.valueOf(place.getLatLng().latitude));
                        bundle.putString(ResponseBank.LONG, String.valueOf(place.getLatLng().longitude));
                    }

                    bundle.putString(ResponseBank.PICKUP, pickUp.getText().toString());
                    bundle.putString(ResponseBank.DROPOFF, dropOff.getText().toString());
                    TaskWorker.requestTask(bundle, drop);
                }
                break;
        }
    }
}
