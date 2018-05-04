package fair.fairmobilerental.response;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Randy Richardson on 4/30/18.
 */

public class Rental {

    private String company, address, type;
    private double price;
    private float distance;

    private double lat, lng;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LatLng getLocation() {
        return new LatLng(lat, lng);
    }

    public void setLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(Location currLoc) {
        Location point = new Location("");
        point.setLatitude(lat);
        point.setLongitude(lng);
        this.distance = currLoc.distanceTo(point);
    }
}
