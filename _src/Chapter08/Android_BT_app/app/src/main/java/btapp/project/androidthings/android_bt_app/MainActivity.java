package btapp.project.androidthings.android_bt_app;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devpaul.bluetoothutillib.SimpleBluetooth;
import com.devpaul.bluetoothutillib.dialogs.DeviceDialog;
import com.devpaul.bluetoothutillib.utils.BluetoothUtility;
import com.devpaul.bluetoothutillib.utils.SimpleBluetoothListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private SimpleBluetooth btConnection;
    private String serverMacAdd;
    private TextView tempView;
    private TextView humView;
    private TextView pressView;

    private static final int SCAN_REQUEST = 1410;

    private static final String TAG = "BTApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button scanBtn = (Button) findViewById(R.id.scan_button);

        // UI
        tempView = (TextView) findViewById(R.id.temp);
        humView = (TextView) findViewById(R.id.hum);
        pressView = (TextView) findViewById(R.id.press);

        initBT();
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConnection.scan(SCAN_REQUEST);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_REQUEST) {
            Log.d(TAG, "SCAN REQUEST - Result Code ["+requestCode+"]");
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Activity result scan..") ;
                serverMacAdd = data.getStringExtra(DeviceDialog.DEVICE_DIALOG_DEVICE_ADDRESS_EXTRA);
                Log.d(TAG, "Device Add ["+serverMacAdd+"]");
                btConnection.connectToBluetoothServer(serverMacAdd);
               // btConnection.connectToBluetoothDevice(serverMacAdd);

            }
        }
    }

    private void initBT() {
        btConnection = new SimpleBluetooth(this, this);
        btConnection.setSimpleBluetoothListener(new SimpleBluetoothListener() {
            @Override
            public void onBluetoothDataReceived(byte[] bytes, String data) {
                super.onBluetoothDataReceived(bytes, data);
                Log.d(TAG, "Data received");
                if (data != null) {
                    try {
                        JSONObject obj = new JSONObject(data);
                        String temp = obj.getString("temp");
                        String press = obj.getString("press");
                        String hum = obj.getString("hum");
                        updateView(hum, temp, press);
                    }
                    catch(JSONException jse) {
                        jse.printStackTrace();
                    }

                }
            }

            @Override
            public void onDeviceConnected(BluetoothDevice device) {
                super.onDeviceConnected(device);
                Log.d(TAG, "Device connected" + device.getName());


            }

            @Override
            public void onDeviceDisconnected(BluetoothDevice device) {
                super.onDeviceDisconnected(device);
                Log.d(TAG, "Device disconnected" + device.getName());
            }

            @Override
            public void onDiscoveryStarted() {
                super.onDiscoveryStarted();
                Log.d(TAG, "Discovery started");
            }

            @Override
            public void onDiscoveryFinished() {
                super.onDiscoveryFinished();
                Log.d(TAG, "Discovery finished");
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {
                super.onDevicePaired(device);
                Log.d(TAG, "Device paiered" + device.getName());
            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {
                super.onDeviceUnpaired(device);
                Log.d(TAG, "Device unpaired" + device.getName());
            }
        });

        btConnection.initializeSimpleBluetooth();
        btConnection.setInputStreamType(BluetoothUtility.InputStreamType.NORMAL);
    }

    private void updateView(final String hum, final String temp, final String press) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tempView.setText(temp);
                pressView.setText(press);
                humView.setText(hum);
            }
        });

    }
}
