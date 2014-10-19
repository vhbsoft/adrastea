package edu.ucla.cs.lasr.psd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class BluetoothDiscover extends Observable {

  private Set<BluetoothDevice> unpairedDevices;
  private Set<BluetoothDevice> pairedDevices;
  private BluetoothAdapter bluetoothAdapter;
  private BroadcastReceiver receiver;
  private boolean closed;
  private Context context;

  BluetoothDiscover(Context con) {
    this(con, BluetoothAdapter.getDefaultAdapter());
  }

  BluetoothDiscover(Context con, BluetoothAdapter btAdapter) {
    closed = false;
    this.context = con;
    this.bluetoothAdapter = btAdapter;
    unpairedDevices = new HashSet<BluetoothDevice>();
    pairedDevices = new HashSet<BluetoothDevice>();


    receiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
          // When discovery finds a device
          BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          unpairedDevices.add(device);
        }
        setChanged();
        notifyObservers(action);
      }
    };

    // Register the BroadcastReceiver
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    context.registerReceiver(receiver, filter); // Don't forget to unregister during onDestroy
  }

  public Set<BluetoothDevice> getAllPairedDevices() {
    for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
      pairedDevices.add(device);
    }
    return pairedDevices;
  }

  public Set<BluetoothDevice> getDiscoveredUnpairedDevices() {
    return unpairedDevices;
  }

  public Set<BluetoothDevice> getAllKnownDevices() {
    Set<BluetoothDevice> all = new HashSet<BluetoothDevice>();
    all.addAll(getAllPairedDevices());
    all.addAll(getDiscoveredUnpairedDevices());
    return all;
  }

  public boolean startDiscovery() {
    return bluetoothAdapter.startDiscovery();
  }

  public void stopDiscovery() {
    bluetoothAdapter.cancelDiscovery();
  }

  public boolean isDiscovering() {
    return bluetoothAdapter.isDiscovering();
  }

  public boolean isClosed() {
    return closed;
  }

  public boolean close() {
    if (isClosed()) {
      return false;
    }
    this.context.unregisterReceiver(receiver);
    closed = true;
    return true;
  }

  @Override
  protected void finalize() throws Throwable {
    this.close();
    super.finalize();
  }
}
