package klara.lookbook.utils;

import android.content.Context;
import android.util.TypedValue;

public class Formater {

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }
}
