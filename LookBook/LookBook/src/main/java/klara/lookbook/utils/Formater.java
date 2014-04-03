package klara.lookbook.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.TypedValue;

import java.util.Calendar;

public class Formater {

    public static final String FORMAT_DATE_TIME = "hh-dd-MM";

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    public static String formatTime(int timestamp, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp*1000);
        return DateFormat.format(format, cal).toString();
    }
}
