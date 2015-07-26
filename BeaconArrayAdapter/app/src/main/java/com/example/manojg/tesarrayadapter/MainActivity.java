package com.example.manojg.tesarrayadapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.example.manojg.tesarrayadapter.beacon;


public class MainActivity extends Activity{

    adapterActivity beaconAdapter;
    ListView listViewBeacon;
    ArrayList<beacon> beacons;
    beacon beaconClass;
    Context context=MainActivity.this;
    private BeaconManager beaconManager;
    private static final int NOTIFICATION_ID = 123;

    public static int count;
    public static Beacon BeaconToRange;
    private NotificationManager notificationManager;
    private Region region;
    private Region Oldregion;
    public List<Beacon> beaconsCopy;
    private static final String TAG =MainActivity.class.getSimpleName();
    public static String previousString;
    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listViewBeacon=(ListView)findViewById(R.id.listView);
        beaconClass=new beacon();
        beacons=beaconClass.getBeacons();
        beaconAdapter=new adapterActivity(context);
        listViewBeacon.setAdapter(beaconAdapter);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        //beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);


        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                if(beacons.size()>0) {
                    beaconsCopy = new ArrayList<Beacon>(beacons.size());
                    for (Beacon single : beacons) {
                        beaconsCopy.add(single);
                    }
                        Collections.sort(beaconsCopy, new Comparator<Beacon>() {
                            @Override
                            public int compare(Beacon x, Beacon y) {
                                // TODO: Handle null x or y values
                                return y.getRssi() - (x.getRssi());
                            }
                        });

                    String regionString = beaconsCopy.get(0).getProximityUUID() + ":" + beaconsCopy.get(0).getMajor();
                    try {
                        region = new Region("RID", beaconsCopy.get(0).getProximityUUID(), beaconsCopy.get(0).getMajor(), null);
                        if (!(regionString.equalsIgnoreCase(previousString))) {
                            count = 0;
//                            if(previousString!=null) {
//                                String[] previousBeaconValues = previousString.split(":");
//                                Oldregion = new Region("RID", previousBeaconValues[0], Integer.parseInt(previousBeaconValues[1]), null);
//                            }
                        } else {
                            count++;
                            if (count == 5) {
                                beaconManager.startMonitoring(region);
//                                if(Oldregion!=null)
//                                beaconManager.stopMonitoring(Oldregion);
                            }
                        }
                        previousString = beaconsCopy.get(0).getProximityUUID() + ":" + beaconsCopy.get(0).getMajor();
                    } catch (RemoteException e) {
                        Toast.makeText(MainActivity.this, "Cannot start ranging, something terrible happened",
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Cannot start ranging", e);
                    }
                }
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Note that beacons reported here are already sorted by estimated
                        // distance between device and beacon.
                        beaconAdapter.replaceWith(beaconsCopy);
                    }
                });
            }
        });


        region = new Region("rId",  null, null, null);

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {

            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
//                beaconsCopy = new ArrayList<Beacon>(beacons.size());
//                for (Beacon single: beacons) {
//                    beaconsCopy.add(single);
//                }
//                if (beacons.size() > 0)
//                    Collections.sort(beaconsCopy, new Comparator<Beacon>() {
//                        @Override
//                        public int compare(Beacon x, Beacon y) {
//                            // TODO: Handle null x or y values
//                            return y.getRssi() - (x.getRssi());
//                        }
//                    });
//                Log.i("Main Activity","Printing Beaconscopy "+beaconsCopy);
                Beacon enteredRegion=beaconsCopy.get(0);
                postNotification(String.format("Entered region Major: %d Minor: %d", enteredRegion.getMajor(), enteredRegion.getMinor()));
            }

            @Override
            public void onExitedRegion(Region region) {
                postNotification(String.format("Exited region Major: %d Minor: %d", region.getMajor(), region.getMinor()));
                try {
                    beaconManager.stopMonitoring(region);
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this, "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        notificationManager.cancel(NOTIFICATION_ID);
//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                try {
//                    beaconManager.startMonitoring(region);
//                } catch (RemoteException e) {
//                    Log.d(TAG, "Error while starting monitoring");
//                }
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }
    private void postNotification(String msg) {
        Intent notifyIntent = new Intent(MainActivity.this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                MainActivity.this,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(MainActivity.this)
                .setSmallIcon(R.drawable.area_000)
                .setContentTitle("Rendezvous Region")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);

        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
            beaconManager.stopMonitoring(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging and monitoring", e);
        }

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {
        beaconAdapter.replaceWith(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                    //beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this, "Cannot start ranging, something terrible happened",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }


}
