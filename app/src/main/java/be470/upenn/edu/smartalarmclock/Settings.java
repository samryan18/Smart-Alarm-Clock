package be470.upenn.edu.smartalarmclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.Set;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.bluetooth.BluetoothSocket;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Message;
import android.os.Handler;
import android.widget.ProgressBar;

public class Settings extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothDevice arduino;
    private static final int PROGRESS = 0x1;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //setUpBluetooth();
        fakeBluetooth();

    }

    private void fakeBluetooth() {
        Toast.makeText(this, "Searching for bluetooths nearby!", Toast.LENGTH_LONG).show();
        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        /*long time1 = System.currentTimeMillis();
        int mProgressStatus = 0,counter =0;
        while (mProgressStatus <= 100) {
            long time2 = System.currentTimeMillis();
            counter++;
            if (time2 - time1 > (100 * counter)) {
                mProgress.setProgress(mProgressStatus);
                mProgressStatus++;
            }
        }*/

        mProgress.setVisibility(View.INVISIBLE);
        // Start lengthy operation in a background thread
        /*new Thread(new Runnable() {
            public void run() {
                long time1 = System.currentTimeMillis();
                int counter = 0;
                while (mProgressStatus < 100) {
                    long time2 = System.currentTimeMillis();
                    if (time2-time1 > 10*counter) {
                        mProgressStatus++;
                    } counter ++;


                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                }

            }
        }).start();*/


        Button bt1=(Button)findViewById(R.id.bt1);
        bt1.setVisibility(View.VISIBLE); //To set visible

    }

    public void callPair(View v) {
        Toast.makeText(this, "Pairing...", Toast.LENGTH_LONG).show();
        /*long time1 = System.currentTimeMillis();
        int counter = 0;
        while (counter < 55) {
            long time2 = System.currentTimeMillis();
            if (time2 - time1 > 100 * counter) {
                counter++;
            }
        }*/
        Toast.makeText(this, "Device Paired. Sleep well :)", Toast.LENGTH_LONG).show();

    }

    public void goHome(View v) {
        Intent goToHome = new Intent(this, HomeScreen.class);
        startActivity(goToHome);
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
                    String deviceName = arduino.getName();
                    String deviceHardwareAddress = arduino.getAddress(); // MAC address
                    Toast.makeText(this, ("Found a device: " + deviceName + " Mac Address: " + deviceHardwareAddress), Toast.LENGTH_SHORT).show();
                    mConnectThread = new ConnectThread(arduino);
                    mConnectThread.start();
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
                            mHandler2.obtainMessage(1, begin, i, buffer).sendToTarget();
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

    Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    System.out.println(writeMessage);

                    break;
            }
        }
    };
}