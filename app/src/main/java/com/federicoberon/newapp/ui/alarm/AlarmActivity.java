package com.federicoberon.newapp.ui.alarm;

import static com.federicoberon.newapp.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.databinding.FragmentAlarmBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.retrofit.Horoscope;
import com.federicoberon.newapp.retrofit.HoroscopeTwo;
import com.federicoberon.newapp.retrofit.WeatherResponse;
import com.federicoberon.newapp.retrofit.WeatherResponseTwo;
import com.federicoberon.newapp.service.AlarmService;
import com.federicoberon.newapp.service.ServiceUtil;
import com.federicoberon.newapp.utils.AlarmManager;
import com.federicoberon.newapp.utils.HoroscopeManager;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AlarmActivity extends AppCompatActivity implements AlarmViewModel.OnResponseUpdateListener {
    private static final String LOG_TAG = "AlarmActivity";
    private FragmentAlarmBinding binding;
    private AlarmEntity mAlarmEntity;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    SharedPreferences sharedPref;

    @Inject
    AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ((SimpleRemindMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);

        binding = FragmentAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        alarmViewModel.init(this,
                sharedPref.getString(getString(R.string.sign_name), "aries"));

        // ----- If extra alarmEntity not found then exit activity
        Intent intent = getIntent();
        if(intent.hasExtra(ALARM_ENTITY)) {
            mAlarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);
        }else {
            Toast.makeText(this, getString(R.string.no_alarm_entity),
                    Toast.LENGTH_LONG).show();
            finish();
        }

        // ----- Config dismiss alarm action
        binding.activityRingDismiss.setOnClickListener(v -> {
            if(!AlarmManager.recurring(mAlarmEntity)) {
                mAlarmEntity.setStarted(false);
                mDisposable.add(new ServiceUtil(this).updateAlarm(mAlarmEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> {
                        Log.w(LOG_TAG, "<<< DESACTIVWE LA ALARMA WEY >>> " + id);
                        finish();
                    },
                    throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));

                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
            }
        });

        // ----- Hide snooze button if the alarm isn't configured it for that
        int postpone_time;
        if(mAlarmEntity.isPostponeOn())
            postpone_time = mAlarmEntity.getPostponeTime();
        else {
            postpone_time = 0;
            binding.activityRingSnooze.setVisibility(View.GONE);
        }

        binding.activityRingSnooze.setOnClickListener(v -> {
            AlarmManager.schedule(getApplicationContext(),
                    AlarmManager.getSnoozedAlarm(mAlarmEntity, postpone_time));
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });

        if(mAlarmEntity.isHoroscopeOn())
            alarmViewModel.loadHoroscope();
        else
            binding.horoscopeCardView.setVisibility(View.GONE);

        if(mAlarmEntity.isWeatherOn())
            alarmViewModel.loadWeather(this);
        else
            binding.weatherCardView.setVisibility(View.GONE);

        binding.changeUnit.setOnClickListener(view -> {
            alarmViewModel.changeInCelsius();
            if(alarmViewModel.isInCelsius()){
                changeUnitColor(ContextCompat.getColor(AlarmActivity.this, R.color.white_transparent)
                        ,ContextCompat.getColor(AlarmActivity.this, R.color.white));
                changeUnit(alarmViewModel.getCurrentTempC(), alarmViewModel.getMinTempC(), alarmViewModel.getMaxTempC());
            }else{
                changeUnitColor(ContextCompat.getColor(AlarmActivity.this, R.color.white)
                        ,ContextCompat.getColor(AlarmActivity.this, R.color.white_transparent));
                changeUnit(alarmViewModel.getCurrentTempF(), alarmViewModel.getMinTempF(), alarmViewModel.getMaxTempF());
            }
        });
    }

    private void changeUnitColor(int fColor, int sColor) {
        binding.textViewCurrentTempF.setTextColor(fColor);
        binding.textViewCurrentTempC.setTextColor(sColor);
    }

    private void changeUnit(double currentTemp, double minTemp, double maxTemp) {
        binding.textViewCurrentTemp.setText(String.valueOf((int)currentTemp));
        binding.textViewDailyTemp.setText(String.format(getString(R.string.min_max_temp_c)
                ,(int)minTemp, (int)maxTemp));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onHoroscopeChanged(Horoscope horoscope) {
        if(horoscope!=null){
            loadHoroscopeInfo(horoscope.getDescription());
        }else{
            alarmViewModel.loadHoroscopeTwo();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadHoroscopeInfo(String description) {
        binding.signImage.setBackground(
                getDrawable(HoroscopeManager.getIconId(this, alarmViewModel.getSign())));

        binding.signTitle.setText(HoroscopeManager.getName(
                this, alarmViewModel.getSign()));

        binding.signDesc.animate().alpha(0f).setDuration(0)
            .setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (binding!=null) {
                        binding.signDesc.setText(description);
                        binding.signDesc.animate().alpha(1f).setDuration(600)
                                .start();
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
    }

    @Override
    public void onHoroscopeChangedTwo(HoroscopeTwo horoscope) {
        if(horoscope!=null){
            loadHoroscopeInfo(horoscope.getDescription());
        }else{
            binding.horoscopeCardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWeatherChanged(WeatherResponse weatherResponse) {
        if(weatherResponse!=null) {

            // current data
            alarmViewModel.setCurrentTempF((double) weatherResponse.getCurrentWeather().getTemp_f());
            alarmViewModel.setCurrentTempC(weatherResponse.getCurrentWeather().getTemp_c());

            binding.textViewCurrentWeather.setText(
                    weatherResponse.getCurrentWeather().getCondition().getCondition());
            binding.textViewCurrentTemp.setText(
                    String.valueOf((int) alarmViewModel.getCurrentTempC()));
            binding.imageViewIcon.setImageDrawable(getWeatherDrawableService(
                    weatherResponse.getCurrentWeather().getCondition().getId()));

            // forecast data
            alarmViewModel.setMinTempC(weatherResponse.getDayForecast().getForecastday().getDay().getMintemp_c());
            alarmViewModel.setMaxTempC(weatherResponse.getDayForecast().getForecastday().getDay().getMaxtemp_c());
            alarmViewModel.setMinTempF(weatherResponse.getDayForecast().getForecastday().getDay().getMintemp_f());
            alarmViewModel.setMaxTempF(weatherResponse.getDayForecast().getForecastday().getDay().getMaxtemp_f());

            binding.textViewDailyWeather.setText(
                    weatherResponse.getDayForecast().getForecastday().getDay().getCondition().getCondition());
            binding.textViewDailyTemp.setText(String.format(getString(R.string.min_max_temp_c)
                    ,(int)alarmViewModel.getMinTempC(),(int)alarmViewModel.getMaxTempC()));
            binding.imageViewDailyIcon.setImageDrawable(
                    getWeatherDrawableServiceTwo(weatherResponse.getDayForecast()
                            .getForecastday().getDay().getCondition().getId()));
            binding.textViewRainingChance.setText(String.format(getString(R.string.rain_chance),
                    weatherResponse.getDayForecast().getForecastday().getDay().getDaily_chance_of_rain()));

            binding.weatherCardView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onWeatherChangedTwo(WeatherResponseTwo weatherResponse) {
        // current data
        // (32 °F − 32) × 5/9 = 0 °C
        double tempC = (weatherResponse.current.temp-32)*5/9;
        alarmViewModel.setCurrentTempF(weatherResponse.current.temp);
        alarmViewModel.setCurrentTempC(tempC);

        binding.textViewCurrentWeather.setText(weatherResponse.current.weather.get(0).description);
        binding.textViewCurrentTemp.setText(String.valueOf((int) tempC));
        binding.imageViewIcon.setImageDrawable(getWeatherDrawableServiceTwo(weatherResponse.current.weather.get(0).id));

        // daily data
        double maxTempC=(weatherResponse.daily.get(0).temp.max-32)*5/9;
        double minTempC=(weatherResponse.daily.get(0).temp.min-32)*5/9;
        alarmViewModel.setMinTempC(minTempC);
        alarmViewModel.setMaxTempC(maxTempC);
        alarmViewModel.setMinTempF(weatherResponse.daily.get(0).temp.min);
        alarmViewModel.setMaxTempF(weatherResponse.daily.get(0).temp.max);

        binding.textViewDailyWeather.setText(weatherResponse.daily.get(0).dailyWeather.get(0).description);
        binding.textViewDailyTemp.setText(String.format(getString(R.string.min_max_temp_c)
                ,(int)minTempC,(int)maxTempC));
        binding.imageViewDailyIcon.setImageDrawable(getWeatherDrawableServiceTwo(weatherResponse.daily.get(0).dailyWeather.get(0).id));
        binding.textViewRainingChance.setVisibility(View.GONE);

        binding.weatherCardView.setVisibility(View.VISIBLE);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getWeatherDrawableService(int id) {
        List<Integer> rainIds = Arrays.asList(1192, 1195, 1201, 1246, 1252, 1255);
        List<Integer> rainyIds = Arrays.asList(1063, 1153, 1180, 1183, 1186, 1189, 1198, 1240, 1243, 1249);
        // muchas nubes
        List<Integer> cloudIds = Arrays.asList(1006, 1030, 1135, 1147);
        // pocas nubes
        List<Integer> cloudyIds = Arrays.asList(1003, 1009);

        List<Integer> snowIds = Arrays.asList(1066, 1069, 1072, 1114, 1117, 1150, 1171, 1168
                , 1204, 1207, 1210, 1213, 1216, 1219, 1222, 1225, 1237, 1258, 1261, 1264);

        List<Integer> thunderIds = Arrays.asList(1087, 1273, 1276, 1279);

        if(id == 1000)
            return getDrawable(R.drawable.sun);
        else if (rainIds.contains(id))
            return getDrawable(R.drawable.rain);
        else if (rainyIds.contains(id))
            return getDrawable(R.drawable.rainy);
        else if (cloudIds.contains(id))
            return getDrawable(R.drawable.clouds);
        else if (cloudyIds.contains(id))
            return getDrawable(R.drawable.cloudy);
        else if (thunderIds.contains(id))
            return getDrawable(R.drawable.storm);
        else if (snowIds.contains(id))
            return getDrawable(R.drawable.snow);
        else
            return getDrawable(R.drawable.cloudy);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getWeatherDrawableServiceTwo(int id) {
        if (id>801)
            return getDrawable(R.drawable.clouds);
        else if(id==800)
            return getDrawable(R.drawable.sun);
        else if(id==801)
            return getDrawable(R.drawable.cloudy);
        else if(id>=700)
            return getDrawable(R.drawable.windy);
        else if(id>=600)
            return getDrawable(R.drawable.snow);
        else if(id>=511)
            return getDrawable(R.drawable.rain);
        else if(id>=500)
            return getDrawable(R.drawable.rainy);
        else if(id>=300)
            return getDrawable(R.drawable.rain);
        else if(id>=200)
            return getDrawable(R.drawable.storm);
        else
            return getDrawable(R.drawable.cloudy);
    }
}
