package com.wireless.ambeent.mozillaprototype.businesslogic;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.wireless.ambeent.mozillaprototype.hotspot.ClientScanResult;
import com.wireless.ambeent.mozillaprototype.hotspot.FinishScanListener;
import com.wireless.ambeent.mozillaprototype.hotspot.WifiApManager;
import com.wireless.ambeent.mozillaprototype.pojos.ConnectedDeviceObject;
import com.wireless.ambeent.mozillaprototype.server.ServerController;

import java.util.ArrayList;
import java.util.List;

//This class is responsible for creating and disabling hotspots, scanning and detecting the hotspots that are created by the application.
public class WifiApController {

    private static final String TAG = "WifiApController";


    private Context mContext;
    private List<ConnectedDeviceObject> mConnDevObjList;

    //Hotspot controller object of external library. Its contents are in 'hotspot' package
    private WifiApManager mWifiApManager;

    //Hotspot object for Android O and above
    private WifiManager.LocalOnlyHotspotReservation mReservation;

    //WifiManager for general purposes
    private WifiManager mWifiManager;

    //When connected to a network, these will be used to check other clients that are connected to network periodically
    private Handler mClientDetectorHandler;
    private Runnable mClientDetectorRunnable;

    //Contains the list of the hotspots that are created by the app
    private List<String> mScannedHotspotSsidList = new ArrayList<>();

    public WifiApController(Context mContext, List<ConnectedDeviceObject> mConnDevObjList) {
        this.mContext = mContext;
        this.mConnDevObjList = mConnDevObjList;

        initialization();


    }

    //Initialization of the object that are used in this class
    private void initialization(){

        mWifiApManager = new WifiApManager(mContext);

        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Update client list every 10 seconds
        mClientDetectorHandler = new Handler();
        mClientDetectorRunnable = new Runnable() {
            @Override
            public void run() {
                updateClientList();
                mClientDetectorHandler.postDelayed(this, 10000);
            }
        };

        checkWriteSettingsPermission();


    }

    //Starts periodical updating of the client list
    public void startUpdatingClientList(){
        mClientDetectorHandler.postDelayed(mClientDetectorRunnable, 1000);
    }

    //Stops periodical updating of the client list
    public void stopUpdatingClientList(){
        mClientDetectorHandler.removeCallbacks(mClientDetectorRunnable);
    }

    //Decides which methods to call to enable hotspot
    public void turnOnHotspot() {
        Log.i(TAG, "turnOnHotspot: ");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            turnOnHotspotAboveOreo();
        } else {
            turnOnHotspotBelowOreo();
        }

        startUpdatingClientList();
    }

    //Decides which methods to call to disable hotspot
    public void turnOffHotspot() {
        Log.i(TAG, "turnOffHotspot: ");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            turnOffHotspotAboveOreo();
        } else {
            turnOffHotspotBelowOreo();
        }

        stopUpdatingClientList();
    }

    //Checks the WRITE_SETTINGS permission, sends user to permission page is needed
    private void checkWriteSettingsPermission() {
        mWifiApManager.showWritePermissionSettings(false);
    }




    //Turning on hotspot for the versions below O
    private void turnOnHotspotBelowOreo() {
        WifiConfiguration wifiConfiguration = initHotspotConfig();
        mWifiApManager.setWifiApEnabled(wifiConfiguration, true);
    }

    //Turning off hotspot for  the versions below O
    private void turnOffHotspotBelowOreo() {
        //Stop acting like a server
        ServerController.getInstance().stopServer();

        mWifiManager.setWifiEnabled(true);
    }

    //Turning on hotspot for Android O and above
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOnHotspotAboveOreo() {
        Log.i(TAG, "turnOnHotspotAboveOreo: ");

        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                Log.d(TAG, "Wifi Hotspot is on now");
                mReservation = reservation;
            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d(TAG, "onStopped: ");
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Log.d(TAG, "onFailed: ");
            }
        }, new Handler());
    }

    private void turnOffHotspotAboveOreo() {
        Log.i(TAG, "turnOffHotspotAboveOreo: ");
        if (mReservation != null) {
            mReservation.close();
        }
    }

    //Pings the network and sets the Connected Device list accordingly
    public void updateClientList() {

        mWifiApManager.getClientList(false, new FinishScanListener() {


            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

                //Dummy list to keep main list away from work as much as possible
                ArrayList<ConnectedDeviceObject> connectedDeviceObjects = new ArrayList<>();

                StringBuilder a = new StringBuilder();
                a.append("WifiApState: " + mWifiApManager.getWifiApState() + "\n\n");
                a.append("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
                    a.append("####################\n");
                    a.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
                    a.append("Device: " + clientScanResult.getDevice() + "\n");
                    a.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
                    a.append("isReachable: " + clientScanResult.isReachable() + "\n");

                    ConnectedDeviceObject connectedDeviceObject = new ConnectedDeviceObject(clientScanResult.getHWAddr(), clientScanResult.getIpAddr());
                    connectedDeviceObjects.add(connectedDeviceObject);
                }

                //Add detected devices to main list.
                mConnDevObjList.clear();
                mConnDevObjList.addAll(connectedDeviceObjects);
                Log.i(TAG, "onFinishScan: " + a);
            }
        });

    }

    //Creates a WifiConfiguration for hotspot initialization and returns it
    private WifiConfiguration initHotspotConfig() {

        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = "Test Hotspot";

        // must be 8 length
        wifiConfig.preSharedKey = "abcd1234";

        //wifiConfig.hiddenSSID = true;

        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);


        return wifiConfig;
    }

    public List<String> getmScannedHotspotSsidList() {
        return mScannedHotspotSsidList;
    }
}
