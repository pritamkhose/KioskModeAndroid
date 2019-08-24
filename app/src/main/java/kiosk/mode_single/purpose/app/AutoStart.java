package kiosk.mode_single.purpose.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
    public static final String PREFS_NAME = "MyPrefsFile";


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d("boot completed", "boot completed caught");
            Boolean autoRestart = false;
            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            autoRestart = sp.getBoolean("autoRestart", false);

            if (autoRestart){

                Log.d("boot completed", "auto restart true");

                Intent i = new Intent(context, DemoActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            } else {
                Log.d("boot completed", "auto restart false");
            }
        }
    }

}