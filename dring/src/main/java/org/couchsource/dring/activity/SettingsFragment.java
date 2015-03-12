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

import org.couchsource.dring.application.AppContextWrapper;
import org.couchsource.dring.application.Constants;
import org.couchsource.dring.application.DeviceProperty;
import org.couchsource.dring.application.DeviceStatus;
import org.couchsource.dring.application.DeviceStatusHelper;
import org.couchsource.dring.application.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingsFragment extends Fragment implements Constants {

    private static final String TAG = SettingsFragment.class.getName();
    public static final float ENABLED = 1f;
    public static final float DISABLED = 0.4f;
    private OnFragmentInteractionListener mListener;
    private DeviceStatus deviceStatus;
    private int ringerVolume;
    private boolean doVibrate;
    private boolean isFeatureActive;
    private CheckBox cbEnabled;
    private SeekBar mVolumeControl;
    private View ringerIcon;
    private View ringtoneLabel;
    private CheckBox cbVibrateOnRing;


    /**
     * Do not use.
     *
     * @see {@link }
     */
    public SettingsFragment() {
    }

    public static SettingsFragment newInstance(String mPhoneStatus) {
        if (TextUtils.isEmpty(mPhoneStatus)) {
            throw new IllegalArgumentException("mPhoneStatus is blank or null");
        }
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("mPhoneStatus", mPhoneStatus);
        settingsFragment.setArguments(bundle);
        return settingsFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        deviceStatus = DeviceStatus.phoneStatusFromLabel((String) getArguments().get("mPhoneStatus"));
        if (deviceStatus == null) {
            throw new IllegalArgumentException("phoneStatus is null or blank");
        }
        final View mView = inflater.inflate(R.layout.settings_layout, container, false);
        mVolumeControl = (SeekBar) mView.findViewById(R.id.seekBarRinger);
        ringerIcon =  mView.findViewById(R.id.RingerIcon);
        ringtoneLabel =  mView.findViewById(R.id.lblRingerVolume);
        mVolumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        ringerVolume = (int)mListener.getActivityContext().getFloatSharedPref(deviceStatus.name(), DeviceProperty.RINGER.name(), 50.0f);
        mVolumeControl.setProgress(ringerVolume);
        updateImage(mVolumeControl, mView, R.id.RingerIcon, R.drawable.ic_action_ring_volume);

        doVibrate = mListener.getActivityContext().getBooleanSharedPref(deviceStatus.name(), DeviceProperty.VIBRATE.name(), false);
        cbVibrateOnRing = (CheckBox) mView.findViewById(R.id.cbVibrate);
        cbVibrateOnRing.setChecked(doVibrate);

        cbEnabled = (CheckBox) mView.findViewById(R.id.cbEnabled);
        cbEnabled.setText(DeviceStatusHelper.getResId(deviceStatus));

        isFeatureActive = mListener.getActivityContext().getBooleanSharedPref(deviceStatus.name(), DeviceProperty.ACTIVE.name(), true);
        cbEnabled.setChecked(isFeatureActive);

        if (mListener.isFirstLaunch()){
            setEnabledFragmentView(true);
        }else{
            setEnabledFragmentView(mListener.isRingerServiceRunning());
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

        Log.d(TAG, "New view for phone status " + deviceStatus.name() + " created");
        return mView;
    }


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
        setEnabled(mVolumeControl, isEnabled);
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

    private void saveUserPreferences() {
        SharedPreferences.Editor editor = mListener.getActivityContext().getSharedPreferences(deviceStatus.name(), Context.MODE_PRIVATE).edit();
        editor.putBoolean(DeviceProperty.ACTIVE.name(), isFeatureActive);
        editor.putFloat(DeviceProperty.RINGER.name(), ringerVolume);
        editor.putBoolean(DeviceProperty.VIBRATE.name(), doVibrate);
        editor.apply();
        Log.d(TAG, "User pref for " + deviceStatus.name() + " saved!");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public AppContextWrapper getActivityContext();
        public boolean isFirstLaunch();
        public boolean isRingerServiceRunning();
    }


}
