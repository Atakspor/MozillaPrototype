package com.wireless.ambeent.mozillaprototype.businesslogic;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.wireless.ambeent.mozillaprototype.hotspot.ClientScanResult;
import com.wireless.ambeent.mozillaprototype.hotspot.FinishScanListener;
import com.wireless.ambeent.mozillaprototype.hotspot.WifiApManager;
import com.wireless.ambeent.mozillaprototype.pojos.ConnectedDeviceObject;

import java.util.ArrayList;
import java.util.List;

//This class is responsible for creating and disabling hotspots, scanning and detecting the hotspots that are created by the application.
public class HotspotController {

    private static final String TAG = "HotspotController";


    private Context mContext;
    private List<ConnectedDeviceObject> mConnDevObjList;

    private WifiApManager mWifiApManager;

    //Contains the list of the hotspots that are created by the app
    private List<String> mScannedHotspotSsidList = new ArrayList<>();

    public HotspotController(Context mContext, List<ConnectedDeviceObject> mConnDevObjList) {
        this.mContext = mContext;
        this.mConnDevObjList = mConnDevObjList;

        mWifiApManager = new WifiApManager(mContext);

     //   mWifiApManager.showWritePermissionSettings(true);


        mWifiApManager.getClientList(false, new FinishScanListener() {



            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {
                StringBuilder a = new StringBuilder();
                a.append("WifiApState: " + mWifiApManager.getWifiApState() + "\n\n");
                a.append("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
                    a.append("####################\n");
                    a.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    a.append("Device: " + clientScanResult.getDevice() + "\n");
                    a.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    a.append("isReachable: " + clientScanResult.isReachable() + "\n");
                }
                Log.i(TAG, "onFinishScan: " + a);
            }
        });

        createHotspot();

    }

    //Creates a hotspot
    public void createHotspot(){

        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "MyDummySSID";
        mWifiApManager.setWifiApEnabled(wifiConfiguration, true);
    }

    //Pings the network and sets the Connected Device list accordingly
    public void pingTheNetwork(){

        WifiPinger wifiPinger = new WifiPinger(mContext, mConnDevObjList);
        wifiPinger.startScanning();
    }

    public List<String> getmScannedHotspotSsidList() {
        return mScannedHotspotSsidList;
    }
}
