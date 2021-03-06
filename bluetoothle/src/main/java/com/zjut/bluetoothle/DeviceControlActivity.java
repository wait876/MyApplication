/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjut.bluetoothle;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.zjut.bluetoothle.Constants.*;


/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = "DeviceControlActivity";//DeviceControlActivity
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private HashMap<String, BluetoothGattCharacteristic> allCharacteristics;
    private TextView mConnectionState;	//连接状态
    private TextView mDataField;		//数据
    private TextView mobileTime;


    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;	//ServicesList
    private BluetoothLeService mBluetoothLeService;	//BLE服务
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        // 绑定服务后
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            // 初始化蓝牙服务
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // 开始连接
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();

                        Log.i(TAG, String.valueOf(charaProp) + "LJP");

                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // 2
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            // 读取属性
                            mBluetoothLeService.readCharacteristic(characteristic);
                            Log.i(TAG, "Readljp");
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            // 16
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                            Log.i(TAG, "Notifyljp");
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            // 8
                            Log.i(TAG, "Writeljp");
                        }
                        displayData(getResources().getString(R.string.no_data));
                        return true;
                    }
                    return false;
                }
            };

    private Dialog mDialog;
    private Handler tipHandler=new Handler();
    private long startTime;
    private long endTime;


    private File file = null;
    private File dir = null;
    private File backFile = null;
    private EditText deviceIdEditText;
    //private EditText deviceTimeEditText;
    private TextView deviceTimeTextView;
    private EditText deviceSumEditText;
    private String[] newSettingStrings=null;
    private Button submitButton;
    private Button readButton;
    private Button syncButton;
    private int syncCounts;

    //advertising time  2014-11-16
    private EditText advertising_time_start_1_EditText;
    private EditText advertising_time_end_1_EditText;
    private EditText advertising_time_start_2_EditText;
    private EditText advertising_time_end_2_EditText;
    private EditText advertising_time_start_3_EditText;
    private EditText advertising_time_end_3_EditText;
    private LinearLayout advertising_1_Layout;
    private LinearLayout advertising_2_Layout;
    private LinearLayout advertising_3_Layout;
    private boolean isNewVersion = false;

    /*private int advertising_time_start_1;
    private int advertising_time_end_1;
    private int advertising_time_start_2;
    private int advertising_time_end_2;
    private int advertising_time_start_3;
    private int advertising_time_end_3;*/



    // 接收到广播之后的响应
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
                // 连接成功后
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                // 断开连接后
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
                mBluetoothLeService.disconnect();
                ShowToastShort(getResources().getString(R.string.disconnected_toast));
                onBackPressed();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // 显示服务与属性
                displayGattServices(mBluetoothLeService.getSupportedGattServices());

                //在此处添加连接后要读取的属性
                // 自动读取序列号
                //mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("Serial Number"));

                // 读取设置
                showRequestDialog(getResources().getString(R.string.reading_data));
                mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("setting"));
                operationType = READ_DEVICE;

                // 读取运动数据
                //mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("exerciseData"));

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // 显示数据
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

            } else if (BluetoothLeService.READ_ACTION.equals(action)) {
                closeRequestDialog();
                Log.i(TAG, "READ_ACTION-ljp");
                // ShowToastShort(intent.getStringExtra(BluetoothLeService.MY_EXTRA_DATA));
                String tempString = intent.getStringExtra(BluetoothLeService.MY_EXTRA_DATA);
                String[] strings = tempString.split("_");
                deviceIdEditText.setText(strings[0]);
                deviceTimeTextView.setText(strings[1]);
                deviceSumEditText.setText(strings[2]);
                mobileTime.setText(TimeHelper.getMobileTime());

                if (strings.length > 3) {
                    isNewVersion = true;
                    advertising_time_start_1_EditText.setText(strings[3]);
                    advertising_time_end_1_EditText.setText(strings[4]);
                    advertising_time_start_2_EditText.setText(strings[5]);
                    advertising_time_end_2_EditText.setText(strings[6]);
                    advertising_time_start_3_EditText.setText(strings[7]);
                    advertising_time_end_3_EditText.setText(strings[8]);

                    advertising_1_Layout.setVisibility(View.VISIBLE);
                    advertising_2_Layout.setVisibility(View.VISIBLE);
                    advertising_3_Layout.setVisibility(View.VISIBLE);
                } else {
                    isNewVersion = false;

                }
                //testEditText.setText(tempString);
                /*
				 * String arrayString[]=tempString.split("-"); for (String
				 * string : arrayString) { ShowToastShort(string); }
				 */
                //testButton.setEnabled(true);
                //把所支持的属性都推送至Service
                mBluetoothLeService.setAllCharacteristics(allCharacteristics);

            } else if (BluetoothLeService.SET_ACTION.equals(action)) {
                closeRequestDialog();
                String tempString = intent.getStringExtra(BluetoothLeService.MY_EXTRA_DATA);
                if (tempString.equals("true")) {
                    showToastDialog(DeviceControlActivity.this, getResources().getString(R.string.tip_submit_ok));
                    deviceIdEditText.setText(newSettingStrings[0]);
                    deviceTimeTextView.setText(newSettingStrings[1]);
                    deviceSumEditText.setText(newSettingStrings[2]);
                    if (isNewVersion) {
                        advertising_time_start_1_EditText.setText(newSettingStrings[3]);
                        advertising_time_end_1_EditText.setText(newSettingStrings[4]);
                        advertising_time_start_2_EditText.setText(newSettingStrings[5]);
                        advertising_time_end_2_EditText.setText(newSettingStrings[6]);
                        advertising_time_start_3_EditText.setText(newSettingStrings[7]);
                        advertising_time_end_3_EditText.setText(newSettingStrings[8]);
                    }
                } else {
                    showToastDialog(DeviceControlActivity.this, getResources().getString(R.string.tip_submit_fail));
                }

            } else if (BluetoothLeService.SYNC_ACTION.equals(action)) {

                //Log.i(TAG, "SYNC_ACTION-ljp");
                // ShowToastShort(intent.getStringExtra(BluetoothLeService.MY_EXTRA_DATA));
                //String tempString = intent.getStringExtra(BluetoothLeService.MY_EXTRA_DATA);

                /*ExerciseData exerciseData = (ExerciseData) intent.getSerializableExtra(BluetoothLeService.MY_EXTRA_DATA);
                if (exerciseData.getIsEmpty()) {
                    closeRequestDialog();

                    endTime = new Date().getTime();
                    ShowToastShort("同步完成" + syncCounts);
                    syncStringBuilder.insert(0, "共" + syncCounts + "组数据，耗时 " + (endTime - startTime) + " ms\r\n");
                    syncDataTextView.setText(syncStringBuilder.toString());
                    wakeLock.release();

                } else {
                    //Log.i(TAG, exerciseData.toString());
                    syncStringBuilder.append(exerciseData.toString()).append("\r\n");
                    syncCounts++;
                    //syncHandler.post(syncThread);

                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                        fileOutputStream.write(syncStringBuilder.toString().getBytes());
                        fileOutputStream.close();

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("exerciseData"));
                    //operationType=SYNC_DEVICE;


                }*/

                closeRequestDialog();
                endTime = new Date().getTime();
                ArrayList<ExerciseData> arrayList = (ArrayList<ExerciseData>) intent.getSerializableExtra("ArrayList");
                syncCounts = arrayList.size();
                for (ExerciseData e : arrayList) {
                    sb.append(e.toString()).append("\r\n");
                }
                try {
                    file = new File(android.os.Environment.getExternalStorageDirectory() + "/BLE/" + arrayList.get(0).getDevice_id() + "_" + TimeHelper.getDatetime() + ".txt");
                    FileOutputStream fileOutputStream = new FileOutputStream(file, false);
                    fileOutputStream.write(sb.toString().getBytes());
                    //fileOutputStream.write(arrayList.toString().getBytes());
                    fileOutputStream.close();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                sb.insert(0, "共" + syncCounts + "组数据，耗时 " + (endTime - startTime) + " ms\r\n");

                ShowToastShort("同步完成" + syncCounts);

                syncDataTextView.setText(sb.toString());
                wakeLock.release();

                backFile = new File(android.os.Environment.getExternalStorageDirectory() + "/BLE/" + arrayList.get(0).getDevice_id() + "_backup" + ".txt");
                if (backFile.exists())
                    backFile.delete();

            }

        }
    };
    //private Handler syncHandler;
    //private SyncThread syncThread = null;
    //private StringBuilder syncStringBuilder = null;
    private TextView syncDataTextView;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    private StringBuilder sb = null;

    //指定IntentFilter
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.READ_ACTION);
        intentFilter.addAction(BluetoothLeService.SET_ACTION);
        intentFilter.addAction(BluetoothLeService.SYNC_ACTION);
        return intentFilter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        this.initView();
        //服务列表
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        //mGattServicesList.setVisibility(View.GONE);
        getActionBar().setTitle(getResources().getString(R.string.device_setting));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //绑定服务
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void initView() {
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        mobileTime = (TextView) findViewById(R.id.mobile_time);
        mobileTime.setText(TimeHelper.getMobileTime());

        dir = new File(android.os.Environment.getExternalStorageDirectory() + "/BLE");
        if (!dir.exists())
            dir.mkdir();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WAKE");


        deviceIdEditText = (EditText) this.findViewById(R.id.editText_device_id);
        deviceSumEditText = (EditText) this.findViewById(R.id.editText_device_sum);
        deviceTimeTextView = (TextView) this.findViewById(R.id.device_time);
        syncDataTextView = (TextView) this.findViewById(R.id.exerciseDataTextview);

        advertising_time_start_1_EditText = (EditText) this.findViewById(R.id.editText_advertising_1_start);
        advertising_time_end_1_EditText = (EditText) this.findViewById(R.id.editText_advertising_1_end);
        advertising_time_start_2_EditText = (EditText) this.findViewById(R.id.editText_advertising_2_start);
        advertising_time_end_2_EditText = (EditText) this.findViewById(R.id.editText_advertising_2_end);
        advertising_time_start_3_EditText = (EditText) this.findViewById(R.id.editText_advertising_3_start);
        advertising_time_end_3_EditText = (EditText) this.findViewById(R.id.editText_advertising_3_end);
        advertising_1_Layout = (LinearLayout) this.findViewById(R.id.layout_advertising_1);
        advertising_2_Layout = (LinearLayout) this.findViewById(R.id.layout_advertising_2);
        advertising_3_Layout = (LinearLayout) this.findViewById(R.id.layout_advertising_3);

        deviceIdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    deviceSumEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        deviceSumEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    advertising_time_start_1_EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        advertising_time_start_1_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    advertising_time_end_1_EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        advertising_time_end_1_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    advertising_time_start_2_EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        advertising_time_start_2_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    advertising_time_end_2_EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        advertising_time_end_2_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    advertising_time_start_3_EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        advertising_time_start_3_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    advertising_time_end_3_EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });



        /*testEditText = (EditText) findViewById(R.id.editText1);
        testButton = (Button) findViewById(R.id.testBtuuon);
        testButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //testButton.setEnabled(false);
                // TODO Auto-generated method stub
                //ShowToastShort();
                *//*for (ArrayList<BluetoothGattCharacteristic> mGattCharacteristic : mGattCharacteristics) {
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : mGattCharacteristic) {
						Log.i(TAG, SampleGattAttributes.lookup(bluetoothGattCharacteristic.getUuid().toString(), "unknown")+"ljp");
					}
				}*//*

				*//*BluetoothGattCharacteristic testBluetoothGattCharacteristic= mGattCharacteristics.get(2).get(1);
                mBluetoothLeService.myReadCharacteristic(testBluetoothGattCharacteristic);*//*
                Iterator iterator = allCharacteristics.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Log.i(TAG, entry.getKey() + "ljp");
                }
                //mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("Serial Number"));

            }
        });*/

        readButton = (Button) findViewById(R.id.button_read);
        readButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showRequestDialog(getResources().getString(R.string.reading_data));
                operationType = READ_DEVICE;
                mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("setting"));

                //ShowToastShort("READing");
            }
        });

        submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkValid()) {
                    new AlertDialog.Builder(DeviceControlActivity.this).setTitle(getResources().getString(R.string.submit_confirm))
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    // TODO Auto-generated method stub

                                    /*  old version
                                    byte[] settings = generateNewSetting();
                                    */

                                    byte[] settings;
                                    if (isNewVersion) {
                                        settings = generateNewSetting2();
                                    } else {
                                        settings = generateNewSetting();
                                    }
                                    operationType = SET_DEVICE;
                                    mBluetoothLeService.myWriteCharacteristic(allCharacteristics.get("setting"), settings);

                                    //mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("setting"));
                                    //ShowToastShort("SUBMIT");
                                }
                            }).setNegativeButton(getResources().getString(R.string.cancel), null).show();
                }

            }
        });

        syncButton = (Button) findViewById(R.id.button_sync);
        syncButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showRequestDialog(getResources().getString(R.string.syncing));
                operationType = SYNC_DEVICE;
                mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("exerciseData"));
                syncCounts = 0;
                //syncHandler = new Handler();
                //syncThread = new SyncThread();
                //syncStringBuilder = new StringBuilder();
                sb = new StringBuilder();

                startTime = new Date().getTime();
                wakeLock.acquire();
                //file = new File(android.os.Environment.getExternalStorageDirectory() + "/BLE/" + TimeHelper.getDatetime() + ".txt");
            }
        });
    }

    // 断开连接后，清除列表
    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }


   @Override
    protected void onResume() {
        super.onResume();
        //注册广播接收
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "ljp_Connect request result=" + result);
        }
        mConnectionState.setFocusable(true);
        mConnectionState.requestFocus();
        mConnectionState.setFocusableInTouchMode(true);

   }

    //解除广播接收
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);

    }

    //解除广播接收,清空服务
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    //菜单的显示
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

    //菜单的点击操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //更新连接状态	
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    //显示具体数据
    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // 解析服务与属性
    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);				//未知服务
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);			//未知属性
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        allCharacteristics=new HashMap<String, BluetoothGattCharacteristic>();
        // 遍历每一个服务
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();			//某一个服务
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);		//currentService=<serviceName0,uuid0>
            gattServiceData.add(currentServiceData);		//服务List,循环结束后gattServiceData-> [<serviceName0,uuid0>,<serviceName1,uuid1>...]

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();		//取出某一个服务下边所有属性
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // 针对一个服务中的每一个属性进行操作
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);		//将该属性对象添加到名为charas的数组链表中
                // 对当前属性进行解析
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));	//currentCharaData=<charName0,uuid0>
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);		//一个服务下的所有属性组，<charName0,uuid0>,<charName1,uuid1>,<charName2,uuid2>

                allCharacteristics.put(SampleGattAttributes.lookup(uuid, unknownCharaString), gattCharacteristic);
            }
            mGattCharacteristics.add(charas);							//<<服务0-属性0,服务0-属性1,服务0-属性2>,<服务1-属性0,服务1-属性1,服务1-属性2>,<服务2-属性0,服务2-属性1,服务2-属性2,>>
            gattCharacteristicData.add(gattCharacteristicGroupData);	//<服务0中的所有属性组,服务1中的所有属性组,服务2中的所有属性组>
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
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
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    public void ShowToastShort(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private void showToastDialog(Context context, String tipString)
    {
        mDialog = DialogFactory.createToastDialog(context, tipString);
        mDialog.show();

        tipHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;

                }
            }
        }, DELAY_TIME);
    }

    private void showRequestDialog(String string) {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(this, string);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void closeRequestDialog() {
        if (mDialog != null)
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    //检查输入的合法性
    private boolean checkValid()
    {

        if (deviceIdEditText.length() == 0) {
            showToastDialog(DeviceControlActivity.this, getResources().getString(R.string.tip_noid));
            return false;
        }

        if (deviceSumEditText.length() == 0) {
            showToastDialog(DeviceControlActivity.this, getResources().getString(R.string.tip_nosum));
            return false;
        }

        if (isNewVersion) {
            if (advertising_time_end_1_EditText.length() == 0 || advertising_time_start_1_EditText.length() == 0 ||
                    advertising_time_end_2_EditText.length() == 0 || advertising_time_start_2_EditText.length() == 0 ||
                    advertising_time_end_3_EditText.length() == 0 || advertising_time_start_3_EditText.length() == 0) {
                showToastDialog(DeviceControlActivity.this, getResources().getString(R.string.tip_noadvertisingtime));
                return false;
            }
        }


        return true;

    }

    // 生成配置byte数组  old version
    private byte[] generateNewSetting()
    {
        byte[] settingByte = new byte[9];
        newSettingStrings = new String[3];
        // id
        String idString = deviceIdEditText.getText().toString().trim();
        newSettingStrings[0] = idString;
        idString = insertZero(toHEXString(idString), 8);
        String[] idStrings = new String[]{(String) idString.subSequence(6, 8), (String) idString.subSequence(4, 6), (String) idString.subSequence(2, 4), (String) idString.subSequence(0, 2)};
        for (int i = 0; i < idStrings.length; i++) {
            settingByte[i] = (byte) toValueInt(idStrings[i]);
        }

        // time
        String timeString = TimeHelper.dateToSecond();
        newSettingStrings[1] = TimeHelper.secondToDate(timeString);
        //String timeString="453302738";
        timeString = insertZero(toHEXString(timeString), 8);
        String[] timeStrings = new String[]{(String) timeString.subSequence(6, 8), (String) timeString.subSequence(4, 6), (String) timeString.subSequence(2, 4), (String) timeString.subSequence(0, 2)};
        for (int i = 0; i < timeStrings.length; i++) {
            settingByte[i + 4] = (byte) toValueInt(timeStrings[i]);
        }
        mobileTime.setText(TimeHelper.getMobileTime());

        // sum
        String sumString = deviceSumEditText.getText().toString().trim();
        newSettingStrings[2] = sumString;
        sumString = insertZero(toHEXString(sumString), 2);
        settingByte[8] = (byte) toValueInt(sumString);

        return settingByte;
    }

    // 生成配置byte数组  new version
    private byte[] generateNewSetting2() {
        byte[] settingByte = new byte[15];
        newSettingStrings = new String[9];
        // id
        String idString = deviceIdEditText.getText().toString().trim();
        newSettingStrings[0] = idString;
        idString = insertZero(toHEXString(idString), 8);
        String[] idStrings = new String[]{(String) idString.subSequence(6, 8), (String) idString.subSequence(4, 6), (String) idString.subSequence(2, 4), (String) idString.subSequence(0, 2)};
        for (int i = 0; i < idStrings.length; i++) {
            settingByte[i] = (byte) toValueInt(idStrings[i]);
        }

        // time
        String timeString = TimeHelper.dateToSecond();
        newSettingStrings[1] = TimeHelper.secondToDate(timeString);
        //String timeString="453302738";
        timeString = insertZero(toHEXString(timeString), 8);
        String[] timeStrings = new String[]{(String) timeString.subSequence(6, 8), (String) timeString.subSequence(4, 6), (String) timeString.subSequence(2, 4), (String) timeString.subSequence(0, 2)};
        for (int i = 0; i < timeStrings.length; i++) {
            settingByte[i + 4] = (byte) toValueInt(timeStrings[i]);
        }
        mobileTime.setText(TimeHelper.getMobileTime());

        // sum
        String sumString = deviceSumEditText.getText().toString().trim();
        newSettingStrings[2] = sumString;
        sumString = insertZero(toHEXString(sumString), 2);
        settingByte[8] = (byte) toValueInt(sumString);

        // advertising time
        newSettingStrings[3] = advertising_time_start_1_EditText.getText().toString().trim();
        newSettingStrings[4] = advertising_time_end_1_EditText.getText().toString().trim();
        newSettingStrings[5] = advertising_time_start_2_EditText.getText().toString().trim();
        newSettingStrings[6] = advertising_time_end_2_EditText.getText().toString().trim();
        newSettingStrings[7] = advertising_time_start_3_EditText.getText().toString().trim();
        newSettingStrings[8] = advertising_time_end_3_EditText.getText().toString().trim();

        settingByte[9] = (byte) toValueInt(insertZero(toHEXString(newSettingStrings[3]), 2));
        settingByte[10] = (byte) toValueInt(insertZero(toHEXString(newSettingStrings[4]), 2));
        settingByte[11] = (byte) toValueInt(insertZero(toHEXString(newSettingStrings[5]), 2));
        settingByte[12] = (byte) toValueInt(insertZero(toHEXString(newSettingStrings[6]), 2));
        settingByte[13] = (byte) toValueInt(insertZero(toHEXString(newSettingStrings[7]), 2));
        settingByte[14] = (byte) toValueInt(insertZero(toHEXString(newSettingStrings[8]), 2));

        return settingByte;
    }

    public String insertZero(String str, int len)
    {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < len) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }


    // 数组的反转
    /*public <T> T invertArray(T array) {  
        int len = Array.getLength(array);  
        Class<?> classz = array.getClass().getComponentType();  
        Object dest = Array.newInstance(classz, len);  
        System.arraycopy(array, 0, dest, 0, len);  
        Object temp;  
        for (int i = 0; i < (len / 2); i++) {  
            temp = Array.get(dest, i);  
            Array.set(dest, i, Array.get(dest, len - i - 1));  
            Array.set(dest, len - i - 1, temp);  
        }  
        return (T)dest;  
    }  */

    // 新建线程，同步数据
   /* class SyncThread extends Thread
    {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            //Log.i(TAG, "syncThread");
            mBluetoothLeService.myReadCharacteristic(allCharacteristics.get("exerciseData"));
            operationType = SYNC_DEVICE;
        }
    }*/

    //检查IP的合法性 
    /*private Boolean ipValid(String str)
    {
    	//Log.i(TAG, str);
    	String ipStrings[]=str.split("\\.");
    	
    	//Log.i(TAG, String.valueOf(ipStrings.length));
    	if (ipStrings.length!=4) {
			return false;
		}
    	int ip[]=new int[4];
    	try {    		
    		for (int i = 0; i < ip.length; i++) {
				ip[i]=Integer.valueOf(ipStrings[i]);
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
    	
    	for (int i : ip) {
			if (i>255 || i<0) {
				return false;
			}
		}
    	return true;
    }*/
}

