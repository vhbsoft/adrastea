package edu.ucla.cs.lasr.psd;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class SelectPSDDeviceActivity extends Activity implements Observer {

  Button btDiscoverButton;
  private BluetoothDiscover btDiscover;
  Intent SelectedPSDIntent;
  public static final String BT_DEVICE = "selected_bt_psd_device";

  private BtArrayAdapter adapter;

  final ArrayList<BluetoothDevice> values = new ArrayList<BluetoothDevice>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_selectpsd);
    final ListView btList = (ListView) findViewById(R.id.bt_device_list);

    SelectedPSDIntent = new Intent();

    btList.setClickable(true);
    btList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BluetoothDevice bt_device = (BluetoothDevice) btList.getItemAtPosition(position);
//        System.out.println(bt_device.getAddress());

        SelectedPSDIntent.putExtra(SelectPSDDeviceActivity.BT_DEVICE, bt_device);
        setResult(PSDManagementActivity.PSD_AP_ACTIVITY_RESULT, SelectedPSDIntent);
        finish();
      }
    });

    adapter = new BtArrayAdapter(this, R.layout.device_layout, values);
    btList.setAdapter(adapter);

    btDiscover = new BluetoothDiscover(getApplicationContext());
    btDiscover.addObserver(this);

    btDiscoverButton = (Button) findViewById(R.id.bt_discover);
    btDiscoverButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        btDiscover.startDiscovery();
        adapter.clear();
        adapter.addAll(btDiscover.getAllKnownDevices());
      }
    });

    adapter.addAll(btDiscover.getAllKnownDevices());
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

  public void update(Observable observable, Object o) {
//    System.out.println(o.toString());
    if (observable == btDiscover) {
      if (BluetoothDevice.ACTION_FOUND.equals(o)) {
        adapter.clear();
        adapter.addAll(btDiscover.getAllKnownDevices());
      }
    }
  }
}
