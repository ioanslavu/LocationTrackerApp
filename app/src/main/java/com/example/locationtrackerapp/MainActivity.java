package com.example.locationtrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;


/* TO DO:
   1. Add Gradle: Location Dependency --> OK!
   2. Add Manifest: Location Permission --> OK!
   3. Add Manifest: LocationService for location background --> OK!
 */


public class MainActivity extends AppCompatActivity {

    // Create layout variables
    TextView tv_lat, tv_lon, tv_distance, tv_speed, tv_add;
    Button bt_stop, bt_start, bt_exit;



    private static final int PERMISSIONS_ACCESS_LOCATION = 99;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize and assign layout item
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_distance = findViewById(R.id.tv_dist);
        tv_speed = findViewById(R.id.tv_speed);
        tv_add = findViewById(R.id.tv_add);


        bt_start = findViewById(R.id.butt_start);
        bt_stop = findViewById(R.id.butt_stop);
        bt_exit = findViewById(R.id.butt_exit);

        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, request them
            Log.d("myLog", "onCreate: PERMISSION REQUEST");
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSIONS_ACCESS_LOCATION);
            //startLocationService();
        }

        // Button START SERVICE onClickListener
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Location Service started", Toast.LENGTH_SHORT).show();
                startLocationService();
                //IT IS GOING TO STORE THE LOCATIONS IN A LIST
                //storeLocations();
            }
        });

        // Button STOP SERVICE onClickListener
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
                stopLocationService();
            }
        });


        // Button EXIT onClickListener
        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Log.d("myLog", "bt_exit.setOnClickListener: PROGRAM EXIT.");
                Toast.makeText(MainActivity.this, "Program exit", Toast.LENGTH_SHORT).show();

            }
        });

    } // end onCreate



    private void startLocationService() {

        // Listen to Broadcast
        LocationBroadcastReceiver receiver = new LocationBroadcastReceiver();
        IntentFilter filter = new IntentFilter("ActionLocation");
        registerReceiver(receiver, filter);

        // Start Location Service
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);

    }

    private void stopLocationService() {

        Intent intent = new Intent(MainActivity.this, LocationService.class);
        stopService(intent);

        Log.d("myLog", "stopLocationService(): LOCATION SERVICE STOPPED." );

        tv_lat.setText("No GPS tracking");
        tv_lon.setText("No GPS tracking");
        tv_distance.setText("No GPS tracking");
        tv_speed.setText("No GPS tracking");
        tv_add.setText("No GPS Tracking");


    }

    private void updateValues (double lat, double lon, double speed, String address, float distance) {

        tv_lat.setText(String.valueOf(lat));
        tv_lon.setText(String.valueOf(lon));
        tv_distance.setText(String.valueOf(distance));
        tv_speed.setText(String.valueOf(speed));
        tv_add.setText(address);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_ACCESS_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("myLog", "onReq: startService");
                    startLocationService();
                } else {
                    Toast.makeText(this, "This App requires permission to be granted", Toast.LENGTH_SHORT).show();
                }

        }
    }

    public class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ActionLocation")) {

                // If the action matches the one from the Intent at the Sending Broadcast, then retrieve data
                double lat = intent.getDoubleExtra("latitude", 0f);
                double lon = intent.getDoubleExtra("longitude", 0f);
                double speed = intent.getDoubleExtra("speed", 0f);
                String address = intent.getStringExtra("address");

                /* TRY TO GET DISTANCE: Get distance from the Broadcast
                 */
                float distance = intent.getFloatExtra("distance",0f);


                /* TRY TO GET DISTANCE: updateValues(lat, lon, speed, address, distance); */
                updateValues(lat, lon, speed, address, distance);



            }
        }
    


}
