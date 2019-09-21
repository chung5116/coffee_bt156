package com.example.coffee_bt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class first_page_activity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "MainActivity";
    BluetoothAdapter mBluetoothAdapter;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothDevice mBTDevice;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;

    ListView lvNewDevices;

    BluetoothConnectionService mBluetoothConnection;

    //sec_page
    private static final String TAG2 = "sec_page_Activity";

    int value;
    int mvalue,mvalue2;
    int value2;
    int value0 = 0;
    String Svalue,Svalue1;
    boolean mturn;


    StringBuilder messages =new StringBuilder();
    //TextView beams_weight = (TextView)findViewById(R.id.beams_weight);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page_activity);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4,filter);

        lvNewDevices.setOnItemClickListener(first_page_activity.this);

        enableDisableBT();




        //LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver1,new IntentFilter("incomingMessage_for_weight"));



    }



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);

        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
        //mBluetoothAdapter.cancelDiscovery();       //原本前面有//

    }


    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

        }
        if(mBluetoothAdapter.isEnabled()){
            // mBluetoothAdapter.disable();  這個會讓原本開藍芽取消

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

        }
            btnDiscover();




    }

    public void btnDiscover(){
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkBTPermissions();
            }

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkBTPermissions();
            }

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
                btnDiscover();
            }
        }
    };


    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);

            }
        }
    };


    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() ==  BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


    /*public BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String gweight = intent.getStringExtra("theMessage_for_weight");
            messages.append(gweight+"\n");
            beams_weight1.setText(messages);
            Log.d(TAG2,"重量");
        }
    };*/

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        if (mBluetoothAdapter.isEnabled()){
            Toast.makeText(this,"已點選裝置",Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "請再次開啟藍芽", Toast.LENGTH_SHORT).show();
        }

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(first_page_activity.this);
        }



        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startConnection();
                Log.d(TAG,"自動連線好了");//自動連線
            }
        },1000);


        jumpto_sec_page();

    }

    public void jumpto_sec_page(){
        setContentView(R.layout.activity_sec_page_activity);
        Button return_zero = (Button)findViewById(R.id.return_zero);

        final String zero = "t";
        final TextView beams_weight1 = (TextView)findViewById(R.id.beams_weight);
        //StringBuilder messages = new StringBuilder();

        BroadcastReceiver mReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String gweight = intent.getStringExtra("theMessage_for_weight");

                //messages.append(gweight);
                beams_weight1.setText(gweight);
                Log.d(TAG2,"重量");

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver1,new IntentFilter("incomingMessage_for_weight"));








        return_zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[]bytes = zero.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write1(bytes);
            }
        });
        /*
        final NumberPicker mNumberPicker =(NumberPicker)findViewById(R.id.picker);
        final TextView tvShowNumbers =(TextView)findViewById(R.id.tvShowNumbers);
        Button sendspeed = (Button)findViewById(R.id.sendspeed);
        Button playpause =(Button)findViewById(R.id.play_pause);


        mNumberPicker.setMinValue(0);  //把0改掉
        mNumberPicker.setMaxValue(6);

        // to change formate of number in numberpicker
        mNumberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                value = Integer.valueOf(mNumberPicker.getValue()).intValue();
                mvalue = value*682;
                Svalue = String.valueOf(mvalue);
            }
        });


        sendspeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = Svalue.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write1(bytes);
                mturn = true;
                value2 = value;
                tvShowNumbers.setText(""+value);
            }
        });


        //暫停繼續
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mturn == true) {
                    Log.d(TAG, "第一");
                    Svalue1 = String.valueOf(value0);
                    byte[] bytes = Svalue1.getBytes(Charset.defaultCharset());
                    mBluetoothConnection.write1(bytes);
                    mturn = false;
                } else {
                    mvalue2 = value2*682;
                    Log.d(TAG,"第二");
                    Svalue1 = String.valueOf(mvalue2);
                    byte[]bytes = Svalue1.getBytes(Charset.defaultCharset());
                    mBluetoothConnection.write1(bytes);
                    mturn = true;
                }
            }

        });*/
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkBTPermissions(){
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0){

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001); //any number
            }
        }else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    /**
     * 偵測是否退出兩秒內按兩下才會退出
     */
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(getApplicationContext(),"再按一次退出程式",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //create method for starting connection
//***remember the connection will fail and app will crash if you haven't paired first
    public void startConnection(){
        try {
            Log.d(TAG,"開始連接BTConnection");//這邊加上try catch 看看可不可不要直接crash
            Log.d(TAG,"test 1");
            startBTConnection(mBTDevice,MY_UUID_INSECURE);
        }catch (Exception e){
            Log.d(TAG,"還沒選device");
            Toast.makeText(this,"未選擇裝置",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }



}
