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

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    //public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    //public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        /*attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
    	attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        attributes.put("000018f1-0000-1000-8000-00805f9b34fb", "Time Service");
        attributes.put("000018f2-0000-1000-8000-00805f9b34fb", "Exercise Data");*/
        attributes.put("0000f000-0000-1000-8000-00805f9b34fb", "Smart Exercise");
        // Sample Characteristics.
        //attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        /*attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        attributes.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Connection Parameter");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
        
        attributes.put("00002a70-0000-1000-8000-00805f9b34fb", "Time Second");
        attributes.put("00002a80-0000-1000-8000-00805f9b34fb", "X");
        attributes.put("00002a81-0000-1000-8000-00805f9b34fb", "Y");
        attributes.put("00002a82-0000-1000-8000-00805f9b34fb", "Z");
        attributes.put("00002a83-0000-1000-8000-00805f9b34fb", "MinData");
        attributes.put("00002a84-0000-1000-8000-00805f9b34fb", "MinCount");
        
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Info");
        
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "Certification Data List");
        attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");*/
        attributes.put("0000f001-0000-1000-8000-00805f9b34fb", "fff1");
        attributes.put("0000f002-0000-1000-8000-00805f9b34fb", "exerciseData");
        attributes.put("0000f003-0000-1000-8000-00805f9b34fb", "battery");
        attributes.put("0000f004-0000-1000-8000-00805f9b34fb", "setting");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    //是否存在特定的服务
    public static boolean existService(String uuid) {
        String name = attributes.get(uuid);
        return name == null ? false : true;
    }
}
