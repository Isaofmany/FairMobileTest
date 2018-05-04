package fair.fairmobilerental.tools;

/**
 * Created by Randy Richardson on 4/30/18.
 */

public interface DataDrop<T> {
    void dropData(String type, T object);
}
