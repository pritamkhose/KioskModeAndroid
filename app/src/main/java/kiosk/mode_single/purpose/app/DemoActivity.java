package kiosk.mode_single.purpose.app;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import kiosk.mode_single.purpose.app.utils.BaseActivity;
import kiosk.mode_single.purpose.app.utils.MySharedPreferences;
import kiosk.mode_single.purpose.app.utils.SettingFragment;

public class DemoActivity extends BaseActivity {
    private static final String TAG = DemoActivity.class.getSimpleName();
    final private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //to remove "information bar" above the action bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //to remove the action bar (title bar)
        getSupportActionBar().hide();

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DemoActivity.this, NextActivity.class));
            }
        });
        findViewById(R.id.lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingsDialog();
            }
        });
        setUpKioskMode();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!kioskMode.isLocked(this)) {
            super.onBackPressed();
        }
    }

    private void setUpKioskMode() {
        if (!MySharedPreferences.isAppLaunched(this)) {
            Log.d(TAG, "onCreate() locking the app first time");
            kioskMode.lockUnlock(this, true);
            MySharedPreferences.saveAppLaunched(this, true);
        } else {
            //check if app was locked
            if (MySharedPreferences.isAppInKioskMode(this)) {
                Log.d(TAG, "onCreate() locking the app");
                kioskMode.lockUnlock(this, true);
            }
        }
    }

    /**
     * show settings dialog
     */
    private void showSettingsDialog() {
        SettingFragment settingFragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putBoolean(SettingFragment.LOCKED_BUNDLE_KEY, kioskMode.isLocked(this));
        settingFragment.setArguments(args);
        settingFragment.show(fragmentManager, settingFragment.getClass().getSimpleName());
        settingFragment.setActionHandler(new SettingFragment.IActionHandler() {
            @Override
            public void isLocked(boolean isLocked) {
                int msg = isLocked ? R.string.setting_device_locked : R.string.setting_device_unlocked;
                kioskMode.lockUnlock(DemoActivity.this, isLocked);
                Toast.makeText(DemoActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume()
    {
        super.onResume();

        if (Build.VERSION.SDK_INT < 16)
        {
            // Hide the status bar
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // Hide the action bar
            getSupportActionBar().hide();
        }
        else
        {
            // Hide the status bar
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // Hide the action bar
            getSupportActionBar().hide();
        }
    }

/*    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }*/

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        ((ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);

    }


    // https://stackoverflow.com/questions/12950215/onkeydown-and-onkeylongpress
    boolean flag = false;

    boolean flag2 = false;


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.d("onKeyLongPress", String.valueOf(keyCode));
            flag = false;
            flag2 = true;
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.d("onKeyDown", String.valueOf(keyCode));
            event.startTracking();
            if (flag2 == true) {
                flag = false;
            } else {
                flag = true;
                flag2 = false;
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

            event.startTracking();
            if (flag) {
                Log.d("onKeyUp_VOLUME_DOWN", String.valueOf(keyCode));
            }
            flag = true;
            flag2 = false;
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}
