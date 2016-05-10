package es.atmosferia.arva.arvapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int BLUETOOTH_SEARCH_DEVICE = 2;
    private static final int RECIEVE_MESSAGE = 3;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Handler h;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private BluetoothAdapter myBA = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice connectedDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, BluetoothDeviceListActivity.class);
                startActivityForResult(i, BLUETOOTH_SEARCH_DEVICE);
            }
        });

        if(myBA == null){
            Toast message = Toast.makeText(this, "Bluetooth not supportted", Toast.LENGTH_LONG);
            message.show();
        }
        else{
            if(!myBA.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:													// if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
                        sb.append(strIncom);												// append string
                        int endOfLineIndex = sb.indexOf("\r\n");							// determine the end-of-line
                        if (endOfLineIndex > 0) { 											// if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);				// extract string
                            sb.delete(0, sb.length());										// and clear
                            //TODO: do appropiate things with data
                            TextView textView = (TextView) findViewById(R.id.section_label);
                            textView.setText("Bluetooth available and receiving data: " + sbprint);
                            Log.i("DATA:", sbprint);
                            /*txtArduino.setText("Data from Arduino: " + sbprint); 	        // update TextView
                            btnOff.setEnabled(true);
                            btnOn.setEnabled(true);*/
                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Toast res;
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                switch(resultCode){
                    case RESULT_CANCELED:
                        res = Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_LONG);
                        res.show();
                        break;
                    case RESULT_OK:
                        res = Toast.makeText(this, "Bluetooth enabled and ready!", Toast.LENGTH_LONG);
                        res.show();
                        break;
                    default:
                        res = Toast.makeText(this, "Something gone wrong while enabling bluetooth, try again please", Toast.LENGTH_LONG);
                        res.show();
                        break;
                }
                break;
            case BLUETOOTH_SEARCH_DEVICE:
                switch(resultCode){
                    case RESULT_CANCELED:
                        res = Toast.makeText(this, "You must link a ARVA device to proceed", Toast.LENGTH_LONG);
                        res.show();
                        break;
                    case RESULT_OK:
                        //desactivar botó i ficar la imatge en fletxa
                        Bundle bund = data.getExtras();
                        connectedDevice = bund.getParcelable("BluetoothDevice");
                        Log.i("MAC: ", connectedDevice.getAddress());
                        res = Toast.makeText(this, "Succeded!", Toast.LENGTH_LONG);
                        res.show();
                        break;
                    default:
                        res = Toast.makeText(this, "Something gone wrong while enabling bluetooth, try again please", Toast.LENGTH_LONG);
                        res.show();
                        break;
                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("resuuuuuuuume", "...onResume - try connect...");
        BluetoothDevice device = connectedDevice;
        if(device != null){
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(this, "Error, failed to resume the activity", Toast.LENGTH_LONG).show();
                finish();
            }
            myBA.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            Log.i("connect", "...Connecting...");
            try {
                btSocket.connect();
                Log.i("ok", "....Connection ok...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Toast.makeText(this, "Error, failed to resume the activity", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            Log.i("create socket", "...Create Socket...");
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.i("PAUSE", "...In onPause()...");

        if(connectedDevice != null){
            try     {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(this, "Error, failed to pause the activity", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        private MapView mapView = null;
        private GoogleMap mMap;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            switch(getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                    textView.setText("Please enable bluetooth!");
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_gps, container, false);
                    mapView = (MapView) rootView.findViewById(R.id.map);
                    mapView.onCreate(savedInstanceState);
                    mMap = mapView.getMap();
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    try{
                        mMap.setMyLocationEnabled(true);
                    }catch (SecurityException se){
                        se.printStackTrace();
                    }

                    MapsInitializer.initialize(this.getActivity());

                    //mMap.setMyLocationEnabled(true);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_stats, container, false);
                    break;
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.i("WRIIIIIITE","...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.i("ERRRROOOOOOOR","...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    //Private function, connects a BluetoothSocket
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.i("SOCKET: ", "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }
}
