package com.rikleysandroidprojects.wifisecureapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    public WifiManager wifiManager;

    private TextView txtStatus;
    private TextView txtWiFiInfo;
    private Button btnScan;
    private Button btnScanAll;

    private WifiConfiguration wifiCurrConfig;
    private BroadcastReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up UI widgets
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtWiFiInfo = (TextView) findViewById(R.id.txtWiFiInfo);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        btnScanAll = (Button) findViewById(R.id.btnScanAll);
        btnScanAll.setOnClickListener(this);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiReceiver == null) {
            wifiReceiver = new ScanReceiver(this);
        }

        registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void analyzeWifiConfiguration(WifiConfiguration wifiCurrConfig) {
        // Authentication processing
        if (wifiCurrConfig.allowedKeyManagement.get(
                WifiConfiguration.KeyMgmt.WPA_PSK)) {
            txtStatus.append("Key Management: WPA_PSK \n");
        }

        if (wifiCurrConfig.allowedKeyManagement.equals(
                WifiConfiguration.KeyMgmt.IEEE8021X)) {
            txtStatus.append("802.1x \n");
        }

        if (wifiCurrConfig.wepKeys[0] != null) {
            txtStatus.append("WEP \n");
        }

        if (wifiCurrConfig.allowedKeyManagement.equals(
                WifiConfiguration.KeyMgmt.WPA_EAP)) {
            txtStatus.append("WPA_EAP \n");
        }

        if (wifiCurrConfig.allowedKeyManagement.equals(
                WifiConfiguration.KeyMgmt.NONE)) {
            txtStatus.append("NONE \n");
        }
        // Security Protocols
        txtStatus.append("Protocols: ");
        if (wifiCurrConfig.allowedProtocols.get(WifiConfiguration.Protocol.RSN)) {
            // IEEE 801.11i or WPA2
            txtStatus.append("RSN ");
        }
        if (wifiCurrConfig.allowedProtocols.get(WifiConfiguration.Protocol.WPA)) {
            //WPA or IEEE 801.11i or D3.0
            txtStatus.append("WPA ");
        }

        txtStatus.append("\n \n RAW Config: \n" +wifiCurrConfig.toString());
        //  txtStatus.append("\n RAW EConfig: \n" +wifiCurrConfig.enterpriseConfig.toString());
        txtStatus.append("\n \n");

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);

        }

        appendLog(wifiCurrConfig.toString());
    }

    // Click on button
    public void onClick(View view) {

        if (view.getId() == R.id.btnScan) {
            Toast.makeText(this, "Scanning initiated", Toast.LENGTH_LONG).show();

            if (wifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                // Identify and collect configuration of connected WiFi network
                List<WifiConfiguration> allWifiConfigs = wifiManager.getConfiguredNetworks();
                txtStatus.append("Current WiFi Connection: " + wifiInfo.getSSID().toString());
                txtStatus.append("\n");
                for (WifiConfiguration config : allWifiConfigs) {
                    if (config.networkId == wifiInfo.getNetworkId()) {
                        wifiCurrConfig = config;
                    }
                }

                analyzeWifiConfiguration(wifiCurrConfig);

            } else {
                txtWiFiInfo.setText("Enable WiFi");
            }

        }

        if (view.getId() == R.id.btnScanAll) {
            Toast.makeText(this, "Scanning all Wifi in range", Toast.LENGTH_LONG).show();

            if (wifiManager.isWifiEnabled()) {
            //    wifiManager.startScan();
                // Identify and collect configuration of connected WiFi network
               // txtWiFiInfo.setText(" ");

          //      for (ScanResult config : scanResults) {
          //          txtStatus.setText(config.capabilities);
  //                  txtStatus.append("SSID: " + config.SSID + "\n");
    //                txtStatus.append("PreSharedKey: " + config.preSharedKey + "\n");

//                    analyzeWifiConfiguration(config);
          //      }

            }
        }

    }

    @Override
    public void onStop() {
        // Make sure reciever is off
        super.onStop();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public static void appendLog(String text) {

        File logFile = new File("sdcard/wifilog.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(text);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onRequestPermissionsResult( int requestCode,
//                                            String permissions[],
//                                            int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_COARSE_LOCATION: {
//                if (grantResults.length > 0 &&
//                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//
//                }
//            }
//        }
//    }

    private static final int REQUEST_WRITE_STORAGE = 112;
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode)
    {
        case REQUEST_WRITE_STORAGE: {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //reload my activity with permission granted or use the features what required the permission
                this.recreate();
            } else
            {
                Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
            }
        }
    }

}
}