

package com.example.velocityshower;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class MyCommunicationsActivity extends AppCompatActivity {

    private String velocityFromServer = "";
    private TextView velocityTextView;
    private ImageView speedSign;
    private Handler handler;

    public BluetoothSocket mBluetoothSocket = null;

    private String mDeviceAddress;
    private boolean mConnected = true;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    //TODO: Få data via nedenstående metode og ændret textviewet ud fra det

    //TODO: Lav layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_communications);

        velocityTextView = findViewById(R.id.velocityFromServer);

        // Retrieve the address of the bluetooth device from the BluetoothListDeviceActivity
        Intent newint = getIntent();
        mDeviceAddress = newint.getStringExtra(DeviceListActivity.EXTRA_ADDRESS);

        this.handler = new Handler();

        // Create a connection to this device by running asynctask
        messageListener.execute();

    }
    //region Support methods

    private final Runnable readInputFromServer = new Runnable() {
            public void run()
            {
                System.out.println("Entering while loop");
                    if (available()>0) {

                        char c = (char) read();
                        velocityFromServer += c;

                        System.out.println("String from server: " + velocityFromServer);

                        if (velocityFromServer.length() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    velocityTextView.setText(velocityFromServer);

                                }
                            });
                            velocityFromServer = "";
                        }
                    }

                }
            };


    //endregion

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectBluetoothDevice();
    }

    //region AsyncTask

    @SuppressLint("StaticFieldLeak")
    public AsyncTask<Void, Void, Void> messageListener = new AsyncTask<Void, Void, Void>() {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (mBluetoothSocket == null || !mConnected) {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);//connects to the device's address and checks if it's available
                    mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e) {
                mConnected = false;
                System.out.println("ERROR: Device address not found. Exception: " + e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!mConnected){
                Toast.makeText(MyCommunicationsActivity.this, "Connection Failed. Try again", Toast.LENGTH_SHORT).show();
                finish();

            }
            else {
                Toast.makeText(MyCommunicationsActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                handler.postDelayed(readInputFromServer, 1000);
            }

        }
    };

    //endregion

    //region Support methods

    private void disconnectBluetoothDevice() {
        if (mBluetoothSocket!=null) //If the btSocket is busy
        {
            try  {
                mBluetoothSocket.close(); //close connection
            }
            catch (IOException e) {
                Toast.makeText(this, "Error trying to close connection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "Successfully disconected bluetooth device", Toast.LENGTH_SHORT).show();

        //Finish activity when disconnected
        finish();
    }

    //endregion

    //region Public methods

    public void write(byte b) {

        try {
            mBluetoothSocket.getOutputStream().write((int)b);
        }
        catch (IOException e) {
            Toast.makeText(this, "ERROR: Could not write bytes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public int read() {

        int readInput = -1;

        try {
            readInput = mBluetoothSocket.getInputStream().read();
        }
        catch (IOException e) {
            Toast.makeText(this, "ERROR: Could not read bytes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return readInput;
    }

    public int available() {

        int availableInput = 0;

        try {
            availableInput = mBluetoothSocket.getInputStream().available();
        }
        catch (IOException e) {
            Toast.makeText(this, "ERROR: Could not find any available bytes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return availableInput;
    }

    //endregion

}
