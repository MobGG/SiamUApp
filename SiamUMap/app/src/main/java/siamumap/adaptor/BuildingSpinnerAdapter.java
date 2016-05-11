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

/**
 * Created by Mob on 16-Dec-15.
 */
public class BuildingSpinnerAdapter extends ArrayAdapter<String> {
    Building building = null;
    private Context context;
    private ArrayList spinnerData;
    LayoutInflater inflater;

    public BuildingSpinnerAdapter(Context context, int itemLayoutID, ArrayList spinnerData ) {
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

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.custom_building_spinner, parent, false);

        /***** Get each Model object from Arraylist ********/
        building = (Building) spinnerData.get(position);

        TextView id = (TextView) row.findViewById(R.id.buildingNo);
        TextView value = (TextView) row.findViewById(R.id.buildingDescription);
        value.setTextColor(Color.parseColor("#000000"));

        id.setText(building.getBuildingNo());
        if (building.getBuildingNo() == null) {
            value.setText(building.getBuildingDescription());
        } else {
            value.setText("อาคาร " + String.valueOf(building.getBuildingNo()));
        }
        return row;
    }
}
