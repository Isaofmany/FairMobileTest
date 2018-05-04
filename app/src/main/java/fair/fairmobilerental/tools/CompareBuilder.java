package fair.fairmobilerental.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import fair.fairmobilerental.response.Rental;

/**
 * Created by Randy Richardson on 5/2/18.
 */

public class CompareBuilder {


    public static final String SORT = "sort";
    public static final String SORTCOMP = "sortCompany";
    public static final String SORTDIST = "sortDistance";
    public static final String SORTPRICE = "sortPrice";
    public static final String SORTCOMPDWN = "sortCompanyDwn";
    public static final String SORTDISTDWN = "sortDistanceDwn";
    public static final String SORTPRICEDWN = "sortPriceDwn";

    public static class PriceCompare implements Comparator<Rental> {

        public int compare(Rental obj0, Rental obj1) {

            if(obj0.getPrice() < obj1.getPrice()) {
                return -1;
            }
            else if(obj1.getPrice() > obj0.getPrice()) {
                return 1;
            }
            else {
                return 1;
            }
        }
    }

    public static class DistanceCompare implements Comparator<Rental> {

        public int compare(Rental obj0, Rental obj1) {

            if(obj0.getDistance() < obj1.getDistance()) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }

    public static class CompanyCompare implements Comparator<Rental> {

        public int compare(Rental obj0, Rental obj1) {
            return obj0.getCompany().compareToIgnoreCase(obj1.getCompany());
        }
    }

}
