package com.federicoberon.alarme.ui.alarm;

import static com.federicoberon.alarme.MainActivity.GENERATED_USER_CODE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LATITUDE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LONGITUDE;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMe;
import com.federicoberon.alarme.broadcastreceiver.ActionReceiver;
import com.federicoberon.alarme.databinding.FragmentAlarmBinding;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.retrofit.Horoscope;
import com.federicoberon.alarme.retrofit.HoroscopeTwo;
import com.federicoberon.alarme.retrofit.WeatherResponse;
import com.federicoberon.alarme.retrofit.WeatherResponseTwo;
import com.federicoberon.alarme.service.AlarmService;
import com.federicoberon.alarme.service.ServiceUtil;
import com.federicoberon.alarme.utils.AlarmManager;
import com.federicoberon.alarme.utils.HoroscopeManager;
import com.federicoberon.alarme.utils.CustomMediaPlayer;
import com.federicoberon.alarme.utils.PhrasesManager;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AlarmActivity extends AppCompatActivity implements
        AlarmViewModel.OnResponseUpdateListener, TextToSpeech.OnInitListener{
    private static final String LOG_TAG = "AlarmActivity";
    private FragmentAlarmBinding binding;
    private AlarmEntity mAlarmEntity;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private TextToSpeech mTextToSpeech;
    private Handler handler;
    private Runnable delayedRunnable;

    @Inject
    SharedPreferences sharedPref;

    @Inject
    AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ((AlarMe) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

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
                    // todo creo que aca falta que pasaria si es recurrente, no lo tengo creo que no anda
                    Intent i = new Intent(this, ActionReceiver.class);
                    i.putExtra(ALARM_ENTITY, mAlarmEntity);
                    i.setAction(ALARM_ENTITY);
                    sendBroadcast(i);
                    finish();
                });
            /*if(!AlarmManager.recurring(mAlarmEntity)) {
                mAlarmEntity.setStarted(false);
                mDisposable.add(new ServiceUtil(this).updateAlarm(mAlarmEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(id -> {
                        Log.w(LOG_TAG, "<<< DESACTIVE LA ALARMA WEY >>> " + id);
                        finish();
                    },
                    throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));

                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
            }else {
            }
        });*/

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

        if(mAlarmEntity.isWeatherOn()) {
            //alarmViewModel.loadWeather(this);
            double lat = 0;
            double lon = 0;
            if(intent.hasExtra(LATITUDE))
                lat = intent.getDoubleExtra(LATITUDE, 0.0);
            if(intent.hasExtra(LONGITUDE))
                lon = intent.getDoubleExtra(LONGITUDE, 0.0);
            alarmViewModel.callWeatherAPI(lat, lon);

        }else
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

        // phrases
        Calendar calendar = Calendar.getInstance();
        int position = calendar.get(Calendar.DAY_OF_YEAR) + sharedPref.getInt(GENERATED_USER_CODE, 0);
        if (position > PhrasesManager.getPhrasesSize())
            position = position - PhrasesManager.getPhrasesSize();
        String phrase = getString(PhrasesManager.getPhraseId(position));
        binding.textPhrase.setText(phrase);

        // listener for play button
        binding.fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeak();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_MUTE){

            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }

        if(handler!=null)
            handler.removeCallbacks(delayedRunnable);

        mDisposable.clear();
        binding = null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onHoroscopeChanged(Horoscope horoscope) {
        if(horoscope!=null){
            binding.horoscopeCardView.setVisibility(View.VISIBLE);
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
            binding.horoscopeCardView.setVisibility(View.VISIBLE);
            loadHoroscopeInfo(horoscope.getDescription());
        }else{
            binding.horoscopeCardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                locale = getResources().getConfiguration().getLocales().get(0);
            } else{
                //noinspection deprecation
                locale = getResources().getConfiguration().locale;
            }

            if(!locale.getLanguage().equals("en")){
                locale = new Locale("spa", "MEX");
            }
            //int result = mTextToSpeech.setLanguage(Locale.ENGLISH);
            int result = mTextToSpeech.setLanguage(locale);
            mTextToSpeech.setSpeechRate(0.8f);
            mTextToSpeech.setPitch(0.7f);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            } else {
                String hourText = String.format(getString(R.string.hour_speach), mAlarmEntity.getHour()
                        , mAlarmEntity.getMinute());

                String tempText = "";
                if(mAlarmEntity.isWeatherOn())
                    tempText = String.format(getString(R.string.temp_speach),
                        alarmViewModel.getCurrentTempC(), alarmViewModel.getCurrentTempF());


                mTextToSpeech.speak(hourText + ". " + tempText, TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            Log.e("error", "Failed to Initialize");
        }
    }

    private void textToSpeak() {

        binding.fabPlay.setVisibility(View.GONE);

        CustomMediaPlayer.getMediaPlayerInstance().stopAudioFile();
        mTextToSpeech = new TextToSpeech(this, this);

        handler = new Handler(Looper.getMainLooper());
        delayedRunnable = () -> {CustomMediaPlayer.getMediaPlayerInstance().playAudioFile();
            binding.fabPlay.setVisibility(View.VISIBLE);};
        handler.postDelayed(delayedRunnable, 1000*13); // 13 seg
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
            textToSpeak();
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
        textToSpeak();
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