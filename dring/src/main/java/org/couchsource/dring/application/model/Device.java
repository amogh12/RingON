package org.couchsource.dring.application.model;

import org.couchsource.dring.application.DevicePosition;

/**
 * A bean that registers all sensor readings and spits out current position of the device.
 *
 * @author Kunal Sanghavi
 */

public class Device {

    private static final String TAG = Device.class.getName();
    private Boolean isFaceUp;
    private Boolean isFaceDown;
    private Boolean isCloseProximity;
    private Boolean isDark;

    /**
     * Create a new instance
     */
    public Device() {
        isFaceUp = null;
        isFaceDown = null;
        isCloseProximity = null;
        isDark = null;
    }

    /**
     * Registers face up signal
     */
    public void registerFaceUp() {
        this.setFaceUp(true);
    }

    /**
     * Registers face down signal
     */
    public void registerFaceDown() {
        this.setFaceDown(true);
    }

    /**
     * Registers unknown position
     */
    public void registerUnknownState() {
        this.setFaceUp(false);
        this.setFaceDown(false);
    }

    /**
     * Registers if the device is in close proximity of an external entity
     */
    public void registerCloseProximity() {
        this.setProximity(true);
    }

    /**
     * Registers if the device is NOY in close proximity of any external entity
     */
    public void registerDistantProximity() {
        this.setProximity(false);
    }

    /**
     * Registers if the device is in dark area.
     */
    public void registerDarkness() {
        this.setDark(true);
    }

    /**
     * Registers if the device is in brightly lit area.
     */
    public void registerIllumination() {
        this.setDark(false);
    }

    /**
     * Returns current position of device.
     * @return DevicePosition
     */
    public DevicePosition getCurrentPosition() {
        if (!isReady())
            return null;

        if (isFaceUp()) {
            return (DevicePosition.FACE_UP);
        } else if (isFaceDown() && isCloseProximity()) {
            return (DevicePosition.FACE_DOWN);
        } else if (isDark() && isCloseProximity()
                && (!isFaceDown()) && (!isFaceUp())) {
            return (DevicePosition.IN_POCKET);
        }
        return DevicePosition.UNKNOWN;
    }

    @Override
    public String toString() {
        return "Device{" +
                "isFaceUp=" + isFaceUp +
                ", isFaceDown=" + isFaceDown +
                ", isCloseProximity=" + isCloseProximity +
                ", isDark=" + isDark +
                '}';
    }

    private Boolean isCloseProximity() {
        return isCloseProximity;
    }

    private Boolean isDark() {
        return isDark;
    }

    private Boolean isFaceUp() {
        return isFaceUp;
    }

    private Boolean isFaceDown() {
        return isFaceDown;
    }

    private boolean isReady() {
        return ((isFaceUp != null) && (isFaceDown != null) && (isCloseProximity != null) && (isDark != null));

    }

    private void setFaceUp(boolean isFaceUp) {
        this.isFaceUp = isFaceUp;
        if (isFaceUp) {
            this.isFaceDown = !isFaceUp;
        }
    }

    private void setFaceDown(boolean isFaceDown) {
        this.isFaceDown = isFaceDown;
        if (isFaceDown) {
            this.isFaceUp = !isFaceDown;
        }
    }

    private void setProximity(boolean isCloseProximity) {
        this.isCloseProximity = isCloseProximity;
    }

    private void setDark(boolean isDark) {
        this.isDark = isDark;
    }

}
