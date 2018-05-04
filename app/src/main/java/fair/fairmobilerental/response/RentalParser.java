package fair.fairmobilerental.response;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Randy Richardson on 4/30/18.
 */

public class RentalParser {

    private static Location currLocat;

    public static List<Rental> getRentals(@NonNull JSONObject object, Location currLocat0) throws JSONException {

        currLocat = currLocat0;
        final JSONArray array = object.getJSONArray(ResponseBank.RESULTS);
        final List<Rental> results = new ArrayList<>();

        for(int x = 0; x < array.length(); x++) {
            results.addAll(objectParser(array.getJSONObject(x)));
        }

        return results;
    }

    private static List<Rental> objectParser(JSONObject object) throws JSONException {

        List<Rental> rentals = new ArrayList<>();
        String company = object.getJSONObject(ResponseBank.PROVIDER).getString(ResponseBank.COMPNAME);
        String address = object.getJSONObject(ResponseBank.ADDRESS).getString(ResponseBank.LINE1)
                            + "\n" + object.getJSONObject(ResponseBank.ADDRESS).getString(ResponseBank.CITY)
                            + "\n" + object.getJSONObject(ResponseBank.ADDRESS).getString(ResponseBank.REGION)
                            + "\n" + object.getJSONObject(ResponseBank.ADDRESS).getString(ResponseBank.COUNTRY);
        double lat = Double.valueOf(object.getJSONObject(ResponseBank.LOCAT).getString(ResponseBank.LAT));
        double lng = Double.valueOf(object.getJSONObject(ResponseBank.LOCAT).getString(ResponseBank.LONG));

        JSONArray cars = object.getJSONArray(ResponseBank.CARS);

        for(int x = 0; x < cars.length(); x++) {

//            Catch JsonException here for individual car items, but continue for other's success
            try {
                Rental rental = new Rental();
                rental.setCompany(company);
                rental.setLocation(lat, lng);
                rental.setAddress(address);
                rental.setType(cars.getJSONObject(x).getJSONObject(ResponseBank.VEHICINFO).getString(ResponseBank.CATEGORY));
                rental.setPrice(cars.getJSONObject(x).getJSONArray(ResponseBank.RATES).getJSONObject(0)
                                                                            .getJSONObject(ResponseBank.PRICE).getDouble(ResponseBank.AMOUNT));
                if(currLocat != null) {
                    rental.setDistance(currLocat);
                }
                rentals.add(rental);
            }
            catch (JSONException e) {
                Log.d("Data", e.getMessage());
            }
            catch (Exception e) {
                Log.d("Data", e.getMessage());
            }
        }

        return rentals;
    }
}
