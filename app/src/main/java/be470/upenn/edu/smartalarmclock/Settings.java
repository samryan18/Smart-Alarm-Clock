package be470.upenn.edu.smartalarmclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.Set;
import android.widget.Toast;
import android.bluetooth.BluetoothSocket;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Message;
import android.os.Handler;

public class Settings extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothDevice arduino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setUpBluetooth();
    }

    /**
     * Sets up Bluetooth connection with HC-05 Bluetooth Module with the arduino
     * This method and the sub-classes below are based off the following app note:
     * http://www.egr.msu.edu/classes/ece480/capstone/spring14/group01/docs/appnote/Wirsing-SendingAndReceivingDataViaBluetoothWithAnAndroidDevice.pdf
     */
    private void setUpBluetooth() {
        Toast.makeText(this, "Bluetooth Setup Started..", Toast.LENGTH_SHORT).show();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Bluetooth Adapter is null. (Genymotion can cause this issue.)",
                    Toast.LENGTH_LONG).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "ENABLING mBluetoothAdapter", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    arduino = device;
                    Toast.makeText(this, "Found a device", Toast.LENGTH_LONG).show();
                    //String deviceName = arduino.getName();
                    //String deviceHardwareAddress = arduino.getAddress(); // MAC address
                    mConnectThread = new ConnectThread(arduino);
                    mConnectThread.start();
                    Toast.makeText(this, "mConnectThreadStarted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**************************************************************************
     ********************** ConnectThread Inner Class *************************
     **************************************************************************/

    /**
     * Up until now, all of the
     * program’s code has been written in the main thread, or “user interface thread” (UI thread). The
     * UI thread should never be blocked. Therefore, create a new thread class where the connection
     * will form.
     */

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        // HC-05 UUID  "00001101-0000-1000-8000-00805F9B34FB"
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }
        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /**************************************************************************
     ********************** ConnectedThread Inner Class ***********************
     **************************************************************************/

    /**
     * Used for sending data to and from arduino
     */

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mmSocket = socket;
            try {
                tmpOut = socket.getOutputStream();
                tmpIn = socket.getInputStream();
            } catch (IOException e) { System.out.println("Caught IOException in ConnectedThread"); }
            mmOutStream = tmpOut;
            mmInStream = tmpIn;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes = 0;
            int begin = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) /* if '#' terminating character reached */ {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { System.out.println("Caught IOException in ConnectedThread"); }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { System.out.println("Caught IOException in ConnectedThread"); }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    break;
            }
        }
    };
}