package org.couchsource.dring.application.model;


import org.couchsource.dring.application.DeviceStatus;

/**
 * A bean that holds device status.
 *
 * @author Kunal Sanghavi
 */

public class Device {

    private static final String TAG = Device.class.getName();
    private Boolean isFaceUp;
    private Boolean isFaceDown;
    private Boolean isCloseProximity;
    private Boolean isDark;

    public Device() {

        isFaceUp = null;
        isFaceDown = null;
        isCloseProximity = null;
        isDark = null;
    }

    public void registerFaceUp() {
        this.setFaceUp(true);
    }

    public void registerFaceDown() {
        this.setFaceDown(true);
    }

    public void registerUnknownState() {
        this.setFaceUp(false);
        this.setFaceDown(false);
    }

    public void registerCloseProximity() {
        this.setProximity(true);
    }

    public void registerDistantProximity() {
        this.setProximity(false);
    }

    public void registerDarkness() {
        this.setDark(true);
    }


    public void registerIllumination() {
        this.setDark(false);
    }

    public DeviceStatus getCurrentStatus() {
        if (!isReady())
            return null;

        if (isFaceUp()) {
            return (DeviceStatus.FACE_UP);
        } else if (isFaceDown() && isCloseProximity()) {
            return (DeviceStatus.FACE_DOWN);
        } else if (isDark() && isCloseProximity()
                && (!isFaceDown()) && (!isFaceUp())) {
            return (DeviceStatus.IN_POCKET);
        }
        return DeviceStatus.UNKNOWN;
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
