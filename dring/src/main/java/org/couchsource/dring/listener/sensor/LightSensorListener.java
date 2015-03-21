package org.couchsource.dring.listener.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.couchsource.dring.service.SensorEventsAggregator;

/**
 * Registrable for the Light Sensor on a device.
 *
 * author Kunal Sanghavi
 */
public class LightSensorListener extends DeviceSensorEventListener {

    /**
     * Creates a new instance of the listener
     * @param sensorEventsAggregator required {@link org.couchsource.dring.service.SensorEventsAggregator}
     */
    public LightSensorListener(SensorEventsAggregator sensorEventsAggregator){
        super(sensorEventsAggregator);
    }


    @Override
    public void register() {
        super.register(Sensor.TYPE_LIGHT);
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
            getSensorEventsAggregator().registerDarkness();
        else
            getSensorEventsAggregator().registerIllumination();
    }
}
