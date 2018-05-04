package fair.fairmobilerental.request;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.IOException;

import fair.fairmobilerental.KeyBank;
import fair.fairmobilerental.response.ResponseBank;
import fair.fairmobilerental.tools.DataDrop;

/**
 * Created by Randy Richardson on 4/29/18.
 */

public class TaskWorker {

    public static void requestTask(final @NonNull Bundle bundle, final @NonNull DataDrop<String> drop) {

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TaskRental.request(drop, bundle, 0);
                    drop.dropData(KeyBank.LOADING, null);
                }
                catch(IOException e) {
                    drop.dropData(ResponseBank.ERROR, null);
                }
            }
        });

        worker.start();
    }
}
