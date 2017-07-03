package com.wso2.glass.picavi.picaviglass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wso2.glass.picavi.picaviglass.services.DeviceManagementService;
import com.wso2.glass.picavi.picaviglass.util.LocalRegistry;

public class RegisteredActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        Intent serviceIntent = new Intent(this, DeviceManagementService.class);
        startService(serviceIntent);

        final Button unregisterBtn = (Button) findViewById(R.id.button);
        unregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregister();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean unregister() {
        if (!LocalRegistry.isExist(getApplicationContext())) {
            Intent activity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(activity);
        }
        LocalRegistry.setEnrolled(getApplicationContext(), false);
        LocalRegistry.removeUsername(getApplicationContext());
        LocalRegistry.removeDeviceId(getApplicationContext());
        LocalRegistry.removeServerURL(getApplicationContext());
        LocalRegistry.removeAccessToken(getApplicationContext());
        LocalRegistry.removeRefreshToken(getApplicationContext());
        LocalRegistry.removeMqttEndpoint(getApplicationContext());
        LocalRegistry.removeTenantDomain(getApplicationContext());
        LocalRegistry.setExist(false);

        //Stop current running background services.
        Intent myService = new Intent(this, DeviceManagementService.class);
        stopService(myService);

        Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        registerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerActivity);
        finish();
        return true;
    }

}
