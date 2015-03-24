package org.couchsource.dring.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
    ToggleButton toggleBtn;
    private boolean firstRun = false;
    private boolean eulaHasBeenShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = new ApplicationContextWrapper(this);
        decorateActionBar();
        setContentView(R.layout.activity_main);
        addSettingFragments();
        checkEulaAccepted();
        //check if the app is launched for the first time
        firstRun = context.getBooleanPreference(RING_ON, FIRST_RUN, true);
        //Service is supposed to be ON, but it may not - due to a crash or newer version of the app.
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
        return (SensorService.isServiceRunning() || context.getBooleanPreference(RING_ON, SENSOR_SERVICE_ON, false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.ActionItem);
        menuItem.setActionView(R.layout.on_off_switch_layout);

        toggleBtn = (ToggleButton)menuItem.getActionView().findViewById(R.id.toggleButton);
        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleRingerService(isChecked);
            }
        });
        toggleBtn.setChecked(isSensorServiceRunning());
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
    }

    private void addSettingFragments() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.FaceUpSettingFrame, SettingsFragment.newInstance(DevicePosition.FACE_UP.name()));
        Log.d(TAG, "Fragment " + DevicePosition.FACE_UP.name() + " added");
        fragmentTransaction.add(R.id.FaceDownSettingFrame, SettingsFragment.newInstance(DevicePosition.FACE_DOWN.name()));
        Log.d(TAG,"Fragment "+ DevicePosition.FACE_DOWN.name()+" added");
        fragmentTransaction.add(R.id.InPocketSettingFrame, SettingsFragment.newInstance(DevicePosition.IN_POCKET.name()));
        Log.d(TAG,"Fragment "+ DevicePosition.IN_POCKET.name()+" added");
        fragmentTransaction.commit();
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

    //EULA stuff below

    private void checkEulaAccepted() {
        PackageInfo versionInfo = getPackageInfo();
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = "eula_" + versionInfo.versionCode;
        eulaHasBeenShown = context.getBooleanPreference(RING_ON, eulaKey, false);
        if(!eulaHasBeenShown) {
            showEula(versionInfo, eulaKey);
        }
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    private void showEula(PackageInfo versionInfo, final String eulaKey) {
            String title = getString(R.string.app_name) + " v" + versionInfo.versionName;
            String message = getString(R.string.app_updates) + "\n\n" + getString(R.string.eula);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.setBooleanPreference(RING_ON,eulaKey,true);
                            context.setBooleanPreference(RING_ON, FIRST_RUN, false);
                            toggleBtn.setChecked(true);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    });
            builder.create().show();
    }

}
