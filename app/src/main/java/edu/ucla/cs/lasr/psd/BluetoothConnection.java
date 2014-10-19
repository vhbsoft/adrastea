package edu.ucla.cs.lasr.psd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnection {

  private OutputStream outputStream;
  private InputStream inputStream;
  private BluetoothSocket socket;
  private BluetoothDevice btDevice;
  private UUID uuid;


  BluetoothConnection(BluetoothDevice btDevice, UUID uuid)
      throws IOException {
    this.btDevice = btDevice;
    this.uuid = uuid;
    tryConnecting(btDevice, uuid);
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public BluetoothDevice getBtDevice() {
    return btDevice;
  }

  public String getConnectionName() {
    return socket.getRemoteDevice().getName();
  }

  public Boolean isConnected() {
    return socket.isConnected();
  }

  public BluetoothSocket getSocket(){
    return socket;
  }

  public void close() {
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Boolean tryConnecting(BluetoothDevice btDevice, UUID uuid) {
    this.btDevice = btDevice;
    try {
      socket = btDevice.createRfcommSocketToServiceRecord(uuid);
      socket.connect();
      System.out.println("Connect");
      outputStream = socket.getOutputStream();
      inputStream = socket.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Exception in creating bt connection...");
      return false;
    }
    return true;
  }
}
