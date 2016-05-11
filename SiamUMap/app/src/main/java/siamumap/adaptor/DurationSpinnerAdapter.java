package siamumap.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.siam.siamumap.R;
import siamumap.dto.Building;
import siamumap.dto.Duration;

/**
 * Created by Mob on 13-Jan-16.
 */
public class DurationSpinnerAdapter extends ArrayAdapter<String> {
    Duration duration;
    private Context context;
    private ArrayList spinnerData;
    LayoutInflater inflater;

    public DurationSpinnerAdapter(Context context, int itemLayoutID, ArrayList spinnerData) {
        super(context, itemLayoutID, spinnerData);
        this.context = context;
        this.spinnerData = spinnerData;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.custom_duration_spinner, parent, false);

        duration = (Duration) spinnerData.get(position);

        TextView id = (TextView) row.findViewById(R.id.id);
        TextView value = (TextView) row.findViewById(R.id.value);
        value.setTextColor(Color.parseColor("#000000"));

        id.setText(duration.getId());
        value.setText(duration.getValue());
        return row;
    }
}
