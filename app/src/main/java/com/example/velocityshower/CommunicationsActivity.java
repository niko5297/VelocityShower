
package com.example.velocityshower;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class CommunicationsActivity extends AppCompatActivity {


    private String mDeviceAddress;
    private boolean mConnected = true;
    private BluetoothSocket mBluetoothSocket = null;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //TODO: Add Lottieanimation

    //TODO: Samle Communicationstask og Activity og lav seperat Asynctask


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_communications);

        // Retrieve the address of the bluetooth device from the BluetoothListDeviceActivity
        Intent newint = getIntent();
        mDeviceAddress = newint.getStringExtra(DeviceListActivity.EXTRA_ADDRESS);

        // Create a connection to this device by running asynctask
        messageListener.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectBluetoothDevice();
    }

    @SuppressLint("StaticFieldLeak")
    private AsyncTask<Void, Void, Void> messageListener = new AsyncTask<Void, Void, Void>() {

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
                Toast.makeText(CommunicationsActivity.this, "Connection Failed. Try again", Toast.LENGTH_SHORT).show();
                finish();

            }
            else {
                Toast.makeText(CommunicationsActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void disconnectBluetoothDevice() {
        if (mBluetoothSocket!=null) //If the btSocket is busy
        {
            try  {
                mBluetoothSocket.close(); //close connection
            }
            catch (IOException e) {
                Toast.makeText(CommunicationsActivity.this, "Error trying to close connection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(CommunicationsActivity.this, "Successfully disconected bluetooth device", Toast.LENGTH_SHORT).show();

        //Finish activity when disconnected
        finish();
    }

    public String getmDeviceAddress() {
        return mDeviceAddress;
    }

    public BluetoothSocket getmBluetoothSocket() {
        return mBluetoothSocket;
    }
}
