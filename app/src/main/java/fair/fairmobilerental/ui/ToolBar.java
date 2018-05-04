package fair.fairmobilerental.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import fair.fairmobilerental.KeyBank;
import fair.fairmobilerental.R;
import fair.fairmobilerental.tools.CompareBuilder;
import fair.fairmobilerental.tools.DataDrop;

/**
 * Created by Randy Richardson on 4/30/18.
 */

public class ToolBar extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    private ImageView company, locat, price;
    private DataDrop<String> drop;

    private boolean longPress;

    public ToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void build(DataDrop<String> drop) {
        initUi();
        this.drop = drop;
    }

    private void initUi() {
        company = findViewById(R.id.company);
        locat = findViewById(R.id.distance);
        price = findViewById(R.id.price);

        company.setOnClickListener(this);
        locat.setOnClickListener(this);
        price.setOnClickListener(this);
        company.setOnLongClickListener(this);
        locat.setOnLongClickListener(this);
        price.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(!longPress) {
            dropResponse(view, false);
        }
        longPress = false;
    }

    @Override
    public boolean onLongClick(View v) {
        dropResponse(v, true);
        longPress = true;
        return false;
    }

    private void dropResponse(View view, boolean isReverse) {
        switch (view.getId()) {
            case R.id.company:
                drop.dropData(CompareBuilder.SORT, isReverse ? CompareBuilder.SORTCOMPDWN : CompareBuilder.SORTCOMP);
                break;
            case R.id.distance:
                drop.dropData(CompareBuilder.SORT, isReverse ? CompareBuilder.SORTDISTDWN : CompareBuilder.SORTDIST);
                break;
            case R.id.price:
                drop.dropData(CompareBuilder.SORT, isReverse ? CompareBuilder.SORTPRICEDWN : CompareBuilder.SORTPRICE);
                break;
        }
    }
}
