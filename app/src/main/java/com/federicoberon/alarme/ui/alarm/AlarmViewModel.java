package com.federicoberon.alarme.ui.alarm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
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
    private Horoscope horoscope;
    private OnResponseUpdateListener listener;
    private HoroscopeTwo horoscopeTwo;
    private WeatherResponse weatherResponse;

    public double latitude;
    public double longitude;
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
        this.isPreview = false;
        inCelsius = true;
        latitude = 0;
        longitude = 0;
    }

    public void init(OnResponseUpdateListener listener, String sign) {
        this.listener = listener;
        this.sign = sign;
    }

    public String getSign() {
        return this.sign;
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

    public boolean coordsCached(){
        return latitude != 0 || longitude !=0;
    }

    public void callWeatherAPI(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;

        if (lat != 0.0 || lon != 0.0) {
            AlarMe application = AlarMe.get(context);
            WeatherService weatherService = application.getWeatherService();

            weatherService.getWeather("576d14184a3e42cc8cd10015222203", new double[]{lat, lon}, 1)
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
                            callWeatherAPITwo(lat, lon);
                            Log.e(TAG, "Error loading weather ", t);
                            listener.onWeatherChanged(null);
                        }
                    });
        }
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
                        if (listener != null){
                            listener.onHoroscopeChanged(horoscope);
                        }
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
