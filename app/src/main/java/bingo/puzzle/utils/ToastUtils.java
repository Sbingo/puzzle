package bingo.puzzle.utils;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import bingo.puzzle.R;


/**
 * Toast工具类
 */
public class ToastUtils {

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }

    public static void toastTop(Context context,String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    private static CountDownTimer toastCountDown;
    public static Toast toast;
    private static View toastLayout;
    private static TextView toastText;
    private static Activity savedActivity;

    /**
     * @param activity
     * @param content
     * @return
     */
    public static <T extends Activity> Toast displayCustomToast(T activity, String content, int position) {
        if (savedActivity == null || (savedActivity != null && !savedActivity.equals(activity))) {
            savedActivity = activity;
            toastLayout = LayoutInflater.from(activity).inflate(R.layout.custom_toast, (ViewGroup) savedActivity.findViewById(R.id.toast_layout_root));
            toastText = (TextView) toastLayout.findViewById(R.id.text);
            toast = new Toast(savedActivity);
            toast.setGravity(position, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            if (toast != null) {
                toast.cancel();
            }
        }
        toastText.setText(content);
        toast.setView(toastLayout);
        if (toastCountDown != null) {
            toastCountDown.cancel();
        }
        toastCountDown = new CountDownTimer(1500, 500 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            public void onFinish() {
                toast.cancel();
            }
        };
        // Show the toast and starts the countdown
        toast.show();
        toastCountDown.start();
        return toast;
    }
}
