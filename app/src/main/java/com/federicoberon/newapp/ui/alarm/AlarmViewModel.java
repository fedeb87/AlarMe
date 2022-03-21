package com.federicoberon.newapp.ui.alarm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.repositories.AlarmRepository;
import com.federicoberon.newapp.retrofit.Horoscope;
import com.federicoberon.newapp.retrofit.HoroscopeService;
import com.federicoberon.newapp.retrofit.HoroscopeServiceTwo;
import com.federicoberon.newapp.retrofit.HoroscopeTwo;
import com.federicoberon.newapp.utils.HoroscopeManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AlarmViewModel extends ViewModel {

    private static final String TAG = "AlarmViewModel";
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private String sign;
    private final AlarmRepository mAlarmRepository;
    private Horoscope horoscope;
    private OnResponseUpdateListener listener;
    private HoroscopeTwo horoscopeTwo;

    @Inject
    public AlarmViewModel(Application app, AlarmRepository alarmRepository) {
        this.context = ((SimpleRemindMeApplication)app).appComponent.getContext();
        this.mAlarmRepository = alarmRepository;
    }

    public void init(OnResponseUpdateListener listener, String sign){
        this.listener = listener;
        this.sign = sign;
    }

    public String getSign() {
        return this.sign;
    }

    public Maybe<Long> updateAlarm(AlarmEntity alarmEntity){
        return mAlarmRepository.insertOrUpdateAlarm(alarmEntity);
    }

    public void loadHoroscope() {
        SimpleRemindMeApplication application = SimpleRemindMeApplication.get(context);
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
        SimpleRemindMeApplication application = SimpleRemindMeApplication.get(context);
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

    public interface OnResponseUpdateListener {
        void onHoroscopeChanged(Horoscope horoscope);
        void onHoroscopeChangedTwo(HoroscopeTwo horoscope);
    }
}
