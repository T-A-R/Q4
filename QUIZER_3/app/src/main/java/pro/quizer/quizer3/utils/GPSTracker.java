package pro.quizer.quizer3.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;

public class GPSTracker extends Service implements LocationListener {

//    private final Activity mActivity;
    private final WeakReference<Activity> mActivity;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    boolean continueWithZeroLocation = false;
    Location location; // location
    double latitude = 0; // latitude
    double longitude = 0; // longitude
    long gpstime = 0; // time
    Location locationNetwork; // location
    double latitudeNetwork = 0; // latitude
    double longitudeNetwork = 0; // longitude
    long gpstimeNetwork = 0; // time
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
//    protected LocationManager locationManager;
    protected  LocationManager locationManager;

    public GPSTracker(Activity context) {
//        this.mActivity = context;
        this.mActivity = new WeakReference<>(context);
        getLocation();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mActivity.get()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            if (!isGPSEnabled && !isNetworkEnabled) {
            if (!isGPSEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestSingleUpdate(
                            LocationManager.NETWORK_PROVIDER, this, null);
                    if (locationManager != null) {
                        locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (locationNetwork != null) {
                            latitudeNetwork = locationNetwork.getLatitude();
                            longitudeNetwork = locationNetwork.getLongitude();
                            gpstimeNetwork = locationNetwork.getTime();
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                gpstime = location.getTime();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        if (location == null) {
        } else latitude = location.getLatitude();

        return latitude;
    }

    public double getLongitude() {
        if (location == null) {
        } else longitude = location.getLongitude();

        return longitude;
    }

    public double getLatitudeNetwork() {
        if (locationNetwork == null) {
        } else latitudeNetwork = locationNetwork.getLatitude();

        return latitudeNetwork;
    }

    public double getLongitudeNetwork() {
        if (locationNetwork == null) {
        } else longitudeNetwork = locationNetwork.getLongitude();

        return longitudeNetwork;
    }

    public long getGpsTime() {
        if (location == null) {
        } else gpstime = location.getTime();

        return gpstime;
    }

    public long getGpsTimeNetwork() {
        if (locationNetwork != null) {
            gpstimeNetwork = locationNetwork.getTime();
        }

        return gpstimeNetwork;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity.get(), R.style.AlertDialogTheme);
//        alertDialog.setCancelable(false);
//        alertDialog.setTitle(R.string.dialog_please_turn_on_gps);
//        alertDialog.setMessage(R.string.dialog_you_need_to_turn_on_gps);
//        alertDialog.setPositiveButton(R.string.dialog_turn_on, new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mActivity.get().startActivity(intent);
//            }
//        });
//
//        if (!mActivity.get().isFinishing()) {
//            alertDialog.show();
//        }
//    }

//    public void showNoGpsAlert(boolean isForceGps) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity.get(), R.style.AlertDialogTheme);
//        alertDialog.setCancelable(false);
//        alertDialog.setTitle(R.string.dialog_no_gps);
//        if (isForceGps) {
//            alertDialog.setMessage(R.string.dialog_no_gps_empty_text);
//            alertDialog.setPositiveButton(R.string.dialog_next, new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    continueWithZeroLocation = true;
//                    dialog.dismiss();
//                }
//            });
//            alertDialog.setNegativeButton(R.string.view_yes, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//        } else {
//            alertDialog.setMessage(R.string.dialog_no_gps_text_warning);
//            alertDialog.setPositiveButton(R.string.dialog_next, new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    continueWithZeroLocation = true;
//                    dialog.dismiss();
//                }
//            });
//        }
//        alertDialog.setNegativeButton(R.string.view_retry, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                getLocation();
//                dialog.dismiss();
//            }
//        });
//        if (!mActivity.get().isFinishing()) {
//            alertDialog.show();
//        }
//    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isFakeGPS() {
        if (location != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return location.isFromMockProvider();
            } else {
                String mockLocation = "0";
                try {
                    mockLocation = Settings.Secure.getString(mActivity.get().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return !mockLocation.equals("0");
            }
        } else {
            return false;
        }
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
    @Override
    public void onDestroy() {
        stopUsingGPS();
        super.onDestroy();
    }
}
