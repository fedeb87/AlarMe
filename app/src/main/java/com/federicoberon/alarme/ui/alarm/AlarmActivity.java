package com.federicoberon.alarme.ui.alarm;

import static com.federicoberon.alarme.MainActivity.ENABLE_LOGS;
import static com.federicoberon.alarme.MainActivity.GENERATED_USER_CODE;
import static com.federicoberon.alarme.MainActivity.LAT_KEY;
import static com.federicoberon.alarme.MainActivity.LON_KEY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.IS_PREVIEW;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LATITUDE;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.LONGITUDE;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.broadcastreceiver.ActionReceiver;
import com.federicoberon.alarme.databinding.FragmentAlarmBinding;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.api.Horoscope;
import com.federicoberon.alarme.api.HoroscopeTwo;
import com.federicoberon.alarme.api.WeatherResponse;
import com.federicoberon.alarme.api.WeatherResponseTwo;
import com.federicoberon.alarme.service.AlarmService;
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

public class AlarmActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private static final String LOG_TAG = "<<< AlarmActivity >>>";
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

    private int streamMusicCurrentVol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ((AlarMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        binding = FragmentAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        alarmViewModel.init(sharedPref.getString(getString(R.string.sign_name), "aries"));

        // ----- If extra alarmEntity not found then exit activity
        Intent intent = getIntent();
        if(intent.hasExtra(IS_PREVIEW)) {
            alarmViewModel.isPreview = intent.getBooleanExtra(IS_PREVIEW, false);
        }

        if(intent.hasExtra(ALARM_ENTITY)) {
            mAlarmEntity = (AlarmEntity) intent.getSerializableExtra(ALARM_ENTITY);
        }else {
            if(sharedPref.getBoolean(ENABLE_LOGS, false))
                Log.w(LOG_TAG,getString(R.string.no_alarm_entity));
            finish();
        }

        // ----- Config dismiss alarm action
        binding.activityRingDismiss.setOnClickListener(v -> {
            Intent i = new Intent(this, ActionReceiver.class);
            i.putExtra(ALARM_ENTITY, mAlarmEntity);
            i.putExtra(IS_PREVIEW, alarmViewModel.isPreview);
            i.setAction(ALARM_ENTITY);

            sendBroadcast(i);
            finish();
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
            stopAlarmService();
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            alarmViewModel.setLocale(getResources().getConfiguration().getLocales().get(0));
        else
            alarmViewModel.setLocale(getResources().getConfiguration().locale);

        if(mAlarmEntity.isHoroscopeOn()) {
            mDisposable.add(alarmViewModel.loadHoroscope(HoroscopeManager.getNameURL(this, alarmViewModel.getSign()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(((AlarMeApplication) getApplicationContext()).defaultSubscribeScheduler())
                    .subscribe(this::onHoroscopeChanged, throwable -> onHoroscopeChanged(null)));
        }else
            binding.horoscopeCardView.setVisibility(View.GONE);

        double lat = 0;
        double lon = 0;
        if(mAlarmEntity.isWeatherOn()) {
            if(intent.hasExtra(LONGITUDE) && intent.hasExtra(LATITUDE)){
                lat = intent.getDoubleExtra(LATITUDE, 0.0);
                lon = intent.getDoubleExtra(LONGITUDE, 0.0);
            }else if(sharedPref.contains(LAT_KEY) && sharedPref.contains(LON_KEY)) {
                lat = sharedPref.getFloat(LAT_KEY, 0.0f);
                lon = sharedPref.getFloat(LON_KEY, 0.0f);
            }else{
                textToSpeak();
                binding.weatherCardView.setVisibility(View.GONE);
            }

            double finalLat = lat;
            double finalLon = lon;
            //AlarmViewModel.this.weatherResponse = weatherResponse;
            mDisposable.add(alarmViewModel.callWeatherAPI(lat, lon)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(((AlarMeApplication) getApplicationContext()).defaultSubscribeScheduler())
                    .subscribe(this::onWeatherChanged,
                            throwable -> {
                                mDisposable.add(alarmViewModel.callWeatherAPITwo(finalLat, finalLon).observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(((AlarMeApplication) getApplicationContext()).defaultSubscribeScheduler())
                                        .subscribe(this::onWeatherChangedTwo,
                                                throwable2 -> {
                                                    if(sharedPref.getBoolean(ENABLE_LOGS, false))
                                                        Log.e(LOG_TAG, "Error loading weather ", throwable2);
                                                    onWeatherChangedTwo(null);
                                                }));
                                if(sharedPref.getBoolean(ENABLE_LOGS, false))
                                    Log.e(LOG_TAG, "Error loading weather ", throwable);
                                onWeatherChanged(null);
                            }));

        }else {
            binding.weatherCardView.setVisibility(View.GONE);
            textToSpeak();
        }

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

        if (mAlarmEntity.isPhrasesOn()) {
            // phrases
            Calendar calendar = Calendar.getInstance();
            int position = calendar.get(Calendar.DAY_OF_YEAR) + sharedPref.getInt(GENERATED_USER_CODE, 0);
            if (position >= PhrasesManager.getPhrasesSize())
                position = position - PhrasesManager.getPhrasesSize();
            String phrase = getString(PhrasesManager.getPhraseId(position));
            binding.textPhrase.setText(phrase);
        }else{
            binding.textPhrase.setVisibility(View.GONE);
        }
        // listener for play button
        binding.fabPlay.setOnClickListener(view -> textToSpeak());
    }

    private void stopAlarmService() {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        intentService.putExtra(ALARM_ENTITY, mAlarmEntity);
        getApplicationContext().stopService(intentService);
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
    protected void onPause() {
        super.onPause();
        stopAlarmService();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        stopAlarmService();
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
    public void onHoroscopeChanged(Horoscope horoscope) {
        if(horoscope!=null){
            binding.horoscopeCardView.setVisibility(View.VISIBLE);
            loadHoroscopeInfo(horoscope.getDescription());
        }else{
            mDisposable.add(alarmViewModel.loadHoroscopeTwo(HoroscopeManager.getNameURLTwo(this, alarmViewModel.getSign()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(((AlarMeApplication) getApplicationContext()).defaultSubscribeScheduler())
                    .subscribe(this::onHoroscopeChangedTwo, throwable -> {

                        if(sharedPref.getBoolean(ENABLE_LOGS, false))
                            Log.e(LOG_TAG, "Second horoscope service error: ", throwable);

                        onHoroscopeChangedTwo(null);
                    }));

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadHoroscopeInfo(String description) {
        binding.horoscopeCardView.setVisibility(View.VISIBLE);
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

    public void onHoroscopeChangedTwo(HoroscopeTwo horoscope) {
        if(horoscope!=null){
            binding.horoscopeCardView.setVisibility(View.VISIBLE);
            loadHoroscopeInfo(horoscope.description);
        }else{
            binding.horoscopeCardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            if(!alarmViewModel.getLocale().getLanguage().equals("en")){
                alarmViewModel.setLocale(new Locale("spa", "MEX"));
            }
            //int result = mTextToSpeech.setLanguage(Locale.ENGLISH);
            int result = mTextToSpeech.setLanguage(alarmViewModel.getLocale());
            mTextToSpeech.setSpeechRate(0.8f);
            mTextToSpeech.setPitch(0.7f);

            // set max volume for speaker
            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            int amStreamMusicMaxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            streamMusicCurrentVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, amStreamMusicMaxVol, 0);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                if(sharedPref.getBoolean(ENABLE_LOGS, false))
                    Log.e(LOG_TAG, "This Language is not supported");
            } else {
                Calendar cal = Calendar.getInstance();
                String hourText = String.format(getString(R.string.hour_speach), cal.get(Calendar.HOUR)
                        , cal.get(Calendar.MINUTE));

                String tempText = "";
                String titleText = "";

                if (mAlarmEntity.isReadTitle() && !mAlarmEntity.getTitle().isEmpty())
                    titleText = mAlarmEntity.getTitle();

                if(mAlarmEntity.isWeatherOn() && (AlarmService.locationEnabled(this) || alarmViewModel.cordsCached()))
                    tempText = String.format(getString(R.string.temp_speach),
                        alarmViewModel.getCurrentTempC(), alarmViewModel.getCurrentTempF());

                mTextToSpeech.speak(hourText + ". " + titleText + ". " + tempText, TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {

            if(handler!=null)
                handler.removeCallbacks(delayedRunnable);

            if(sharedPref.getBoolean(ENABLE_LOGS, false))
                Log.e(LOG_TAG, "Failed to Initialize " + status);
        }
    }

    private void textToSpeak() {
        if(mAlarmEntity.isMelodyOn()) {
            binding.fabPlay.setVisibility(View.GONE);

            CustomMediaPlayer.getMediaPlayerInstance().stopAudioFile();
            mTextToSpeech = new TextToSpeech(this, this);

            handler = new Handler(Looper.getMainLooper());
            delayedRunnable = () -> {
                CustomMediaPlayer.getMediaPlayerInstance().playAudioFile();
                binding.fabPlay.setVisibility(View.VISIBLE);

                // set original vol
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, streamMusicCurrentVol, 0);
            };

            int multiplier = 4;
            if (mAlarmEntity.isWeatherOn() && (AlarmService.locationEnabled(this) || alarmViewModel.cordsCached()))
                multiplier += 7;
            if (mAlarmEntity.isReadTitle() && !mAlarmEntity.getTitle().isEmpty())
                multiplier += 5;
            handler.postDelayed(delayedRunnable, 1000 * multiplier); // 12 sec or 4 sec
        }
    }

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

    public void onWeatherChangedTwo(WeatherResponseTwo weatherResponse) {
        // current data
        // (32 °F − 32) × 5/9 = 0 °C
        if(weatherResponse==null){
            binding.weatherCardView.setVisibility(View.GONE);
            return;
        }
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
