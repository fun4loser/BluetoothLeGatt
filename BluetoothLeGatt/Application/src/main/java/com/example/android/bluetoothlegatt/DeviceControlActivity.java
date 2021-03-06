
package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String NUMBER_OF_DEVICE = "NUMBER OF DEVICE";
    public static final String EXTRAS_DEVICE_NAME_1 = "DEVICE_NAME1";
    public static final String EXTRAS_DEVICE_ADDRESS_1 = "DEVICE_ADDRESS1";
    public static final String NUMBER_OF_DEVICE_1 = "NUMBER OF DEVICE1";

    private TextView mConnectionState;
    private TextView mDataField;
    private TextView mThumb;
    private TextView mIndex;
    private TextView mMiddle;
    private TextView mRing;
    private TextView mPinky;

    private TextView mThumb1;
    private TextView mIndex1;
    private TextView mMiddle1;
    private TextView mRing1;
    private TextView mPinky1;

    private String mDeviceName;
    private String mDeviceAddress;
    private String mDeviceName1;
    private String mDeviceAddress1;
    //private ExpandableListView mGattServicesList;

    //thLeService> mBluetoothLeService=new ArrayList<BluetoothLeService>();
    private int DEVICE_NUMBER;
    private int DEVICE_NUMBER1;
    private int TOTAL_DEVICE;

    private BluetoothLeService mBluetoothLeService;
    //private BluetoothLeService mBluetoothLeService1;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics1 =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mNotifyCharacteristic1;
    private final String ServiceUUID="00002220-0000-1000-8000-00805f9b34fb";
    private final String CharUUID = "00002221-0000-1000-8000-00805f9b34fb";
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG,"Service Connected Called");
            Log.d(TAG," ");
            //Log.d(TAG,"TOTAL DEVICE IN SERVICE CONNECT "+mBluetoothLeService.size());
            //mBluetoothLeService.add(((BluetoothLeService.LocalBinder) service).getService());
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                Log.d(TAG," ");
                finish();

            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress,0);
            mBluetoothLeService.connect(mDeviceAddress1, 1);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG,"Service DISConnected Called");
            Log.d(TAG," ");
            mBluetoothLeService = null;
        }
    };
    //********************************************************************************
    /*private final ServiceConnection mServiceConnection1 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG,"Service Connected Called");
            Log.d(TAG," ");
            //mBluetoothLeService.add(((BluetoothLeService.LocalBinder) service).getService());
            mBluetoothLeService1 = ((BluetoothLeService.LocalBinder) service).getService();

            if (!mBluetoothLeService1.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                Log.d(TAG," ");
                finish();

            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService1.connect(mDeviceAddress1,1);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG,"Service DISConnected Called");
            Log.d(TAG," ");
            mBluetoothLeService1=null;
        }
    };*/
    //************************************************************************************

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                Log.d(TAG,"ACTION_GATT_CONNECTED ");
                Log.d(TAG," ");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG,"WHEN IS ACTION GATT DISCONNECTED");
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                Log.d(TAG,"ACTION_GATT_DISCONNECTED ");
                Log.d(TAG," ");
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                updateGattServices(mBluetoothLeService.getSupportedGattServices(0),0);

                try {
                    Thread.sleep(700);
                }catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                    updateGattServices(mBluetoothLeService.getSupportedGattServices(1), 1);


                try {
                    Thread.sleep(700);
                }catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                updateDATA();
                //updateDATA(1);

                Log.d(TAG,"ACTION_GATT_SERVICE_DISCOVERED");
                Log.d(TAG," ");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA),0);
                Log.d(TAG,"ACTION_DATA_AVAILABLE ");
                Log.d(TAG," ");

                mBluetoothLeService.readCharacteristic(mNotifyCharacteristic,0);
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA),0);

                mBluetoothLeService.readCharacteristic(mNotifyCharacteristic1, 1);
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA1), 1);


                //wait a while before reading next data
                /*try {
                    Thread.sleep(700);
                }catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }}*/
                //Log.d(TAG,"MY DEVICE NUMBER IN ACTION"+DEVICE_NUMBER);
                //Log.d(TAG,"TOTAL NUMBER OF DEVICE IN ACTION"+mBluetoothLeService.size());

                //mBluetoothLeService.readCharacteristic(mNotifyCharacteristic,0);
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));


                //mBluetoothLeService1.readCharacteristic(mNotifyCharacteristic1,1);
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //Log.d(TAG,"AM I BEING RUN? in 0");


            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    /*private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    Log.d(TAG,"THE LISTENER");
                    Log.d(TAG,"THIS IS THE GROPU POSITION"+groupPosition);
                    Log.d(TAG,"THIS IS THE CHILDPOSITION"+childPosition);
                        if (mGattCharacteristics != null) {
                            final BluetoothGattCharacteristic characteristic =
                                    mGattCharacteristics.get(groupPosition).get(childPosition);
                            final int charaProp = characteristic.getProperties();
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                                // If there is an active notification on a characteristic, clear
                                // it first so it doesn't update the data field on the user interface.
                                if (mNotifyCharacteristic != null&& mNotifyCharacteristic1!=null) {
                                    mBluetoothLeService.setCharacteristicNotification(
                                            mNotifyCharacteristic, false,0);

                                    mBluetoothLeService1.setCharacteristicNotification(
                                            mNotifyCharacteristic1,false,1);

                                    //mNotifyCharacteristic = null;
                                    //mNotifyCharacteristic1=null;
                                }
                                mBluetoothLeService.readCharacteristic(characteristic,0);
                                Log.d(TAG,"chracteristic"+characteristic);
                                try {
                                    Thread.sleep(700);
                                }catch(InterruptedException ex) {
                                    Thread.currentThread().interrupt();
                                }
                                mBluetoothLeService1.readCharacteristic(characteristic,1);
                                Log.d(TAG, "PROPER_READ > 0");
                                Log.d(TAG, " ");

                            }
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                //mNotifyCharacteristic = characteristic;
                               // mNotifyCharacteristic1= characteristic;

                                mBluetoothLeService.setCharacteristicNotification(
                                        characteristic, true,0);
                                mBluetoothLeService1.setCharacteristicNotification(
                                        characteristic,true,1);
                                Log.d(TAG, "PROPER_NOTIFY > 0");
                                Log.d(TAG, " ");
                                //Log.d(TAG,"PARSEINT "+Integer.parseInt("32",16));
                            }

                            return true;
                        }

                    return false;
                }
    };*/
    private final boolean updateDATA(){
        int servicePos = 0;
        int charPos = 0;

        if(mGattCharacteristics!=null&&mGattCharacteristics.size()!=0){
            final BluetoothGattCharacteristic characteristic=
                    mGattCharacteristics.get(servicePos).get(charPos);
            final int charPro = characteristic.getProperties();
            if((charPro|BluetoothGattCharacteristic.PROPERTY_READ)>0){
                if(mNotifyCharacteristic!= null){
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic,false,0);
                }
            }
            mBluetoothLeService.readCharacteristic(characteristic,0);

            if ((charPro | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true,0);
                Log.d(TAG, "PROPER_NOTIFY > 0");
                Log.d(TAG, " ");
            }
            return true;
        }

        return false;

    }

    private void clearUI() {
       // mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceName1 = intent.getStringExtra(EXTRAS_DEVICE_NAME_1);
        mDeviceAddress1=intent.getStringExtra(EXTRAS_DEVICE_ADDRESS_1);
        DEVICE_NUMBER=intent.getIntExtra(NUMBER_OF_DEVICE,0);
        DEVICE_NUMBER1=intent.getIntExtra(NUMBER_OF_DEVICE_1,0);
        //Log.d(TAG,"THIS IS MY DEVICE NUMBER"+DEVICE_NUMBER);
        //TOTAL_DEVICE=mBluetoothLeService.size();

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress+"-"+mDeviceAddress1);
        //mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        //mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        mThumb = (TextView) findViewById(R.id.Thumb);
        mIndex = (TextView) findViewById(R.id.Index);
        mMiddle = (TextView) findViewById(R.id.Middle);
        mRing = (TextView) findViewById(R.id.Ring);
        mPinky = (TextView) findViewById(R.id.Pinky);

        mThumb1 = (TextView) findViewById(R.id.Thumb1);
        mIndex1 = (TextView) findViewById(R.id.Index1);
        mMiddle1 = (TextView) findViewById(R.id.Middle1);
        mRing1 = (TextView) findViewById(R.id.Ring1);
        mPinky1 = (TextView) findViewById(R.id.Pinky1);

        Log.d(TAG,"MY DEVICE NAME "+mDeviceName);
        Log.d(TAG,"MY DEVICE ADDRESS "+mDeviceAddress);
        Log.d(TAG,"MY DEVICE NUMBER "+DEVICE_NUMBER);
        Log.d(TAG,"MY DEVICE NAME1 "+mDeviceName1);
        Log.d(TAG,"MY DEVICE ADDRESS1 "+mDeviceAddress1);
        Log.d(TAG,"MY DEVICE NUMBER1 "+DEVICE_NUMBER1);
        getActionBar().setTitle(mDeviceName+" "+mDeviceName1);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        //Intent gattServiceIntent1 = new Intent(this,BluetoothLeService.class);
        //bindService(gattServiceIntent1,mServiceConnection1,BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResuem is Called");
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {

            boolean result = mBluetoothLeService.connect(mDeviceAddress,0);
            boolean result1 = mBluetoothLeService.connect(mDeviceAddress1, 1);

            Log.d(TAG, "Connect request result=" + result+" "+result1);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause is called");
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy is Called");
        super.onDestroy();
        unbindService(mServiceConnection);
        //unbindService(mServiceConnection1);
        //mBluetoothLeService1 = null;
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress,0);
                mBluetoothLeService.connect(mDeviceAddress1, 1);

                return true;
            case R.id.menu_disconnect:
                Log.d(TAG,"FIRST DEVICE"+DEVICE_NUMBER);
                mBluetoothLeService.disconnect();
                //Log.d(TAG,"SECOND DEVICE"+DEVICE_NUMBER1);
                //mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                Log.d(TAG,"HOME IS PRESSED");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data,int device) {
        //Log.d(TAG,"THIS IS MY STRING DATA"+data);
        //int mData = str2int(data);
        //Log.d(TAG,"THIS IS MY DATA"+mData);
        if (data != null && device ==0) {
            mThumb1.setText(data.substring(1, 4));
            mIndex1.setText(data.substring(5, 8));
            mMiddle1.setText(data.substring(9, 12));
            mRing1.setText(data.substring(13, 16));
            mPinky1.setText(data.substring(17, 20));
        }
        else if(data != null && device == 1) {
            mThumb.setText(data.substring(1, 4));
            mIndex.setText(data.substring(5, 8));
            mMiddle.setText(data.substring(9, 12));
            mRing.setText(data.substring(13, 16));
            mPinky.setText(data.substring(17, 20));
        }
            /*if(mData < 50){
                mThumb.setText(data);
            }
            else if (mData<100){
                mIndex.setText(data);
            }
            else if(mData<150 ){
                mMiddle.setText(data);
            }
            else if(mData<200){
                mRing.setText(data);
            }
            else if(mData<250){
                mPinky.setText(data);
            }*/


    }

    /*private int str2int(String data){
        String digits = "0123456789";
        double val = 0;
        int temp;
        int placeholder=data.length()-2;
        Log.d(TAG,"VALUE OF PLACEHOLDER IN str2int "+placeholder);
        char c;
        for(int i=1;i<data.length();i++){
            c=data.charAt(i);
            Log.d(TAG,"VALUE OF CHARACTER IN str2int "+c);
            temp=digits.indexOf(c);
            val = val+Math.pow(10,placeholder)*temp;
            Log.d(TAG,"VALUE OF val IN str2int "+val);
            placeholder--;
        }
        return (int) val;
    }
    */
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void updateGattServices(List<BluetoothGattService> gattServices,int device) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        //ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
          //      = new ArrayList<ArrayList<HashMap<String, String>>>();
        if(device ==0) {
            mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        }
        else if(device ==1) {
            mGattCharacteristics1 = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        }
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            if(uuid.equals(ServiceUUID)) {
                currentServiceData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
                currentServiceData.put(LIST_UUID, "");
                gattServiceData.add(currentServiceData);

                ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                        new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas =
                        new ArrayList<BluetoothGattCharacteristic>();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();
                    if(uuid.equals(CharUUID)) {
                        //charas.add(gattCharacteristic);
                        currentCharaData.put(
                                LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                        currentCharaData.put(LIST_UUID, "");
                        gattCharacteristicGroupData.add(currentCharaData);
                        if(device ==0){
                            mNotifyCharacteristic=gattCharacteristic;
                        }
                        else if(device ==1){
                            mNotifyCharacteristic1=gattCharacteristic;
                        }
                    }
                }
                if(device ==0){
                    mGattCharacteristics.add(charas);
                }
                else if(device ==1){
                    mGattCharacteristics1.add(charas);
                }
                //gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }
        //Updates the expandable list to display the available service and characteristics.
        /*SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);*/
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
