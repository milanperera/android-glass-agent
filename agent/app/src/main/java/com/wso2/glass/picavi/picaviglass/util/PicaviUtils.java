/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */
package com.wso2.glass.picavi.picaviglass.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class PicaviUtils {

    /**
     * this generate the device Id
     */
    //http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
    @SuppressLint("HardwareIds")
    public static String generateDeviceId(Context baseContext, ContentResolver contentResolver) {

        final TelephonyManager tm = (TelephonyManager) baseContext.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = String.valueOf(122345);
        tmSerial = String.valueOf(11111111);
        androidId = String.valueOf(121212121);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();

    }
}
