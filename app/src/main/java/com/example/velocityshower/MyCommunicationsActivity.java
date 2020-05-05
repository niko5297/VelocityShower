

package com.example.velocityshower;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class MyCommunicationsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //region Fields

    private String responseFromServer = "";
    private int speedLimit = 0;
    private TextView velocityTextView;
    private ImageView speedSign;
    private Handler handler;

    public BluetoothSocket mBluetoothSocket = null;

    private String mDeviceAddress;
    private boolean mConnected = true;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private TextToSpeech textToSpeech;

    //endregion

    //region Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_communications);

        velocityTextView = findViewById(R.id.velocityFromServer);
        speedSign = findViewById(R.id.speedSign);

        // Retrieve the address of the bluetooth device from the BluetoothListDeviceActivity
        Intent newint = getIntent();
        mDeviceAddress = newint.getStringExtra(DeviceListActivity.EXTRA_ADDRESS);

        this.handler = new Handler();

        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        // Create a connection to this device by running asynctask
        messageListener.execute();

    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectBluetoothDevice();
    }

    //endregion

    //region Runnable methods

    private final Runnable readInputFromServer = new Runnable() {
            public void run()
            {
                System.out.println("Entering while loop");
                    while (available()>0){

                        char c = (char) read();

                        if (responseFromServer.length() > 0 && c == 'v') {
                            readSpeed();
                            continue;
                        }
                        else if (responseFromServer.length() > 0 && c == 's'){
                            readSpeedSign();
                            continue;
                        }
                        else if (c == 'v'){
                            continue;
                        }
                        else if (c == 's'){
                            continue;
                        }
                        responseFromServer += c;

                        System.out.println("String from server: " + responseFromServer);

                    }
                    MyCommunicationsActivity.this.handler.postDelayed(readInputFromServer,1000);

                }
            };


    //endregion

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
                handler.post(readInputFromServer);
            }

        }
    };

    //endregion

    //region Support methods

    private void disconnectBluetoothDevice() {
        if (mBluetoothSocket!=null) //If the btSocket exists and is currently taken
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

    private void readSpeedSign(){
        //Switch case from the value
        switch (responseFromServer){

            //Set imageresource accordingly
            case "50":
                speedSign.setImageResource(SpeedSignEnum.SPEED_50.getImage());
                speedLimit = 50;
                break;

            case "60":
                speedSign.setImageResource(SpeedSignEnum.SPEED_60.getImage());
                speedLimit = 60;
                break;

            case "70":
                speedSign.setImageResource(SpeedSignEnum.SPEED_70.getImage());
                speedLimit = 70;
                break;

            case "80":
                speedSign.setImageResource(SpeedSignEnum.SPEED_80.getImage());
                speedLimit = 80;
                break;

            default:
                speedSign.setImageResource(SpeedSignEnum.SPEED_NONE.getImage());
                speedLimit = 0;
                System.out.println("WARNING: Found no speedsign for that speed. Setting speedlimit to 0!");

        }

        //Reset responseFromServer string to nothing
        responseFromServer = "";
    }

    private void readSpeed(){

        //Change text to speed using the UIThread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                velocityTextView.setText(responseFromServer);

            }
        });

        //Check if the speed is higher than the speed limit
        if (Integer.parseInt(responseFromServer)>speedLimit && speedLimit!= 0){ //if speedlimit is 0, there is no speedlimit

            //Play "You are going to fast" voice
            textToSpeech.speak("You are going to fast",TextToSpeech.QUEUE_FLUSH,null,null);
        }

        //Reset responseFromServer String to nothing
        responseFromServer = "";
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

    //region onInit

    @Override
    public void onInit(int status) {
        textToSpeech.setLanguage(Locale.ENGLISH);
    }

    //endregion

}
