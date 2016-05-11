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
import siamumap.dto.Faculty;


/**
 * Created by Mob on 14-Dec-15.
 */
public class FacultySpinnerAdapter extends ArrayAdapter<String> {

    Faculty faculty = null;
    private Context context;
    private ArrayList spinnerData;
    LayoutInflater inflater;

    public FacultySpinnerAdapter(Context context, int itemLayoutId, ArrayList spinnerData) {
        super(context, itemLayoutId, spinnerData);
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
        View row = inflater.inflate(R.layout.custom_faculty_spinner, parent, false);

        /***** Get each Model object from Arraylist ********/
        faculty = (Faculty) spinnerData.get(position);

        TextView id = (TextView) row.findViewById(R.id.spinnerID);
        TextView value = (TextView) row.findViewById(R.id.spinnerValue);
        value.setTextColor(Color.parseColor("#000000"));

        id.setText(faculty.getFacultyID());
        value.setText(faculty.getFacultyName());

        return row;
    }
}
