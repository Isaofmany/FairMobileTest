package fair.fairmobilerental.ui.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import fair.fairmobilerental.R;
import fair.fairmobilerental.response.Rental;
import fair.fairmobilerental.tools.CompareBuilder;
import fair.fairmobilerental.tools.DataDrop;
import fair.fairmobilerental.ui.dialogs.DetailsDialog;

/**
 * Created by Randy Richardson on 5/1/18.
 */

public class RentalAdapter extends BaseAdapter {

    private Activity activity;
    private DataDrop<String> drop;
    private FragmentManager fragmentManager;

    private List<Rental> content;

    public RentalAdapter(@NonNull Activity activity, @NonNull DataDrop<String> drop, List<Rental> content, @NonNull String type) {
        this.drop = drop;
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.content = content;
        sort(type, true);
    }

    public void setContent(List<Rental> content) {
        this.content = content;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item, null);
        }

        ((TextView) convertView.findViewById(R.id.company)).setText(content.get(position).getCompany());
        ((TextView) convertView.findViewById(R.id.price)).setText(activity.getString(R.string.currency_symbol)
                                                                    + String.valueOf(content.get(position).getPrice()));

        if(content.get(position).getDistance() == 0) {
            ((TextView) convertView.findViewById(R.id.distance)).setText("--M");
        }
        else {
            ((TextView) convertView.findViewById(R.id.distance)).setText(String.valueOf(content.get(position).getDistance() + "M"));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsDialog dialog = new DetailsDialog();
                dialog.build(content.get(position), drop);
                dialog.show(activity.getFragmentManager(), null);
            }
        });

        return convertView;
    }

    public void sort(String type, boolean initial) {
        if (type.contains(CompareBuilder.SORTCOMP)) {
            Collections.sort(content, new CompareBuilder.CompanyCompare());
        }
        else if (type.contains(CompareBuilder.SORTDIST)) {
            Collections.sort(content, new CompareBuilder.DistanceCompare());
        }
        else if (type.contains(CompareBuilder.SORTPRICE)) {
            Collections.sort(content, new CompareBuilder.PriceCompare());
        }

        if(type.contains("Dwn")) {
            Collections.reverse(content);
        }

        if(!initial) {
            notifyDataSetChanged();
        }
    }
}
