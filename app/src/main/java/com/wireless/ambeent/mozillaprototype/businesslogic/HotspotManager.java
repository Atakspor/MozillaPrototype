package com.wireless.ambeent.mozillaprototype.businesslogic;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

//This class is responsible for creating and disabling hotspots, scanning and detecting the hotspots that are created by the application.
public class HotspotManager {

    private Context mContext;

    //Contains the list of the hotspots that are created by the app
    private List<String> mScannedHotspotSsidList = new ArrayList<>();

    public HotspotManager(Context mContext) {
        this.mContext = mContext;
    }

    //Creates a hotspot
    public void createHotspot(){

        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "MyDummySSID";

    }

    public List<String> getmScannedHotspotSsidList() {
        return mScannedHotspotSsidList;
    }
}
