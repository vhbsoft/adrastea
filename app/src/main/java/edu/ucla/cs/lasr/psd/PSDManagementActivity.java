package edu.ucla.cs.lasr.psd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PSDManagementActivity extends Activity {

  static final public int PSD_BT_ACTIVITY_RESULT = 0;
  static final public int PSD_AP_ACTIVITY_RESULT = 1;
  static private Intent PSDServiceIntent;
  private BroadcastReceiver broadcastReceiver;

  private TextView tv_bluetooth_connection;
  private TextView tv_wifi_connection;
  private final static int REQUEST_ENABLE_BT = 1;

  private WifiManager mainWifiObj;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_psdmanagment);

    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    tv_bluetooth_connection = (TextView) findViewById(R.id.tv_bluetooth_connection_status);
    tv_wifi_connection = (TextView) findViewById(R.id.tv_wifi_connection_status);

    Button btSelectPSDActivity = (Button) findViewById(R.id.btn_select_psd);
    btSelectPSDActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), SelectPSDDeviceActivity.class);
        startActivityForResult(intent, PSD_BT_ACTIVITY_RESULT);
      }
    });

    Button btSelectWifiActivity = (Button) findViewById(R.id.btn_select_AP);
    btSelectWifiActivity.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), SelectAPActivity.class);
        startActivityForResult(intent, PSD_AP_ACTIVITY_RESULT);
      }
    });

    PSDServiceIntent = new Intent(getApplicationContext(), PSDService.class);

    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (PSDService.CONNECTION_STATUS_UPDATE.equals(action)) {
          if (intent.getBooleanExtra(PSDService.BT_IS_CONNECTED, false)) {
            tv_bluetooth_connection.setText(intent.getStringExtra(PSDService.BT_CONNECTION_NAME));
          } else {
            tv_bluetooth_connection
                .setText(getResources().getString(R.string.bt_connection_status));
          }
        }
      }
    };
    IntentFilter intentFilter = new IntentFilter(PSDService.CONNECTION_STATUS_UPDATE);
    registerReceiver(broadcastReceiver, intentFilter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(broadcastReceiver);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case PSD_BT_ACTIVITY_RESULT:
        BluetoothDevice bt_device;
        try {
          bt_device = (BluetoothDevice) data.getExtras().get(SelectPSDDeviceActivity.BT_DEVICE);
          PSDServiceIntent.putExtra(PSDService.BT_DEVICE, bt_device);
          startService(PSDServiceIntent);
        } catch (NullPointerException e) {
        }
        break;

      case PSD_AP_ACTIVITY_RESULT:
        ScanResult ap;
        try {
          ap = (ScanResult) data.getExtras().get(SelectAPActivity.AP_DEVICE);
          PSDServiceIntent.putExtra(PSDService.AP_DEVICE, ap);
          startService(PSDServiceIntent);
        } catch (NullPointerException e) {
        }
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.my, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
