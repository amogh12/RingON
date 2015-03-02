package org.couchsource.dring.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.couchsource.dring.application.AppContextWrapper;
import org.couchsource.dring.application.DeviceStatus;
import org.couchsource.dring.application.R;
import org.couchsource.dring.legal.DringDisclaimer;
import org.couchsource.dring.service.SensorService;


public class DringActivity extends Activity implements SettingsFragment.OnFragmentInteractionListener {

    private static final String TAG = DringActivity.class.getName();
    private AppContextWrapper activityContextWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContextWrapper = new AppContextWrapper(this);
        decorateActionBar();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.Frame1, SettingsFragment.newInstance(DeviceStatus.FACE_UP.name()));
            Log.d(TAG,"Fragment "+ DeviceStatus.FACE_UP.name()+" added");
            fragmentTransaction.add(R.id.Frame2, SettingsFragment.newInstance(DeviceStatus.FACE_DOWN.name()));
            Log.d(TAG,"Fragment "+ DeviceStatus.FACE_DOWN.name()+" added");
            fragmentTransaction.add(R.id.Frame3, SettingsFragment.newInstance(DeviceStatus.IN_POCKET.name()));
            Log.d(TAG,"Fragment "+ DeviceStatus.IN_POCKET.name()+" added");
            fragmentTransaction.commit();
        }
        Log.d(TAG,"Activity created");
    }

    @Override
    public AppContextWrapper getActivityContext(){
        return activityContextWrapper;
    }

    private void decorateActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(R.string.action_bar_background));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.ActionItem);
        menuItem.setActionView(R.layout.on_off_switch_layout);

        ToggleButton toggleSwitch = (ToggleButton)menuItem.getActionView().findViewById(R.id.toggleButton);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleRingerService(isChecked);
            }
        });
        toggleSwitch.setChecked(SensorService.isServiceRunning());
        return true;
    }

    private void toggleRingerService(boolean isChecked) {
        if (isChecked != SensorService.isServiceRunning()){
            if (isChecked){
                startRingerService();
            }else{
                stopRingerService();
            }
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        new DringDisclaimer(this).show();
    }

    private void startRingerService() {
        Intent intent= new Intent(this, SensorService.class);
        this.startService(intent);
        Log.i(TAG, "Service Started");
    }

    private void stopRingerService(){
        Intent intent= new Intent(this, SensorService.class);
        this.stopService(intent);
        Log.i(TAG, "Service Stopped");
    }
}
