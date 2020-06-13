package otabek.io.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tish";
    ArrayList<String> devices;
    ArrayList<String> addresses = new ArrayList<>();
    ListView mListView;
    Button searchButton;
    TextView mTextView;
    BluetoothAdapter mBluetoothAdapter;
    ArrayAdapter mArrayAdapter;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: " + action);
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mTextView.setText("Finished");
                searchButton.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                String listText;
                if (!addresses.contains(address)) {
                    addresses.add(address);
                    if (name == null || name == "") {
                        name = address;
                        listText = name + "RSSI: " + rssi;
                    } else {
                        listText = name + " " + address + " RSSI:" + rssi;
                    }
                    devices.add(listText);

                    mArrayAdapter.notifyDataSetChanged();

                }

            }
        }
    };

    public void search(View view) {
        mTextView.setText("Searching ...");
        searchButton.setEnabled(false);
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devices = new ArrayList<>();
        mListView = findViewById(R.id.listview);
        mTextView = findViewById(R.id.textView);
        searchButton = findViewById(R.id.button);
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, devices);
        mListView.setAdapter(mArrayAdapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        registerReceiver(mBroadcastReceiver, intentFilter);


    }
}
