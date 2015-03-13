package org.couchsource.dring.service;

import android.util.Log;

import org.couchsource.dring.application.model.Device;
import org.couchsource.dring.application.DevicePosition;

/**
 * StatusManager registers new status of the device.
 *
 * @author Kunal Sanghavi
 */
public class DeviceStateListener {

    private static final String TAG = DeviceStateListener.class.getName();
    //Accelerometer readings
    private static final int MAX_DELAY_COUNT = 3;
    private final Device device;
    private final DeviceStateListenerCallback deviceStateListenerCallback;
    private Integer faceUpDelayCounter = 0;
    private Integer faceDownDelayCounter = 0;
    private Integer unknownPositionCounter = 0;

    public DeviceStateListener(DeviceStateListenerCallback deviceStateListenerCallback) {
        if (deviceStateListenerCallback == null) {
            throw new IllegalArgumentException("deviceStateListenerCallback found null");
        }
        this.deviceStateListenerCallback = deviceStateListenerCallback;
        this.device = new Device();
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
            deviceStateListenerCallback.signalDeviceProximityChanged();

    }

    /**
     * Registers if device is no longer in close proximity to any external entity
     */
    public synchronized void registerDistantProximity() {
        device.registerDistantProximity();
        deviceStateListenerCallback.signalDeviceProximityChanged();
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
        //Avoiding false positives
        if ((devicePosition == DevicePosition.IN_POCKET) && (!deviceStateListenerCallback.getContext().isAudioNormalMode())) {
            return;
        }
        deviceStateListenerCallback.signalNewDevicePlacement(devicePosition);
        Log.d(TAG, "Registered Device status " + devicePosition);

    }
}
