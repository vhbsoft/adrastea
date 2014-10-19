package edu.ucla.cs.lasr.psd;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Modified code from: "Professional Android 2 Application Development" By Reto Meier
 */
class BluetoothToWifiSocketListener implements Runnable {

  private BluetoothSocket socket;
  private OutputStream outputSteam;

  public BluetoothToWifiSocketListener(BluetoothSocket socket, OutputStream outputStream) {
    this.socket = socket;
    this.outputSteam = outputStream;
  }

  public void run() {
    int bufferSize = 1024;
    byte[] buffer = new byte[bufferSize];
    try {
      InputStream inStream = socket.getInputStream();
      int bytesRead;
      String message;
      while (true) {
        message = "";
        bytesRead = inStream.read(buffer);
        if (bytesRead != -1) {
          while ((bytesRead == bufferSize) && (buffer[bufferSize - 1] != 0)) {
            message = message + new String(buffer, 0, bytesRead);
            bytesRead = inStream.read(buffer);
          }
          message = message + new String(buffer, 0, bytesRead - 1);
          System.out.println(message);
          outputSteam.write(message.getBytes());
          socket.getInputStream();
        }
      }
    } catch (IOException e) {
      Log.d("BLUETOOTH_COM", e.getMessage());
    }
  }
}
