package com.example.locationtrackerapp;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SaveGPX {
    private static final String TAG = SaveGPX.class.getName();

    public SaveGPX() {

    }

    public static void writePath(File file, String n, List<Location> points){

        //FORMAT OF GPX FILE
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        String name = "<name>" + n + "</name><trkseg>\n";

        String segments = "";
        for(Location l:points){
            segments += "<trkpt lat=\"" + l.getLatitude() +
                    "\" lon=\"" + l.getLongitude() +
                    "\">" + "</trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try{
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();

            Log.i(TAG, "Saved" + points.size() + "points.");
        }catch (IOException e){
            Log.e(TAG, "Error writing the path", e);
        }
    }
}

