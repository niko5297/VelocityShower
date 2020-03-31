

package com.example.velocityshower;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MyCommunicationsActivity extends CommunicationsActivity {

    private String velocityFromServer = "";
    private String mDeviceAddress;
    private BluetoothSocket mBluetoothSocket;
    private TextView velocityTextView;
    private ImageView speedSign;

    //TODO: Få data via nedenstående metode og ændret textviewet ud fra det

    //TODO: Lav layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        velocityTextView = findViewById(R.id.velocityFromServer);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        mBluetoothSocket = getmBluetoothSocket();
        mDeviceAddress = getmDeviceAddress();

        readInputFromServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //region Support methods

    private void readInputFromServer() {
        while (available() > 0) {

            char c = (char) read();


            if (velocityFromServer.length() > 0) {
                velocityTextView.setText(velocityFromServer);
                }

            else {
                velocityFromServer += c;
            }
        }
    }

    //endregion

}
