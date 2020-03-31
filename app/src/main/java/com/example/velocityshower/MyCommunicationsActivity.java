

package com.example.velocityshower;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MyCommunicationsActivity extends CommunicationsActivity {

    private String velocityFromServer;
    private String mDeviceAddress;
    private BluetoothSocket mBluetoothSocket;

    //TODO: Få data via nedenstående metode og ændret textviewet ud fra det

    //TODO: Lav layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBluetoothSocket = getmBluetoothSocket();
        mDeviceAddress = getmDeviceAddress();

        /*
        mMessageTextView = (TextView)findViewById(R.id.serverReplyText);

        mSpeedSeekBar = (SeekBar)findViewById(R.id.seekBar);

        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser==true) {

                    for (byte b : String.valueOf(progress).getBytes()) {
                        mBluetoothConnection.write(b);
                    }
                    mBluetoothConnection.write((byte)'.');

                    while (mBluetoothConnection.available() > 0) {

                        char c = (char)mBluetoothConnection.read();

                        if (c == '.') {

                            if (mMessageFromServer.length() > 0) {
                                mMessageTextView.setText(mMessageFromServer);
                                mMessageFromServer = "";
                            }
                        }
                        else {
                            mMessageFromServer += c;
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

         */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //region Support methods

    private void write(byte b) {

        try {
            mBluetoothSocket.getOutputStream().write((int)b);
        }
        catch (IOException e) {
            Toast.makeText(this, "ERROR: Could not write bytes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int read() {

        int readInput = -1;

        try {
            readInput = mBluetoothSocket.getInputStream().read();
        }
        catch (IOException e) {
            Toast.makeText(this, "ERROR: Could not read bytes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return readInput;
    }

    private int available() {

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
