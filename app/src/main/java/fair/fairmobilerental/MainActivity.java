package fair.fairmobilerental;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import fair.fairmobilerental.response.RentalParser;
import fair.fairmobilerental.response.ResponseBank;
import fair.fairmobilerental.tools.CompareBuilder;
import fair.fairmobilerental.tools.DataDrop;
import fair.fairmobilerental.ui.SearchNav;
import fair.fairmobilerental.ui.ToolBar;
import fair.fairmobilerental.ui.adapters.RentalAdapter;

/**
 * Created by Randy Richardson on 4/29/18.
 */

public class MainActivity extends AppCompatActivity implements DataDrop<String> {

    private ToolBar toolBar;
    private SearchNav searchNav;
    private RentalAdapter adapter;
    private ListView listView;

    private Location currLocat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi() {

        toolBar = findViewById(R.id.tool_bar);
        searchNav = findViewById(R.id.search_nav);
        listView = findViewById(R.id.list_view);

        toolBar.build(this);
        searchNav.build(this, this);

        currLocat = searchNav.getLocat();
    }

    private void toggleLoading(boolean show) {
        findViewById(R.id.progress_lay).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private DataDrop<String> getDataDrop() {
        return this;
    }

    @Override
    public void dropData(final String type, final String object) {

        final Activity toPass = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch (type) {
                    case KeyBank.LOADING:
                        toggleLoading(true);
                        break;
                    case ResponseBank.RESULTS:
                        try {
                            JSONObject results = new JSONObject(object);
                            if(adapter == null) {
                                adapter = new RentalAdapter(toPass, getDataDrop(), RentalParser.getRentals(results, currLocat), CompareBuilder.SORTPRICE);
                            }
                            else {
                                adapter.setContent(RentalParser.getRentals(results, currLocat));
                                adapter.notifyDataSetChanged();
                            }
                            listView.setAdapter(adapter);
                            searchNav.hide();
                        }
                        catch (JSONException e) {
                        }
                        toggleLoading(false);
                        break;
                    case ResponseBank.ERROR:

                        AlertDialog.Builder dialog = null;

                        if(object != null) {
                            if(object.equals(ResponseBank.ERRBADDATE)) {
                                dialog = new AlertDialog.Builder(toPass).setTitle(getResources().getString(R.string.adj_date_error))
                                        .setMessage(getResources().getString(R.string.adj_date_error_msg));
                            }
                            else if(object.equals(ResponseBank.ERRNORES)){
                                dialog = new AlertDialog.Builder(toPass).setTitle(getResources().getString(R.string.adj_data_error))
                                        .setMessage(getResources().getString(R.string.adj_data_error_msg));
                            }
                        }
                        else {
                            dialog = new AlertDialog.Builder(toPass).setTitle(getResources().getString(R.string.unknown_error))
                                    .setMessage(getResources().getString(R.string.unknown_error_msg));
                        }
                        dialog.show();
                        toggleLoading(false);
                        break;
                    case CompareBuilder.SORT:
                        adapter.sort(object, false);
                        break;
                    case ResponseBank.ADDRESS:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(object));
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (data != null && requestCode == KeyBank.REQCODELOCAT) {
                final Place place = PlacePicker.getPlace(this, data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchNav.setPlace(place);
                    }
                });
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        Only one app permission is requested, check for others when more needed
        currLocat = searchNav.getLocat();
    }
}
