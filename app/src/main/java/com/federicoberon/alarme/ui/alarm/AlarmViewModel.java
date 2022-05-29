package com.federicoberon.alarme.ui.alarm;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.federicoberon.alarme.api.Horoscope;
import com.federicoberon.alarme.api.HoroscopeService;
import com.federicoberon.alarme.api.HoroscopeServiceTwo;
import com.federicoberon.alarme.api.HoroscopeTwo;
import com.federicoberon.alarme.api.WeatherResponse;
import com.federicoberon.alarme.api.WeatherResponseTwo;
import com.federicoberon.alarme.api.WeatherService;
import com.federicoberon.alarme.api.WeatherServiceTwo;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.repositories.AlarmRepository;

import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public class AlarmViewModel extends ViewModel {
    private final WeatherService weatherService;
    private final WeatherServiceTwo weatherServiceTwo;
    private final AlarmRepository alarmRepository;
    private final HoroscopeService horoscopeService;
    private final HoroscopeServiceTwo horoscopeServiceTwo;
    public boolean isPreview;
    private String sign;
    public double latitude;
    public double longitude;
    private Double currentTempF;
    private double currentTempC;
    private double maxTempC;
    private double minTempC;
    private double minTempF;
    private double maxTempF;
    private boolean inCelsius;
    private Locale locale;

    @Inject
    public AlarmViewModel(WeatherService weatherService, WeatherServiceTwo weatherServiceTwo,
                          HoroscopeService horoscopeService, HoroscopeServiceTwo horoscopeServiceTwo,
                          AlarmRepository alarmRepository) {
        this.weatherService = weatherService;
        this.weatherServiceTwo = weatherServiceTwo;
        this.alarmRepository = alarmRepository;
        this.horoscopeService = horoscopeService;
        this.horoscopeServiceTwo = horoscopeServiceTwo;
        this.isPreview = false;
        inCelsius = true;
        latitude = 0;
        longitude = 0;
    }

    public void init(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return this.sign;
    }

    public Observable<WeatherResponseTwo> callWeatherAPITwo(double latitude, double longitude) {
        try{
            return weatherServiceTwo.getWeather(latitude, longitude,"minutely,hourly,alerts"
                    , "imperial", "33b26b2199a99f5ddb67b87ce114460a");
        }catch (Exception e){
            return Observable.empty();
        }

    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean cordsCached(){
        return latitude != 0 || longitude !=0;
    }

    @SuppressLint("CheckResult")
    public Observable<WeatherResponse> callWeatherAPI(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
        if (lat != 0.0 || lon != 0.0){
            try{
                return weatherService.getWeather("576d14184a3e42cc8cd10015222203"
                        , new double[]{lat, lon}, 1);
            }catch (Exception e){
                return Observable.empty();
            }
        }
        return Observable.empty();
    }

    public Observable<Horoscope> loadHoroscope(String _sign) {
        try{
            return horoscopeService.getHoroscope(_sign, "today");
        }catch (Exception e){
            Log.w("ERROR", e.getMessage());
            return Observable.empty();
        }
    }

    public Observable<HoroscopeTwo> loadHoroscopeTwo(String _sign) {
        try{
            return horoscopeServiceTwo.getHoroscope(_sign);
        }catch (Exception e){
            Log.w("ERROR", "HUBO UN ERROR " + e.getMessage());
            return Observable.empty();
        }

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

    public Flowable<AlarmEntity> getAlarmById(long id) {
        return alarmRepository.getAlarmById(id);
    }
}
