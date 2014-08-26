package com.zjut.bluetoothle;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


public class DialogFactory {

    //带bar的提示框
    public static Dialog creatRequestDialog(final Context context, String tip) {

        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.layout_dialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int width = getScreenWidth(context);
        lp.width = (int) (0.6 * width);

        TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
        if (tip == null || tip.length() == 0) {
            titleTxtv.setText(R.string.sending_request);
        } else {
            titleTxtv.setText(tip);
        }
        return dialog;
    }

	/*public static void ToastDialog(Context context, String title, String msg) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
				.setPositiveButton("确定", null).create().show();
	}*/

    //不带bar的提示框
    public static Dialog createToastDialog(final Context context, String tip)
    {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.layout_toast);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int width = getScreenWidth(context);
        lp.width = (int) (0.8 * width);

        TextView titleTxtv = (TextView) dialog.findViewById(R.id.tvLoad);
        if (tip == null || tip.length() == 0) {
            titleTxtv.setText(R.string.sending_request);
        } else {
            titleTxtv.setText(tip);
        }

        return dialog;
    }

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public static float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(dm);
            return dm.density;
        } catch (Exception ex) {

        }
        return 1.0f;
    }
}
