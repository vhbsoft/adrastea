package edu.ucla.cs.lasr.psd;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

public class PSDService extends IntentService {
  public String wifiPass;

  public final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  public static final String BT_DEVICE = "bt_device";
  public static final String AP_DEVICE = "ap_device";
  public static final String
      CONNECTION_STATUS_UPDATE =
      "edu.ucla.cs.lasr.psd.PSDService.CONNECTION_STATUS_UPDATE";
  public static final String
      CONNECTION_STATUS_UPDATE_REQUEST =
      "edu.ucla.cs.lasr.PSDService.CONNECTION_STATUS_UPDATE_REQUEST";

  public static final String BT_CONNECTION_NAME =
      "edu.ucla.cs.lasr.PSDService.BT_CONNECTION_NAME";
  public static final String BT_IS_CONNECTED =
      "edu.ucla.cs.lasr.PSDService.BT_IS_CONNECTED";
  private BluetoothConnection btConnection;
  private BluetoothDevice bt_device;
  private ScanResult AP_device;

  private Boolean wifi_connected;
  private Boolean bt_connected;

  public static final String WIFI_CONNECTION_NAME =
      "edu.ucla.cs.lasr.PSDService.WIFI_CONNECTION_NAME";
  public static final String WIFI_IS_CONNECTED =
      "edu.ucla.cs.lasr.PSDService.WIFI_IS_CONNECTED";

  private BroadcastReceiver broadcastReceiver;

  /**
   * A constructor is required, and must call the super IntentService(String) constructor with a
   * name for the worker thread.
   */
  public PSDService() {
    super("PSDService");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    bt_connected = false;
    wifi_connected = false;
    wifiPass = "";

    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (PSDService.CONNECTION_STATUS_UPDATE_REQUEST.equals(action)) {
          System.out.println("UPDATE STATUS REQUEST");
          Intent i = new Intent(PSDService.CONNECTION_STATUS_UPDATE);
          if (btConnection != null) {
            i.putExtra(PSDService.BT_IS_CONNECTED, btConnection.isConnected());
          } else {
            i.putExtra(PSDService.BT_IS_CONNECTED, false);
          }
          if (btConnection.isConnected()) {
            i.putExtra(PSDService.BT_CONNECTION_NAME, btConnection.getConnectionName());
          }
          sendBroadcast(i);
        }
      }
    };
    IntentFilter intentFilter = new IntentFilter(PSDService.CONNECTION_STATUS_UPDATE_REQUEST);
    registerReceiver(broadcastReceiver, intentFilter);
    System.out.println("PSDService is registered!");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(broadcastReceiver);
    System.out.println("PSDService is no more!");

  }

  /**
   * The IntentService calls this method from the default worker thread with the intent that started
   * the service. When this method returns, IntentService stops the service, as appropriate.
   */
  @Override
  protected void onHandleIntent(final Intent intent) {
    bt_device = intent.getParcelableExtra(BT_DEVICE);
    AP_device = intent.getParcelableExtra(AP_DEVICE);
    if (bt_device != null) {
      System.out.println(bt_device.toString());
      try {
        btConnection = new BluetoothConnection(bt_device, MY_UUID);
      } catch (IOException e) {
        e.printStackTrace();
      }
      bt_connected = true;
    }

    if (AP_device != null) {
      System.out.println(AP_device.toString());
      WifiNetwork
          wifiNetwork =
          new WifiNetwork(getApplicationContext(), AP_device.SSID, wifiPass);
      wifi_connected = true;
    }

    wifi_connected = true;

    if (wifi_connected && bt_connected) {
      Socket socket = null;
      System.out.println("Both Connected!!!");
      BluetoothToWifiSocketListener bluetoothToWifiSocketListener;
      try {
        socket = new Socket(InetAddress.getByName("192.168.1.1"), 9876);
        bluetoothToWifiSocketListener =
            new BluetoothToWifiSocketListener(btConnection.getSocket(), socket.getOutputStream());
        bluetoothToWifiSocketListener.run();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
