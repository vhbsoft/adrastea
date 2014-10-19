package edu.ucla.cs.lasr.psd;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SelectAPActivity extends Activity {

  private WifiManager mainWifiObj;
  private BroadcastReceiver wifiScanner;
  private ListView wifiList;

  private final ArrayList<ScanResult> values = new ArrayList<ScanResult>();
  private ApArrayAdapter adapter;
  public static final String AP_DEVICE = "selected_ap_psd_device";
  Intent SelectedAPIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_ap);

    mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    wifiList = (ListView) findViewById(R.id.ap_device_list);

    SelectedAPIntent = new Intent();

    adapter = new ApArrayAdapter(this, R.layout.device_layout, values);
    wifiList.setAdapter(adapter);

    wifiScanner = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
        adapter.addAll(wifiScanList);
      }
    };


    wifiList.setClickable(true);
    wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ScanResult wifiDev = (ScanResult) wifiList.getItemAtPosition(position);
        System.out.println(wifiDev.SSID);

        SelectedAPIntent.putExtra(SelectAPActivity.AP_DEVICE, wifiDev);
        setResult(PSDManagementActivity.PSD_BT_ACTIVITY_RESULT, SelectedAPIntent);
        finish();
      }
    });

    registerReceiver(wifiScanner, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    mainWifiObj.startScan();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    unregisterReceiver(wifiScanner);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.select_a, menu);
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
