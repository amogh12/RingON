package org.couchsource.dring.application;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Enum for all device positions detected by this app
 * @author Kunal Sanghavi
 */
public enum DeviceStatus {

    FACE_UP("FACE_UP", true),
    FACE_DOWN("FACE_DOWN", true),
    IN_POCKET("IN_POCKET", true),
    UNKNOWN("UNKNOWN", false);

    private final String label;

    private final boolean userPreference;

    DeviceStatus(String label, boolean userPreference){
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
    public static DeviceStatus phoneStatusFromLabel(String label){
        if (TextUtils.isEmpty(label)){
            return null;
        }
        for (DeviceStatus deviceStatus : DeviceStatus.values()){
            if (deviceStatus.getLabel().equalsIgnoreCase(label)){
                return deviceStatus;
            }
        }
        return null;
    }

    public static Collection<DeviceStatus> getAllUserPreferences(){
        Collection<DeviceStatus> deviceStatuses = new ArrayList<>();
        for (DeviceStatus deviceStatus : DeviceStatus.values()){
            if (deviceStatus.userPreference){
                deviceStatuses.add(deviceStatus);
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
