package es.atmosferia.arva.arvapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class BluetoothDeviceListActivity extends AppCompatActivity{

    private BluetoothAdapter bluetoothAdapter = null;
    private DeviceListAdapter listAdapter = null;
    private ListViewCompat listViewCompat = null;
    private ArrayList<BluetoothDevice> data = new ArrayList<>();

    private BluetoothDevice currDevice = null;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ble_device_list_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarBle);
        setSupportActionBar(toolbar);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Our custom listview, has the devices found in our scan
        listAdapter = new DeviceListAdapter(this);
        listAdapter.setData(data);
        listAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener(){
            @Override
            public void onPairButtonClick(int position){
                currDevice = data.get(position);

                if(currDevice.getBondState() != BluetoothDevice.BOND_BONDED){
                    Toast.makeText(getApplicationContext(), "Pairing. . .", Toast.LENGTH_LONG).show();
                    if(pairDevice(currDevice) != null){
                        Intent res = new Intent(BluetoothDeviceListActivity.this, MainActivity.class);
                        Log.i("Currdevice 1: ", currDevice.getAddress());
                        res.putExtra("BluetoothDevice", currDevice);
                        BluetoothDeviceListActivity.this.setResult(MainActivity.RESULT_OK, res);
                        BluetoothDeviceListActivity.this.finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Could not pair with the device, try again please", Toast.LENGTH_LONG).show();
                    }
                }
                else if(currDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    //Toast.makeText(getApplicationContext(), "Unpairing. . .", Toast.LENGTH_LONG).show();
                    //unpairDevice(currDevice);
                    Intent res = new Intent(BluetoothDeviceListActivity.this, MainActivity.class);
                    res.putExtra("BluetoothDevice", currDevice);
                    Log.i("Currdevice2: ", currDevice.getAddress());
                    BluetoothDeviceListActivity.this.setResult(MainActivity.RESULT_OK, res);
                    BluetoothDeviceListActivity.this.finish();

                }
            }
        });

        //The listView that contains our devices, must be invalidated when new data comes
        listViewCompat = (ListViewCompat)findViewById(R.id.list_devices);
        listViewCompat.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ble, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            startSearching();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected final void onPause(){
        super.onPause();
        BluetoothDeviceListActivity.this.unregisterReceiver(myReceiver);
    }

    // Create a broadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //When discovery finds a device
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //Do whatever you want with the data
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(data.size() < 1){
                    Log.i("Log", "found device:" + device.getAddress());
                    data.add(device);
                    /*listAdapter.notifyDataSetInvalidated();
                    listAdapter.setData(data);*/
                    listAdapter.notifyDataSetChanged();
                    listViewCompat.invalidateViews();
                }
                else{
                    boolean unique = true;
                    for(int i = 0; i < data.size(); i++){
                        if(device.getAddress().equals(data.get(i).getAddress())){
                            unique = false;
                        }
                    }

                    if(unique){
                        Log.i("Log", "found device:" + device.getAddress());
                        data.add(device);
                        /*listAdapter.notifyDataSetInvalidated();
                        listAdapter.setData(data);*/
                        listAdapter.notifyDataSetChanged();
                        listViewCompat.invalidateViews();
                    }
                }

            }
        }
    };

    //Private function, scans for new devices
    private void startSearching() {
        Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        BluetoothDeviceListActivity.this.registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    //Private function, pairs a BluetoothDevice
    private BluetoothDevice pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            return device;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //Private function, unpairs a BluetoothDevice
    private void unpairDevice(BluetoothDevice device){
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
