package org.couchsource.dring.listener.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.couchsource.dring.application.AppContextWrapper;
import org.couchsource.dring.service.DeviceStateListener;

/**
 * author Kunal
 */
public class LightSensorListener extends DeviceSensorEventListener {

    public LightSensorListener(AppContextWrapper contextWrapper,DeviceStateListener deviceStateListener){
        super(contextWrapper,deviceStateListener);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT){
            processEvent(event);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void processEvent(SensorEvent event) {
        if (event.values[0] < 10)
            getDeviceStateListener().registerDarkness();
        else
            getDeviceStateListener().registerIllumination();
    }

    public void register() {
        super.register(Sensor.TYPE_LIGHT);
    }
}
