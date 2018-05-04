package fair.fairmobilerental.request;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fair.fairmobilerental.response.ResponseBank;
import fair.fairmobilerental.tools.DataDrop;

/**
 * Created by Randy Richardson on 4/29/18.
 */

public class TaskRental {

    private static final int TIMEOUT = 30000;
    private static final String RADIUS = "40";

    private static HttpsURLConnection connection;
    private static URL url = null;
    private static Bundle bundle;

    public static void request(@NonNull DataDrop<String> resDrop, @NonNull Bundle bundle0, int attempt) throws IOException {

        bundle = bundle0;

        if((url = configureURL()) != null) {
            connection = (HttpsURLConnection) url.openConnection();
            configConnection(connection);
        }
        else {
            resDrop.dropData(ResponseBank.ERROR, null);
        }

        connection.connect();

        if(connection.getErrorStream() != null) {
//            Handle error here

            if(attempt < 2) {
                request(resDrop, bundle, attempt + 1);
            }
            else {
                Log.d("DATA", connection.getErrorStream().toString());
                resDrop.dropData(ResponseBank.ERROR, String.valueOf(connection.getResponseCode()));
                connection.disconnect();
            }
        }
        else {

            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            catch (FileNotFoundException e) {
                resDrop.dropData(ResponseBank.ERROR, "500");
                return;
            }
            StringBuilder builder = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            resDrop.dropData(RequestBank.RESULTS, builder.toString());
        }
    }

    private static URL configureURL() {
        try {
            return new URL(RequestBank.CAR + RequestBank.KEY + RequestBank.LAT + bundle.getString(ResponseBank.LAT)
                                + RequestBank.LONG + bundle.getString(ResponseBank.LONG) + RequestBank.RADIUS + RADIUS
                                + RequestBank.PICKUP + bundle.getString(ResponseBank.PICKUP) + RequestBank.DROPOFF
                                + bundle.getString(ResponseBank.DROPOFF));
        }
        catch (MalformedURLException e) {
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static void configConnection(HttpsURLConnection connection) {
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setUseCaches(false);
        connection.setConnectTimeout(TIMEOUT);
    }
}
