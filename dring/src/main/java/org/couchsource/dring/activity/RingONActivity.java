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

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.Constants;
import org.couchsource.dring.application.DevicePosition;
import org.couchsource.dring.application.R;
import org.couchsource.dring.legal.Disclaimer;
import org.couchsource.dring.service.SensorService;

/**
 * Main Activity of RingON. It manages 3 fragments for Face up, Face Down and In-pocket positions.
 * It also handles the main toggle switch that turns ON/OFF the Sensor Service.
 *
 * @author Kunal Sanghavi
 *
 */
public class RingONActivity extends Activity implements SettingsFragment.OnFragmentInteractionListener, Constants {

    private static final String TAG = RingONActivity.class.getName();
    private ApplicationContextWrapper context;
    private boolean firstRun = false;
    private boolean serviceOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = new ApplicationContextWrapper(this);
        decorateActionBar();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.FaceUpSettingFrame, SettingsFragment.newInstance(DevicePosition.FACE_UP.name()));
            Log.d(TAG,"Fragment "+ DevicePosition.FACE_UP.name()+" added");
            fragmentTransaction.add(R.id.FaceDownSettingFrame, SettingsFragment.newInstance(DevicePosition.FACE_DOWN.name()));
            Log.d(TAG,"Fragment "+ DevicePosition.FACE_DOWN.name()+" added");
            fragmentTransaction.add(R.id.InPocketSettingFrame, SettingsFragment.newInstance(DevicePosition.IN_POCKET.name()));
            Log.d(TAG,"Fragment "+ DevicePosition.IN_POCKET.name()+" added");
            fragmentTransaction.commit();
        }
        //check if the app is launched for the first time
        firstRun = context.getBooleanPreference(RING_ON, FIRST_RUN, true);
        //Service is supposed to be ON, but it may not - due to a crash or newer version of the app.
        serviceOn = context.getBooleanPreference(RING_ON, SENSOR_SERVICE_ON, false);
        Log.d(TAG,"Activity created. Running for the first time? "+firstRun);
    }

    @Override
    public ApplicationContextWrapper getActivityContext(){
        return context;
    }

    @Override
    public boolean isFirstLaunch() {
        return firstRun;
    }

    @Override
    public boolean isSensorServiceRunning() {
        return (SensorService.isServiceRunning() || serviceOn);
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
        if (firstRun){
            toggleSwitch.setChecked(true);
        }else{
            toggleSwitch.setChecked(isSensorServiceRunning());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.QuitApp:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (firstRun) {
            context.setBooleanPreference(RING_ON, FIRST_RUN, false);
        }
        new Disclaimer(this).show();
    }

    private void toggleRingerService(boolean isChecked) {
        if (isChecked != SensorService.isServiceRunning()){
            if (isChecked){
                startRingerService();
            }else{
                stopRingerService();
            }
            toggleFragmentControls(isChecked);
        }
    }

    private void decorateActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(R.string.action_bar_background));
    }

    private void toggleFragmentControls(boolean on) {
        getFragmentById(R.id.FaceUpSettingFrame).setEnabledFragmentView(on);
        getFragmentById(R.id.FaceDownSettingFrame).setEnabledFragmentView(on);
        getFragmentById(R.id.InPocketSettingFrame).setEnabledFragmentView(on);
    }

    private SettingsFragment getFragmentById(int id) {
        return (SettingsFragment) getFragmentManager().findFragmentById(id);
    }

    private void startRingerService() {
        Intent intent= new Intent(this, SensorService.class);
        this.startService(intent);
        Log.i(TAG, "Service Start Requested");
    }

    private void stopRingerService(){
        Intent intent= new Intent(this, SensorService.class);
        this.stopService(intent);
        Log.i(TAG, "Service Stopped");
    }
}