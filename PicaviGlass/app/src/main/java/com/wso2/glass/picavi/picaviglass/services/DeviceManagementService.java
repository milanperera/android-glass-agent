/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.wso2.glass.picavi.picaviglass.services;

import android.app.Service;

import android.content.Intent;

import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.wso2.glass.picavi.picaviglass.MessageActivity;

import com.wso2.glass.picavi.picaviglass.constants.Constants;
import com.wso2.glass.picavi.picaviglass.mqtt.AndroidTVMQTTHandler;
import com.wso2.glass.picavi.picaviglass.mqtt.MessageReceivedCallback;
import com.wso2.glass.picavi.picaviglass.mqtt.transport.TransportHandlerException;
import com.wso2.glass.picavi.picaviglass.util.PicaviUtils;
import com.wso2.glass.picavi.picaviglass.util.LocalRegistry;

import java.util.Calendar;



public class DeviceManagementService extends Service {

    private static final String TAG = DeviceManagementService.class.getSimpleName();

    private AndroidTVMQTTHandler androidTVMQTTHandler;
    private static volatile String serialOfCurrentEdgeDevice = "";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        androidTVMQTTHandler = new AndroidTVMQTTHandler(this, new MessageReceivedCallback() {
            @Override
            public void onMessageReceived(JSONObject message) throws JSONException {
                performAction(message.getString("action"), message.getString("payload"));
            }
        });
        androidTVMQTTHandler.connect();
    }

    @Override
    public void onDestroy() {
        if (androidTVMQTTHandler != null && androidTVMQTTHandler.isConnected()) {
            androidTVMQTTHandler.disconnect();
        }
        androidTVMQTTHandler = null;
    }

    private void performAction(String action, String payload) {
        switch (action) {
            case "message":
                startActivity(MessageActivity.class, payload);
                break;
        }
    }



    private void startActivity(Class<?> cls, String extra) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(Constants.MESSAGE, extra);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



    private void sendATResponse(String message) {
        try {
            JSONObject jsonEvent = new JSONObject();
            JSONObject jsonMetaData = new JSONObject();
            jsonMetaData.put("owner", LocalRegistry.getUsername(getApplicationContext()));
            jsonMetaData.put("deviceId", getDeviceId());
            jsonMetaData.put("deviceType", Constants.DEVICE_TYPE);
            jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
            jsonEvent.put("metaData", jsonMetaData);

            JSONObject payload = new JSONObject();
            payload.put("serial", serialOfCurrentEdgeDevice);
            payload.put("at_response", message);
            jsonEvent.put("payloadData", payload);

            JSONObject wrapper = new JSONObject();
            wrapper.put("event", jsonEvent);
            if (androidTVMQTTHandler.isConnected()) {
                androidTVMQTTHandler.publishDeviceData(wrapper.toString());
            }
        } catch (TransportHandlerException | JSONException e) {
            Log.e(TAG, e.getClass().getSimpleName(), e);
        }
    }

    private void publishStats(String topic, String key, float value) {
        try {
            JSONObject jsonEvent = new JSONObject();
            JSONObject jsonMetaData = new JSONObject();
            jsonMetaData.put("owner", LocalRegistry.getUsername(getApplicationContext()));
            jsonMetaData.put("deviceId", getDeviceId());
            jsonMetaData.put("deviceType", Constants.DEVICE_TYPE);
            jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
            jsonEvent.put("metaData", jsonMetaData);

            JSONObject payload = new JSONObject();
            payload.put(key, value);
            jsonEvent.put("payloadData", payload);

            JSONObject wrapper = new JSONObject();
            wrapper.put("event", jsonEvent);
            if (androidTVMQTTHandler.isConnected()) {
                androidTVMQTTHandler.publishDeviceData(wrapper.toString(), topic);
            }
        } catch (TransportHandlerException | JSONException e) {
            Log.e(TAG, e.getClass().getSimpleName(), e);
        }
    }

    public void displayAlert(boolean ac, boolean window, boolean light){
        String alertMsg = "Please ";
        if (window) {
            alertMsg += "close the window ";
        }
        if (ac || light) {
            if (window) {
                alertMsg += "and ";
            }
            alertMsg += "turn off ";
            if (ac) {
                alertMsg += "AC ";
            }
            if (ac && light) {
                alertMsg += "and ";
            }
            if (light) {
                alertMsg += "Light ";
            }
        }
        alertMsg += "before leaving the room.";
        startActivity(MessageActivity.class, alertMsg);
    }

    private String getDeviceId() {
        return PicaviUtils.generateDeviceId(getBaseContext(), getContentResolver());
    }


}
