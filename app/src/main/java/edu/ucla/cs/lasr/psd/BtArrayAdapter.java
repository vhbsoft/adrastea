package edu.ucla.cs.lasr.psd;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BtArrayAdapter extends ArrayAdapter<BluetoothDevice> {

  private Context context;
  private ArrayList<BluetoothDevice> bt_devices;

  public BtArrayAdapter(Context context, int resource, ArrayList<BluetoothDevice> bt_devices) {
    super(context, resource, bt_devices);
    this.context = context;
    this.bt_devices = bt_devices;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View btDevView = inflater.inflate(R.layout.device_layout, parent, false);
    TextView devNameView = (TextView) btDevView.findViewById(R.id.devName);
    TextView devMACAddView = (TextView) btDevView.findViewById(R.id.devMACAdd);

    String device_name = bt_devices.get(position).getName();
    devNameView.setText(device_name);
    String device_address = bt_devices.get(position).getAddress();
    devMACAddView.setText(device_address);

    return btDevView;
  }
}
