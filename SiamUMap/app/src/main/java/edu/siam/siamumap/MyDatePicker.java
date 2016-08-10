package edu.siam.siamumap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 8/8/2016.
 */
public class MyDatePicker {

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        TheListener listener;

        public interface TheListener {
            void returnStartDate(String date);
            void returnEndDate(String date);
        }

        public static final String startDate = "start";
        public static final String endDate = "end";
        private String flag;

        public void setFlag(String typeDate) {
            flag = typeDate;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            listener = (TheListener) getActivity();
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar dateSelected = Calendar.getInstance();
            dateSelected.set(year, month, day);
            SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
            String date = dateFormater.format(dateSelected.getTime());
            if (flag == startDate) {
                listener.returnStartDate(date);
            } else if (flag == endDate) {
                listener.returnEndDate(date);
            }
        }
    }
}
