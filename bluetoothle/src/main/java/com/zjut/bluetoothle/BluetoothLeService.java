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

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import static com.zjut.bluetoothle.Constants.*;
/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {

    public final static String ACTION_GATT_CONNECTED = "com.zjut.bluetoothlegatt.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.zjut.bluetoothlegatt.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.zjut.bluetoothlegatt.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.zjut.bluetoothlegatt.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.zjut.bluetoothlegatt.EXTRA_DATA";
    public final static String MY_EXTRA_DATA = "com.zjut.bluetoothlegatt.MY_EXTRA_DATA";
    public final static String READ_ACTION = "com.zjut.bluetoothlegatt.READ_ACTION";
    public final static String SET_ACTION = "com.zjut.bluetoothlegatt.SET_ACTION";
    public final static String SYNC_ACTION = "com.zjut.bluetoothlegatt.SYNC_ACTION";
    private final static String TAG = "BluetoothLeService";

    private static final int STATE_DISCONNECTED = 0;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private final IBinder mBinder = new LocalBinder();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    //public final static String MY_ACTION ="com.zjut.bluetoothlegatt.MY_ACTION";

    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private byte[] settings = null;


    private File file;
    private HashMap<String, BluetoothGattCharacteristic> allCharacteristics;

    //public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    private Boolean myMethod = false;
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                // 开始搜索服务
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (myMethod) {
                    switch (operationType) {

                        case READ_DEVICE:
                            broadcastUpdate(READ_ACTION, characteristic);
                            break;
                        case SET_DEVICE:
                            broadcastUpdate(SET_ACTION, characteristic);
                            break;

                        case SYNC_DEVICE:
                            broadcastUpdate(SYNC_ACTION, characteristic);
                            break;
                    }

                } else {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        /*@Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
        	Log.i(TAG, "Write complete-ljp");
        	broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }*/
    };
    private ArrayList<ExerciseData> exerciseDataArrayList;
    //同步是否完成的标记
    private boolean isSysnDone = false;

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);
        //Log.i(TAG, characteristic.getUuid().toString()+"-ljp");
        if (myMethod) {
            if (action.equals(READ_ACTION)) {
                byte[] readSetting = characteristic.getValue();
                String result = byteHelper(readSetting);
                intent.putExtra(MY_EXTRA_DATA, result);
            }

            if (action.equals(SET_ACTION)) {
                //Write
                characteristic.setValue(settings);
                boolean writeBoolean = mBluetoothGatt.writeCharacteristic(characteristic);
                intent.putExtra(MY_EXTRA_DATA, String.valueOf(writeBoolean));
                 /*characteristic.setValue(10000, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
             	mBluetoothGatt.writeCharacteristic(characteristic);
             	int second=0;
             	second=characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
             	Log.i(TAG, String.valueOf(second)+"ljpSecond2");
             	intent.putExtra(MY_EXTRA_DATA, String.valueOf(second)); 	*/
            }
            if (action.equals(SYNC_ACTION)) {
                byte[] exerciseDataByte = characteristic.getValue();
                //String exerciseString=exerciseDataHelper(exerciseDataByte);
                //intent.putExtra(MY_EXTRA_DATA, exerciseString);
                ExerciseData exerciseData = getExerciseData(exerciseDataByte);
                //intent.putExtra(MY_EXTRA_DATA, exerciseData);

                //Log.i(TAG,exerciseData.toString());
                if (!exerciseData.getIsEmpty()) {
                    exerciseDataArrayList.add(exerciseData);
                    myReadCharacteristic(allCharacteristics.get("exerciseData"));
                    isSysnDone = false;
                    /*try {
                        file = new File(android.os.Environment.getExternalStorageDirectory() + "/BLE/" +exerciseData.getDevice_id()+"_backup" + ".txt");
                        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                        fileOutputStream.write((exerciseData.toString()+"\r\n").getBytes());
                        fileOutputStream.close();

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }*/
                } else {
                    exerciseDataArrayList.trimToSize();
                    intent.putExtra("ArrayList", exerciseDataArrayList);
                    isSysnDone = true;
                }
            }

        } else {

            // This is special handling for the Heart Rate Measurement profile.  Data parsing is
            // carried out as per profile specifications:
            // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml

            if (characteristic.getUuid().toString().startsWith("0000fff4")) {
                //Log.i(TAG, String.valueOf(characteristic.getProperties())+"-ljp");

                //Log.i(TAG, characteristic.getStringValue(0)+"-ljpString");
                byte[] setting = characteristic.getValue();
                String result = byteHelper(setting);
                //int value=characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                //Log.i(TAG, String.valueOf(setting));
                intent.putExtra(EXTRA_DATA, result);
            } else if (characteristic.getUuid().toString().startsWith("0000fff3")) {
                int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.i(TAG, String.valueOf(value));
                intent.putExtra(EXTRA_DATA, String.valueOf(value));
            } else if (characteristic.getUuid().toString().startsWith("0000fff1")) {
                int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                Log.i(TAG, String.valueOf(value));
                intent.putExtra(EXTRA_DATA, String.valueOf(value));
            }
            /*else if (characteristic.getUuid().toString().startsWith("0000fff2")) {
				byte[] exerciseData=characteristic.getValue();

				for(int i=0;i<exerciseData.length;i++)
				{
					System.out.println(exerciseData[i]);
				}
			}*/

            /*else if(characteristic.getUuid().toString().startsWith("00002a19"))
            {
            	int batteryInfo=0;
            	batteryInfo=characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            	Log.i(TAG, String.valueOf(batteryInfo)+"-ljpBattery");
    			intent.putExtra(EXTRA_DATA, String.valueOf(batteryInfo));

    		}
            else if(characteristic.getUuid().toString().startsWith("00002a70"))
            {
            	int second=0;
            	//Read
            	second=characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            	Log.i(TAG, String.valueOf(second)+"ljpSecond");
            	intent.putExtra(EXTRA_DATA, String.valueOf(second));

            	//Write
            	characteristic.setValue(10000, BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            	mBluetoothGatt.writeCharacteristic(characteristic);
            	second=characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
            	Log.i(TAG, String.valueOf(second)+"ljpSecond2");
            	intent.putExtra(EXTRA_DATA, String.valueOf(second));

            }*/
           /* if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16;
                    Log.d(TAG, "Heart rate format UINT16.");
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    Log.d(TAG, "Heart rate format UINT8.");
                }
                final int heartRate = characteristic.getIntValue(format, 1);
                Log.d(TAG, String.format("Received heart rate: %d", heartRate));
                intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            } else {
                // For all other profiles, writes the data formatted in HEX.
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for(byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                }
            }*/
        }
        if (!intent.getAction().equals(SYNC_ACTION))
            sendBroadcast(intent);
        else if (isSysnDone)
            sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        exerciseDataArrayList = new ArrayList<ExerciseData>();
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        myMethod = false;
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    //我的读取方法
    public void myReadCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        myMethod = true;
        // Constants.isWrite=false;
        mBluetoothGatt.readCharacteristic(characteristic);
        //Log.i(TAG, "myReadCharacteristic+ljp");
    }

    //我的写入方法
    public void myWriteCharacteristic(BluetoothGattCharacteristic characteristic, byte[] settings) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        myMethod=true;
        //Constants.isWrite=true;
        this.settings = settings;
        mBluetoothGatt.readCharacteristic(characteristic);
        //Log.i(TAG, "myReadCharacteristic+ljp");
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        /*if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }*/
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public String byteHelper(byte[] values) {
        //String result=null;
        String[] strings = new String[9];

        for (int i = 0; i < values.length; i++) {
            int temp = values[i];
            if (temp < 0) {
                temp = temp + 256;
            }
            // 小于10时，需要在前边补0！
            strings[i] = insertZero(Constants.toHEXString(String.valueOf(temp)), 2);
        }

        String device_id = strings[3] + strings[2] + strings[1] + strings[0];
        device_id = Constants.toValueString(device_id);
        String device_time = strings[7] + strings[6] + strings[5] + strings[4];
        device_time = TimeHelper.secondToDate(Constants.toValueString(device_time));
        String device_sum = Constants.toValueString(strings[8]);
        return device_id + "_" + device_time + "_" + device_sum;
    }

    public String exerciseDataHelper(byte[] values) {
        String[] strings = new String[16];
        for(int i=0;i<values.length;i++)
        {
            int temp=values[i];
            if (temp<0) {
                temp=temp+256;
            }
            // 小于10时，需要在前边补0！
            strings[i]=insertZero(Constants.toHEXString(String.valueOf(temp)), 2);
        }
        String data_time = strings[3] + strings[2] + strings[1] + strings[0];
        data_time = TimeHelper.minuteToDate(toValueString(data_time));
        String device_id = Constants.toValueString(strings[5] + strings[4]);
        String data_energy = Constants.toValueString(strings[7] + strings[6]);
        String counts1 = Constants.toValueString(strings[9] + strings[8]);
        String counts2 = Constants.toValueString(strings[11] + strings[10]);
        String counts3 = Constants.toValueString(strings[13] + strings[12]);
        String counts4 = Constants.toValueString(strings[15] + strings[14]);
        if (device_id.equals("65535")) {
            return "NULL";
        }
        return data_time + "_" + device_id + "_" + data_energy + "_" + counts1 + "_" + counts2 + "_" + counts3 + "_" + counts4;
    }

    public ExerciseData getExerciseData(byte[] values)
    {
        String[] strings=new String[16];
        for(int i=0;i<values.length;i++)
        {
            int temp=values[i];
            if (temp<0) {
                temp=temp+256;
            }
            // 小于10时，需要在前边补0！
            strings[i]=insertZero(Constants.toHEXString(String.valueOf(temp)), 2);
        }
        ExerciseData exerciseData = new ExerciseData(
                TimeHelper.minuteToDate(toValueString(strings[3] + strings[2] + strings[1] + strings[0])),
                Constants.toValueString(strings[5] + strings[4]),
                Constants.toValueString(strings[7] + strings[6]),
                Constants.toValueString(strings[9] + strings[8]),
                Constants.toValueString(strings[11] + strings[10]),
                Constants.toValueString(strings[13] + strings[12]),
                Constants.toValueString(strings[15] + strings[14]));
        if (exerciseData.getDevice_id().equals("65535")) {
            exerciseData.setIsEmpty(true);
        } else {
            exerciseData.setIsEmpty(false);
        }
        return exerciseData;
    }

    public String insertZero(String str, int len) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < len) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    public HashMap<String, BluetoothGattCharacteristic> getAllCharacteristics() {
        return allCharacteristics;
    }

    public void setAllCharacteristics(HashMap<String, BluetoothGattCharacteristic> allCharacteristics) {
        this.allCharacteristics = allCharacteristics;
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
