package com.rikleysandroidprojects.wifisecureapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by Selena on 01/04/2016.
 */
public class ScanReceiver extends BroadcastReceiver {
    MainActivity mainActivity;

    public ScanReceiver(MainActivity main) {
        super();
        mainActivity = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Scanner", "Recieve");

        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
           List<ScanResult> scanResultList = mainActivity.wifiManager.getScanResults();
            Log.d("Scanner", "Made List");
        }
        else {
            // request permission
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.REQUEST_COARSE_LOCATION);
        }

            //for(ScanResult scanResult : wifiScanList){
                //txtStatus.append(scanResult.SSID + "\n");

            //}
    }



}