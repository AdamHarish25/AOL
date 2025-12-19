package com.aniketjain.weatherapp.toast;

import android.content.Context;

import com.aniketjain.roastedtoast.Toasty;
import com.aniketjain.weatherapp.R;

public class Toaster {
    public static void successToast(Context context, String msg) {
        Toasty.custom(
                context,
                msg,
                R.drawable.ic_baseline_check_24,
                context.getResources().getColor(R.color.blockBGColor),
                14,
                context.getResources().getColor(R.color.textColor));
    }

    public static void errorToast(Context context, String msg) {
        Toasty.custom(
                context,
                msg,
                R.drawable.ic_baseline_error_outline_24,
                context.getResources().getColor(R.color.blockBGColor),
                14,
                context.getResources().getColor(R.color.textColor));
    }
}
