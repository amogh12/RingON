package org.couchsource.dring.service;

import android.util.Log;

import org.couchsource.dring.application.ApplicationContextWrapper;
import org.couchsource.dring.application.model.Device;
import org.couchsource.dring.application.DevicePosition;
import org.couchsource.dring.service.callback.CallbackForSensorEventsAggregator;

/**
 * Aggregates all events from Accelerometer, Light and Proximity Sensors
 * and informs the SensorService of new device positions
 *
 * @author Kunal Sanghavi
 */
public class SensorEventsAggregator {

    private static final String TAG = SensorEventsAggregator.class.getName();
    private static final int MAX_DELAY_COUNT = 3;
    private final Device device;
    private final CallbackForSensorEventsAggregator callback;
    private Integer faceUpDelayCounter = 0;
    private Integer faceDownDelayCounter = 0;
    private Integer unknownPositionCounter = 0;

    /**
     * Create a new SensorEventAggregator
     *
     * @param callback required Callback to SensorService
     */
    public SensorEventsAggregator(CallbackForSensorEventsAggregator callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback found null");
        }
        this.callback = callback;
        this.device = new Device();
    }

    public ApplicationContextWrapper getContext(){
        return callback.getContext();
    }

    /**
     * Registers device status to "Face Up"
     */
    public synchronized void registerFaceUp() {
        unknownPositionCounter = 0;
        faceDownDelayCounter = 0;
        if (faceUpDelayCounter < MAX_DELAY_COUNT) {
            faceUpDelayCounter++;
        }
        if (faceUpDelayCounter == MAX_DELAY_COUNT) {
            device.registerFaceUp();
            faceUpDelayCounter = 0;
            Log.d(TAG, "Face up signalled");
            examineDeviceStatus();
        }
    }

    /**
     * Registers device status to "Face Down"
     */
    public synchronized void registerFaceDown() {
        unknownPositionCounter = 0;
        faceUpDelayCounter = 0;
        if (faceDownDelayCounter < MAX_DELAY_COUNT) {
            faceDownDelayCounter++;
        }
        if (faceDownDelayCounter == MAX_DELAY_COUNT) {
            device.registerFaceDown();
            faceDownDelayCounter = 0;
            Log.d(TAG, "Face down signalled");
            examineDeviceStatus();
        }
    }

    /**
     * If device is neither face up or face down.
     */
    public synchronized void registerNeitherFaceUpNorFaceDown() {
        faceUpDelayCounter = 0;
        faceDownDelayCounter = 0;
        if (unknownPositionCounter < MAX_DELAY_COUNT) {
            unknownPositionCounter++;
        }
        if (unknownPositionCounter == MAX_DELAY_COUNT) {
            device.registerUnknownState();
            Log.d(TAG, "Neither FaceUp nor FaceDown signalled");
            unknownPositionCounter = 0;
            examineDeviceStatus();
        }
    }

    /**
     * Registers if device is in close proximity to any external entity.
     */
    public synchronized void registerCloseProximity() {
        device.registerCloseProximity();
        callback.signalDeviceProximityChanged();

    }

    /**
     * Registers if device is no longer in close proximity to any external entity
     */
    public synchronized void registerDistantProximity() {
        device.registerDistantProximity();
        callback.signalDeviceProximityChanged();
    }

    /**
     * Registers if device is in dark area
     */
    public synchronized void registerDarkness() {
        device.registerDarkness();
    }

    /**
     * Registers if device is in bright area
     */
    public synchronized void registerIllumination() {
        device.registerIllumination();
    }

    private void examineDeviceStatus() {
        DevicePosition devicePosition = device.getCurrentPosition();
        //Avoiding false positives for in pocket position
        if ((devicePosition == DevicePosition.IN_POCKET) && (!callback.getContext().isAudioNormalMode())) {
            Log.d(TAG, "Possible false positive detected for in-pocket position");
            return;
        }
        callback.signalNewPosition(devicePosition);
        Log.d(TAG, "Registered Device status " + devicePosition);

    }
}
