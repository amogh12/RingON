package org.couchsource.dring.application;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Enum for all device positions detected by this app
 *
 * @author Kunal Sanghavi
 */
public enum DevicePosition {

    FACE_UP("FACE_UP", true),
    FACE_DOWN("FACE_DOWN", true),
    IN_POCKET("IN_POCKET", true),
    UNKNOWN("UNKNOWN", false);

    private final String label;

    private final boolean userPreferred;

    DevicePosition(String label, boolean userPreferred){
        this.label = label;
        this.userPreferred = userPreferred;
    }

    public String getLabel(){
        return label;
    }

    /**
     * Returns the {@link org.couchsource.dring.application.DevicePosition} enum from its label
     * @param label required Device Position in String
     * @return {@link org.couchsource.dring.application.DevicePosition}
     */
    public static DevicePosition positionFromLabel(String label){
        if (TextUtils.isEmpty(label)){
            return null;
        }
        for (DevicePosition devicePosition : DevicePosition.values()){
            if (devicePosition.getLabel().equalsIgnoreCase(label)){
                return devicePosition;
            }
        }
        return null;
    }

    public static boolean isDevicePositionValid(String position){
        DevicePosition devicePosition = positionFromLabel(position);
        if (devicePosition == null){
            return false;
        }
        if (devicePosition.isUserPreferredPosition()){
            return true;
        }
        return false;
    }

    public static Collection<DevicePosition> getAllUserPreferredPositions(){
        Collection<DevicePosition> deviceStatuses = new ArrayList<>();
        for (DevicePosition devicePosition : DevicePosition.values()){
            if (devicePosition.userPreferred){
                deviceStatuses.add(devicePosition);
            }
        }
        return Collections.unmodifiableCollection(deviceStatuses);
    }

    public boolean isUserPreferredPosition(){
        if (this.userPreferred){
            return true;
        }
        return false;
    }

}
