package com.federicoberon.alarme.ui.alarm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.federicoberon.alarme.AlarMe;
import com.federicoberon.alarme.repositories.AlarmRepository;
import com.federicoberon.alarme.retrofit.Horoscope;
import com.federicoberon.alarme.retrofit.HoroscopeService;
import com.federicoberon.alarme.retrofit.HoroscopeServiceTwo;
import com.federicoberon.alarme.retrofit.HoroscopeTwo;
import com.federicoberon.alarme.retrofit.WeatherResponse;
import com.federicoberon.alarme.retrofit.WeatherResponseTwo;
import com.federicoberon.alarme.retrofit.WeatherService;
import com.federicoberon.alarme.retrofit.WeatherServiceTwo;
import com.federicoberon.alarme.utils.HoroscopeManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmViewModel extends ViewModel {

    private static final String TAG = "AlarmViewModel";
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    public boolean isPreview;
    private String sign;
    private final AlarmRepository mAlarmRepository;
    private Horoscope horoscope;
    private OnResponseUpdateListener listener;
    private HoroscopeTwo horoscopeTwo;
    private WeatherResponse weatherResponse;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherResponseTwo weatherResponseTwo;
    private Double currentTempF;
    private double currentTempC;
    private double maxTempC;
    private double minTempC;
    private double minTempF;
    private double maxTempF;
    private boolean inCelsius;

    @Inject
    public AlarmViewModel(Application app, AlarmRepository alarmRepository) {
        this.context = ((AlarMe) app).appComponent.getContext();
        this.mAlarmRepository = alarmRepository;
        this.isPreview = false;
        inCelsius = true;
    }

    public void init(OnResponseUpdateListener listener, String sign) {
        this.listener = listener;
        this.sign = sign;
    }

    public String getSign() {
        return this.sign;
    }

    // todo creo que se puede borrar
    @SuppressLint("MissingPermission")
    public void loadWeather(AlarmActivity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (checkLocationPermissions())
            fusedLocationClient.getLastLocation()
            .addOnCompleteListener(activity, task -> {
                if (task.isSuccessful() && task.getResult() != null) {

                    Log.w("MIO", "<<< Ya teno una ubicacion >>>");
                    location = task.getResult();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    callWeatherAPI(location.getLatitude(), location.getLongitude());
                    //callWeatherAPITwo(location.getLatitude(), location.getLongitude());

                } else {
                    Log.w("MIO", "<<< Pido una nueva ubicacion >>>");
                    requestNewLocation();
                }
            });

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocation() {

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    Log.w("MIO", "<<< locationResult es NULL >>>");
                    return;
                }
                Log.w("MIO", "<<< locationResult NO es  NULL >>>");
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                fusedLocationClient.removeLocationUpdates(this);
                callWeatherAPI(latitude, longitude);
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkLocationPermissions())
            fusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private boolean checkLocationPermissions() {
        // check locaiton permissions
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            Log.e("MIO" ,"<<< NO HAY PERMISOS >>>");
            return false;
        }

        // check google service installed
        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
                != ConnectionResult.SUCCESS) {
            Log.e("MIO", "<<< NO TIENE LOS SERVICIOS INSTALADOS >>>");
            return false;
        }
        return true;
    }

    private void callWeatherAPITwo(double latitude, double longitude) {
        AlarMe application = AlarMe.get(context);
        WeatherServiceTwo weatherService = application.getWeatherServiceTwo();

        weatherService.getWeather(latitude, longitude,"minutely,hourly,alerts", "imperial", "33b26b2199a99f5ddb67b87ce114460a")
                .enqueue(new Callback<WeatherResponseTwo>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponseTwo> call,
                                           @NonNull Response<WeatherResponseTwo> response) {
                        if (response.body() != null) {
                            if (response.isSuccessful()) {
                                AlarmViewModel.this.weatherResponseTwo = response.body();
                                if (listener != null)
                                    listener.onWeatherChangedTwo(weatherResponseTwo);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponseTwo> call, @NonNull Throwable t) {
                        Log.e(TAG, "Error loading weather ", t);
                        listener.onWeatherChanged(null);
                    }
                });
    }

    public void callWeatherAPI(double latitude, double longitude) {
        AlarMe application = AlarMe.get(context);
        WeatherService weatherService = application.getWeatherService();

        weatherService.getWeather("576d14184a3e42cc8cd10015222203", new double[]{latitude, longitude}, 1)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherResponse> call,
                                           @NonNull Response<WeatherResponse> response) {
                        if (response.body() != null) {
                            if (response.isSuccessful()) {
                               AlarmViewModel.this.weatherResponse = response.body();
                                if (listener != null)
                                    listener.onWeatherChanged(weatherResponse);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                        callWeatherAPITwo(latitude, longitude);
                        Log.e(TAG, "Error loading weather ", t);
                        listener.onWeatherChanged(null);
                    }
                });
    }

    public void loadHoroscope() {
        AlarMe application = AlarMe.get(context);
        HoroscopeService horoscopeService = application.getHoroscopeService();

        horoscopeService.getHoroscope(HoroscopeManager.getNameURL(context, sign), "today")
                .enqueue(new Callback<Horoscope>() {
            @Override
            public void onResponse(@NonNull Call<Horoscope> call,
                                   @NonNull Response<Horoscope> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        AlarmViewModel.this.horoscope = response.body();
                        if (listener != null)
                            listener.onHoroscopeChanged(horoscope);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Horoscope> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading horoscope ", t);
                listener.onHoroscopeChanged(null);
            }
        });
    }

    public void loadHoroscopeTwo() {
        AlarMe application = AlarMe.get(context);
        HoroscopeServiceTwo horoscopeServiceTwo = application.getHoroscopeServiceTwo();

        horoscopeServiceTwo.getHoroscope(HoroscopeManager.getNameURLTwo(context, sign))
                .enqueue(new Callback<HoroscopeTwo>() {
            @Override
            public void onResponse(@NonNull Call<HoroscopeTwo> call,
                                   @NonNull Response<HoroscopeTwo> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        AlarmViewModel.this.horoscopeTwo = response.body();
                        if (listener != null)
                            listener.onHoroscopeChangedTwo(horoscopeTwo);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HoroscopeTwo> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading horoscope ", t);
                listener.onHoroscopeChanged(null);
            }
        });
    }

    public void setCurrentTempF(Double currentTempF) {
        this.currentTempF = currentTempF;
    }

    public void setCurrentTempC(double currentTempC) {
        this.currentTempC = currentTempC;
    }

    public void setMinTempC(double minTempC) {
        this.minTempC = minTempC;
    }

    public void setMaxTempC(double maxTempC) {
        this.maxTempC = maxTempC;
    }

    public void setMinTempF(double minTempF) {
        this.minTempF = minTempF;
    }

    public void setMaxTempF(double maxTempF) {
        this.maxTempF = maxTempF;
    }

    public boolean isInCelsius() {
        return this.inCelsius;
    }

    public void changeInCelsius(){
        this.inCelsius = !this.inCelsius;
    }

    public double getCurrentTempF() {
        return currentTempF;
    }

    public double getCurrentTempC() {
        return currentTempC;
    }

    public double getMaxTempC() {
        return maxTempC;
    }

    public double getMinTempC() {
        return minTempC;
    }

    public double getMinTempF() {
        return minTempF;
    }

    public double getMaxTempF() {
        return maxTempF;
    }

    public interface OnResponseUpdateListener {
        void onHoroscopeChanged(Horoscope horoscope);
        void onHoroscopeChangedTwo(HoroscopeTwo horoscope);

        void onWeatherChanged(WeatherResponse weatherResponse);
        void onWeatherChangedTwo(WeatherResponseTwo weatherResponse);
    }
}
