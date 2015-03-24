package org.couchsource.dring.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.Constants;
import org.couchsource.dring.application.DevicePositionHelper;
import org.couchsource.dring.application.DeviceProperty;
import org.couchsource.dring.application.DevicePosition;
import org.couchsource.dring.application.R;


/**
 * A simple {@link Fragment} representing settings for a device position.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Kunal Sanghavi
 *
 */
public class SettingsFragment extends Fragment implements Constants {

    private static final String TAG = SettingsFragment.class.getName();
    private static final float ENABLED = 1f;
    private static final float DISABLED = 0.4f;
    private OnFragmentInteractionListener mListener;
    private String devicePosition;
    private int ringerVolume;
    private boolean doVibrate;
    private boolean isFeatureActive;
    private CheckBox cbEnabled;
    private SeekBar sbVolumeControl;
    private View ringerIcon;
    private View ringtoneLabel;
    private CheckBox cbVibrateOnRing;


    /**
     * Do not use. Use newInstance(String devicePosition) instead.
     */
    public SettingsFragment() {
    }

    /**
     * creates a new instance of SettingsFragment
     * @param devicePosition
     * @return
     */
    public static SettingsFragment newInstance(String devicePosition) {
        if (TextUtils.isEmpty(devicePosition)) {
            throw new IllegalArgumentException("devicePosition is blank or null");
        }
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("devicePosition", devicePosition);
        settingsFragment.setArguments(bundle);
        return settingsFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        devicePosition = (String) getArguments().get("devicePosition");
        if (!DevicePosition.isPositionValid(devicePosition)){
            throw new IllegalArgumentException("Invalid device position detected "+ devicePosition);
        }

        final View mView = inflater.inflate(R.layout.settings_layout, container, false);
        sbVolumeControl = (SeekBar) mView.findViewById(R.id.seekBarRinger);
        ringerIcon =  mView.findViewById(R.id.RingerIcon);
        ringtoneLabel =  mView.findViewById(R.id.lblRingerVolume);
        sbVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateImage(seekBar, mView, R.id.RingerIcon, R.drawable.ic_action_ring_volume);
                ringerVolume = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveUserPreferences();
            }
        });
        ringerVolume = (int)mListener.getActivityContext().getFloatPreference(devicePosition, DeviceProperty.RINGER.name(), 50.0f);
        sbVolumeControl.setProgress(ringerVolume);
        updateImage(sbVolumeControl, mView, R.id.RingerIcon, R.drawable.ic_action_ring_volume);

        doVibrate = mListener.getActivityContext().getBooleanPreference(devicePosition, DeviceProperty.VIBRATE.name(), false);
        cbVibrateOnRing = (CheckBox) mView.findViewById(R.id.cbVibrate);
        cbVibrateOnRing.setChecked(doVibrate);

        cbEnabled = (CheckBox) mView.findViewById(R.id.cbEnabled);
        cbEnabled.setText(DevicePositionHelper.getResId(devicePosition));

        isFeatureActive = mListener.getActivityContext().getBooleanPreference(devicePosition, DeviceProperty.ACTIVE.name(), true);
        cbEnabled.setChecked(isFeatureActive);

        if (mListener.isFirstLaunch()){
            setEnabledFragmentView(true);
        }else{
            setEnabledFragmentView(mListener.isSensorServiceRunning());
        }

        cbEnabled.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFeatureActive = isChecked;
                setEnabledChildren(isChecked);
                saveUserPreferences();
            }
        });

        cbVibrateOnRing.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doVibrate = isChecked;
                saveUserPreferences();
            }
        });

        Log.d(TAG, "New view for phone status " + devicePosition + " created");
        return mView;
    }

    /**
     * Enable or disable fragment view
     * @param on
     */
    public void setEnabledFragmentView(boolean on) {
       setEnabled(cbEnabled, on);
       setEnabledChildren(isFeatureActive && on);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setEnabledChildren(boolean isEnabled) {
        setEnabled(sbVolumeControl, isEnabled);
        setEnabled(ringerIcon, isEnabled);
        setEnabled(ringtoneLabel, isEnabled);
        setEnabled(cbVibrateOnRing, isEnabled);
    }

    private void setEnabled(View view, boolean on) {
        view.setEnabled(on);
        if (on){
            view.setAlpha(ENABLED);
        }else{
            view.setAlpha(DISABLED);
        }
    }

    private void updateImage(SeekBar seekBar, View mView, int ringerIcon, int ic_action_ring_volume) {
        ImageView imageView = (ImageView) mView.findViewById(ringerIcon);
        Drawable drawable;
        if (seekBar.getProgress() == 0) {
            drawable = getResources().getDrawable(R.drawable.ic_action_volume_muted);
        } else {
            drawable = getResources().getDrawable(ic_action_ring_volume);
        }
        imageView.setImageDrawable(drawable);
    }

    private void saveUserPreferences(){
        SharedPreferences.Editor editor = mListener.getActivityContext().getSharedPreferences(devicePosition, Context.MODE_PRIVATE).edit();
        editor.putBoolean(DeviceProperty.ACTIVE.name(), isFeatureActive);
        editor.putFloat(DeviceProperty.RINGER.name(), ringerVolume);
        editor.putBoolean(DeviceProperty.VIBRATE.name(), doVibrate);
        Log.d(TAG, "user pref saved for "+devicePosition);
        editor.apply();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        public ApplicationContextWrapper getActivityContext();
        public boolean isFirstLaunch();
        public boolean isSensorServiceRunning();
    }
}
