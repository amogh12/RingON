package org.couchsource.dring.application;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Enum for all device positions detected by this app
 * @author Kunal Sanghavi
 */
public enum DevicePosition {

    FACE_UP("FACE_UP", true),
    FACE_DOWN("FACE_DOWN", true),
    IN_POCKET("IN_POCKET", true),
    UNKNOWN("UNKNOWN", false);

    private final String label;

    private final boolean userPreference;

    DevicePosition(String label, boolean userPreference){
        this.label = label;
        this.userPreference = userPreference;
    }

    public String getLabel(){
        return label;
    }

    /**
     * Returns the PhoneStatus enum from its label
     * @param label required PhoneStatus in String form
     * @return PhoneStatus
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

    public static Collection<DevicePosition> getAllUserPreferences(){
        Collection<DevicePosition> deviceStatuses = new ArrayList<>();
        for (DevicePosition devicePosition : DevicePosition.values()){
            if (devicePosition.userPreference){
                deviceStatuses.add(devicePosition);
            }
        }
        return Collections.unmodifiableCollection(deviceStatuses);
    }

    public boolean isStatusValid(){
        if (this.userPreference){
            return true;
        }
        return false;
    }

}
