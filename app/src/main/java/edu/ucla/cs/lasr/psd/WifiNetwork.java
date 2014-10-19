package edu.ucla.cs.lasr.psd;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiNetwork {

  private WifiConfiguration conf;

  WifiNetwork(Context context, String networkSSID, String networkPass) {
    conf = new WifiConfiguration();
    conf.SSID =
        "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
    conf.preSharedKey = "\"" + networkPass + "\"";

    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    wifiManager.addNetwork(conf);

    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
    for (WifiConfiguration i : list) {
      if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
        wifiManager.disconnect();
        wifiManager.enableNetwork(i.networkId, true);
        wifiManager.reconnect();

        break;
      }
    }

  }

}
