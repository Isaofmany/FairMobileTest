package fair.fairmobilerental.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fair.fairmobilerental.R;
import fair.fairmobilerental.response.Rental;
import fair.fairmobilerental.response.ResponseBank;
import fair.fairmobilerental.tools.DataDrop;

/**
 * Created by Randy Richardson on 5/3/18.
 */

public class DetailsDialog extends DialogFragment implements View.OnClickListener {

    private View view;
    private TextView company, price, type, address, distance;
    private Button directions;

    private Rental rental;

    private DataDrop<String> drop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rental_details, null);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUi();
    }

    public void build(@NonNull Rental rental, @NonNull DataDrop<String> drop) {
        this.drop = drop;
        this.rental = rental;
    }

    private void initUi() {
        company = view.findViewById(R.id.company);
        price = view.findViewById(R.id.price);
        type = view.findViewById(R.id.type);
        address = view.findViewById(R.id.address);
        distance = view.findViewById(R.id.distance);

        directions = view.findViewById(R.id.get_direct);
        directions.setOnClickListener(this);

        company.setText(rental.getCompany());
        price.setText(String.valueOf(getResources().getString(R.string.currency_symbol) + rental.getPrice()));
        type.setText(rental.getType());
        address.setText(rental.getAddress());

        if(rental.getDistance() > 0) {
            distance.setText(String.valueOf(rental.getDistance()) + "M away");
        }
        else {
            distance.setText("--");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_direct:
                drop.dropData(ResponseBank.ADDRESS, rental.getLocation().toString());
                break;
        }
    }
}
