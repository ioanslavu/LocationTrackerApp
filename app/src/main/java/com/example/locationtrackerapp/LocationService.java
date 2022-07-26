package com.example.locationtrackerapp;

import static com.google.android.gms.location.Priority.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LocationService extends Service {

    private static final int PERMISSIONS_ACCESS_LOCATION = 99;

    // Create FusedLocationProviderClient
    FusedLocationProviderClient mFusedLocationProviderClient;

    // Create LocationCallback (for getting location updates)
    LocationCallback mLocationCallback;

    // Location variables
    double lat, lon, speed;
    List<Address> addresses;
    String address;
    float distance;
    Location prevLocation;

    String filePath = "";
    String file = "Location Tracker";  //current location
    Location currentLocation;

    //List of saved locations
    List<Location> savedLocations = new ArrayList<Location>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        Log.d("myLog", "LocationService: FUSED LOCATION PROVIDER CLIENT CREATED.");

        /* TRY TO GET DISTANCE: Initialize prevLocation with lat = 0, lon = 0
         */
        prevLocation = new Location(LocationServices.getFusedLocationProviderClient(this).toString());
        prevLocation.setLatitude(0);
        prevLocation.setLongitude(0);

        // Initialize LocationCallback: callback for receiving notifications from the FusedLocationProviderClient.
        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Save the location
                Location mLocation = locationResult.getLastLocation();

                /* TRY TO GET DISTANCE:
                If prevLocation does not match mLocation, then get the distance between both
                */

                if (prevLocation.equals(mLocation)) {
                    return;
                }
                distance = prevLocation.distanceTo(mLocation);
                prevLocation = mLocation;
                if(mLocation != null){
                    savedLocations.add(mLocation);
                    saveData("locationlog");
                }
                // Get GPS parameter values
                lat = mLocation.getLatitude();
                lon = mLocation.getLongitude();
                speed = mLocation.getSpeed();

                // Create a Geocoder to extract the Address text
                Geocoder mGeocoder = new Geocoder(getApplicationContext());
                try {
                    addresses = mGeocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    address = addresses.get(0).getAddressLine(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("myLog", "Lat: " + lat + "  Long: " + lon);

                // Send broadcast, so that Broadcast Receiver gets Location Updates
                Intent mIntent = new Intent("ActionLocation");

                mIntent.putExtra("latitude", lat);
                mIntent.putExtra("longitude", lon);
                mIntent.putExtra("speed", speed);
                mIntent.putExtra("address", address);
                /* TRY TO GET DISTANCE: putExtra to get data on Broadcast receiver
                 */
                mIntent.putExtra("distance", distance);

                sendBroadcast(mIntent);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* TO DO: Once the service is started, request location */

        Log.d("myLog", "LocationService.onStartcommand(): LOCATION SERVICE STARTED");
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestLocation() {
        /* TO DO: Create a Location Request, request for updates */

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(3 * 1000);
        mLocationRequest.setPriority(PRIORITY_HIGH_ACCURACY);

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Override
    public void onDestroy() {
        /* TO DO: Removes Location Updates when Location Services is stopped */
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }


    private boolean isExternalStorageAvailable(){
        String externalStorage = Environment.getExternalStorageState();
        if(externalStorage.equals(Environment.MEDIA_MOUNTED)){
            return true; //storage available
        }
        return false;
    }

    public void saveData(String file) {
        String fileName = file;
        if (isExternalStorageAvailable()) {
            //get the path to SD-Card
            File sdCard = Environment.getExternalStorageDirectory();
            //Add a new directory path to SD-Card
            File directory = new File(sdCard.getAbsolutePath() + "/LocationLogger/");
            directory.mkdir(); //create directory
            //creation of the file
            File f = new File(directory, fileName + ".gpx");

            SaveGPX gpxFile = new SaveGPX();

            try {
                f.createNewFile();
                gpxFile.writePath(f, fileName, savedLocations);
            } catch (Exception e) {
                Log.e("Writing File ", "Not completed writing" + f.getName());
                Log.e("Exception", String.valueOf(e));
            }
        }
    }
}








