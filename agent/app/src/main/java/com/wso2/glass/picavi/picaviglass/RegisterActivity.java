package com.wso2.glass.picavi.picaviglass;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.wso2.glass.picavi.picaviglass.util.PicaviClient;
import com.wso2.glass.picavi.picaviglass.util.PicaviUtils;
import com.wso2.glass.picavi.picaviglass.util.LocalRegistry;
import com.wso2.glass.picavi.picaviglass.util.dto.RegisterInfo;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class RegisterActivity extends Activity {

    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mHostView;
    private View mProgressView;
    private View mLoginFormView;
    private Button deviceRegisterButton;
    private Handler mUiHandler = new Handler();

    // QREader
    private SurfaceView mySurfaceView;
    private QREader qrEader;

    private TextView qrResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LocalRegistry.isExist(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), RegisteredActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_register);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mHostView = (EditText) findViewById(R.id.hostname);
        qrResponse = (TextView) findViewById(R.id.qrResponse);

        qrResponse.setVisibility(View.GONE);

        deviceRegisterButton = (Button) findViewById(R.id.device_register_button);

        deviceRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginFormView.setVisibility(View.GONE);
        mProgressView = findViewById(R.id.login_progress);

        // Setup SurfaceView
        // -----------------
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_view);

        final Handler handler = new Handler(getMainLooper());
        // Init QREader
        // ------------
        qrEader = new QREader.Builder(this, mySurfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
               Log.d("QREader", "Value : " + data);
                qrResponse.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isValidQRCode(data)) {
                            qrResponse.setText("Valid QR code is detected");
                            qrResponse.setVisibility(View.VISIBLE);
                            attemptLogin();
                        }
                    }
                });
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                    }
                });

            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(mySurfaceView.getHeight())
                .width(mySurfaceView.getWidth())
                .build();
    }

    public void attemptLogin() {
        showProgress(true);
        deviceRegisterButton.setVisibility(View.INVISIBLE);
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String hostname = mHostView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password)) {
            // mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            //cancel = true;
        }
        // Check for a valid username .
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(username)) {
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    PicaviClient client = new PicaviClient(getApplicationContext());
                    LocalRegistry.addServerURL(getBaseContext(), hostname);
                    String deviceId = PicaviUtils.generateDeviceId(getBaseContext(), getContentResolver());
                    final RegisterInfo registerStatus = client.register(username, password, deviceId);
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), registerStatus.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (registerStatus.isRegistered()) {
                        LocalRegistry.setEnrolled(getApplicationContext(), true);
                        LocalRegistry.addUsername(getApplicationContext(), username);
                        LocalRegistry.addDeviceId(getApplicationContext(), deviceId);
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), RegisteredActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            deviceRegisterButton.setVisibility(View.VISIBLE);
                            showProgress(false);
                        }
                    });

                }
            });
            myThread.start();

        }
    }

    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            }
//        });

        qrResponse.setVisibility(show ? View.VISIBLE : View.GONE);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Init and Start with SurfaceView
        // -------------------------------
        qrEader.initAndStart(mySurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cleanup in onPause()
        // --------------------
        qrEader.releaseAndCleanup();
    }

    private boolean isValidQRCode(String value) {
        if (value != null) {
            String values[] = value.split(",");
            if (values.length == 3) {
                mHostView.setText(values[0].trim());
                mUsernameView.setText(values[1].trim());
                mUsernameView.setText(values[2].trim());
                return true;
            }
        }
        return false;
    }

}

