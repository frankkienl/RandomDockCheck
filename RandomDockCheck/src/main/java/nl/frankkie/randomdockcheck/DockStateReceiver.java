package nl.frankkie.randomdockcheck;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by fbouwens on 5/31/13.
 */
public class DockStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MainActivity.thisAct != null) {
            MainActivity.thisAct.refreshUI();
        }

        //Settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().equals(Intent.ACTION_DOCK_EVENT)) {
            if (preferences.getBoolean("change_ime_dock", true)) {
                ((InputMethodManager) context.getSystemService("input_method")).showInputMethodPicker();
            }
        } else {
            if (preferences.getBoolean("change_ime_charge", true)) {
                ((InputMethodManager) context.getSystemService("input_method")).showInputMethodPicker();
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("State Changed");
        String content = "eh idk";
        if (intent.getAction().equals(Intent.ACTION_DOCK_EVENT)) {
            content = getDockState(context, intent);
        } else {
            content = getChargingState(context, intent);
        }
        builder.setContentText(content);
        // Creates an Intent for the Activity
        Intent notifyIntent =
                new Intent(Intent.makeMainActivity(new ComponentName(context, MainActivity.class)));
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Puts the PendingIntent into the notification builder
        builder.setContentIntent(pendingIntent);
        builder.build();


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        if ((intent.getAction().equals(Intent.ACTION_DOCK_EVENT) && preferences.getBoolean("notify_dock", true))
                || (!intent.getAction().equals(Intent.ACTION_DOCK_EVENT) && preferences.getBoolean("notify_charge", true))) {
            mNotificationManager.notify(1, builder.build());
        }
    }

    protected String getDockState(Context context, Intent intent) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_DOCK_EVENT);
        Intent dockStatus = intent; //context.registerReceiver(null, intentFilter);
        int dockState = dockStatus.getIntExtra(Intent.EXTRA_DOCK_STATE, -1);
        boolean isDocked = dockState != Intent.EXTRA_DOCK_STATE_UNDOCKED;
        StringBuilder sb = new StringBuilder();
        sb.append("Dock State:\nis docked: ").append(isDocked);
        return sb.toString();
    }

    protected String getChargingState(Context c, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        ////
        StringBuilder sb = new StringBuilder();
        sb.append("Battery State:\nis charging: ").append(isCharging);
        return sb.toString();
    }

}
