package edu.ucla.cs.lasr.psd;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ApArrayAdapter extends ArrayAdapter<ScanResult> {

  private Context context;
  private ArrayList<ScanResult> ap_devices;

  public ApArrayAdapter(Context context, int resource, ArrayList<ScanResult> ap_devices) {
    super(context, resource, ap_devices);
    this.context = context;
    this.ap_devices = ap_devices;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    View APDevView = inflater.inflate(R.layout.device_layout, parent, false);
    TextView devNameView = (TextView) APDevView.findViewById(R.id.devName);
    TextView devMACAddView = (TextView) APDevView.findViewById(R.id.devMACAdd);

    String device_name = ap_devices.get(position).SSID;
    devNameView.setText(device_name);
    String device_address = ap_devices.get(position).BSSID;
    devMACAddView.setText(device_address);

    return APDevView;
  }
}
