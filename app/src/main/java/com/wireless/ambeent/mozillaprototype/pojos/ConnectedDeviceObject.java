package com.wireless.ambeent.mozillaprototype.pojos;

/**
 * Created by Atakan on 7.12.2017.
 */

public class ConnectedDeviceObject {

    private String macAddress;

    private String ipAddress;

    private boolean isThisTheUser; //Is that device the users device?

    public ConnectedDeviceObject(String macAddress, String ipAddress, boolean isThisTheUser) {
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.isThisTheUser = isThisTheUser;
    }


    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isThisTheUser() {
        return isThisTheUser;
    }

    public void setThisTheUser(boolean thisTheUser) {
        isThisTheUser = thisTheUser;
    }
}
