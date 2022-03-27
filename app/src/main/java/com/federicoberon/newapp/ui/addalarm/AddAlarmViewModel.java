package com.federicoberon.newapp.ui.addalarm;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.model.MelodyEntity;
import com.federicoberon.newapp.repositories.AlarmRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.federicoberon.newapp.utils.AlarmManager;
import com.federicoberon.newapp.utils.DateUtils;
import com.federicoberon.newapp.utils.RepeatManager;
import com.federicoberon.newapp.utils.StringHelper;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Singleton
public class AddAlarmViewModel extends ViewModel {

    @Inject
    AudioManager mAudioManager;

    private final AlarmRepository mAlarmRepository;
    private AlarmEntity insertedAlarm;
    private MutableLiveData<Calendar> nextAlarm;
    private boolean[] daysOfWeek;
    private Calendar scheduledDay;
    private int mHour;
    private int mMinutes;
    private int mYear;
    private int mMonth;
    private int mDay;
    private MelodyEntity selectedMelody;
    private String selectedVibration;
    // time in min
    private int selectedPostpone;
    private int selectedRepeat;
    private boolean isMelodyOn;
    private boolean isVibrationOn;
    private boolean isPostponeOn;
    private boolean isRepeatOn;
    private boolean duplicate;
    private boolean isHoroscopeOn;
    private boolean weatherOn;
    private int volume;

    @Inject
    public AddAlarmViewModel(Application app, AlarmRepository alarmRepository) {
        this.nextAlarm = new MutableLiveData<>();
        this.daysOfWeek = new boolean[7];
        this.mAlarmRepository = alarmRepository;
        this.selectedPostpone = 5;
        this.selectedRepeat = RepeatManager.DEFAULT_REPEAT;
        this.isRepeatOn = false;
        this.isVibrationOn = true;
        this.isPostponeOn = true;
        this.isMelodyOn = true;
        this.duplicate = false;
        this.isHoroscopeOn = false;
        this.weatherOn = false;
        this.volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void restart(){
        this.nextAlarm = new MutableLiveData<>();
        this.daysOfWeek = new boolean[7];
        this.selectedPostpone = 5;
        this.selectedRepeat = RepeatManager.DEFAULT_REPEAT;
        this.isRepeatOn = false;
        this.isVibrationOn = true;
        this.isPostponeOn = true;
        this.isMelodyOn = true;
        this.insertedAlarm = null;
        this.selectedMelody = null;
        this.selectedVibration = null;
        this.duplicate = false;
        this.isHoroscopeOn = false;
        this.weatherOn = false;
        this.volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    }

    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(boolean[] array) {
        this.daysOfWeek = new boolean[7];
        this.daysOfWeek = array;
    }

    public void setDaysOfWeek(int position) {
        this.daysOfWeek[position-1] = !this.daysOfWeek[position-1];
        if (containsTrue())
            this.scheduledDay = null;
        else{
            Calendar calendar = nextAlarm.getValue();
            assert calendar != null;
            calendar.set(Calendar.DAY_OF_MONTH, DateUtils.isTomorrow(mHour, mMinutes));
            this.nextAlarm.setValue(calendar);
            return;
        }

        // lunes es 2, domiungo sera 1
        // y sabado sera 7
        // position es el valor de DAY_OF_WEEK
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        int i=0;
        while(i<7){
            if (this.daysOfWeek[(currentDayOfWeek+i-1)%6]){
                // pongo el dia y salgo del bucle
                Calendar calendar = nextAlarm.getValue();

                calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                calendar.add(Calendar.DAY_OF_MONTH,i);
                nextAlarm.setValue(calendar);
                break;
            }
            i++;
        }
    }

    private boolean containsTrue() {
        for(boolean b : daysOfWeek) if(b) return true;
        return false;
    }

    public MutableLiveData<Calendar> getNextAlarm() {
        return nextAlarm;
    }

    public void setNextAlarm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setNextAlarm(calendar);
    }

    public void setNextAlarm(Calendar calendar) {
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinutes = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.nextAlarm.setValue(calendar);
    }

    public void setTime(int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinutes = minute;
        Calendar calendar = nextAlarm.getValue();
        if (calendar != null) {
            calendar.set(Calendar.HOUR_OF_DAY, mHour);
            calendar.set(Calendar.MINUTE, mMinutes);

            if (scheduledDay == null && !containsTrue()) {
                // no tiene seteado nada mas
                calendar.set(Calendar.DAY_OF_MONTH, DateUtils.isTomorrow(mHour, mMinutes));
            }

            nextAlarm.setValue(calendar);
        }
    }

    public Date getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.MONTH, mMonth);
        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMinutes);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public void setDate(int year, int month, int dayOfMonth) {
        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;

        Calendar calendar = nextAlarm.getValue();
        assert calendar != null;
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.MONTH, mMonth);
        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        scheduledDay = calendar;
        this.daysOfWeek = new boolean[7];
        nextAlarm.setValue(calendar);
    }

    public void setInsertedAlarm(AlarmEntity alarmToInsert) {
        setNextAlarm(alarmToInsert.getAlarmDate());
        this.insertedAlarm = alarmToInsert;
    }

    public AlarmEntity getInsertedAlarm(){
        return this.insertedAlarm;
    }

    public Maybe<Long> saveAlarm(String title) {

        int hourInMinutes = mHour*60+mMinutes;

        if (insertedAlarm==null || this.duplicate)
            insertedAlarm = new AlarmEntity(title, getDate(), mHour, mMinutes, hourInMinutes,
                    this.daysOfWeek, this.isMelodyOn, this.selectedMelody.getUri(),
                    this.selectedMelody.getTitle(), this.volume, this.isVibrationOn, this.selectedVibration,
                    this.isPostponeOn, this.selectedPostpone, this.isRepeatOn, this.selectedRepeat,
                    this.isHoroscopeOn, this.weatherOn, true);
        else
            insertedAlarm = new AlarmEntity(insertedAlarm.getId(), title, getDate(), mHour,
                    mMinutes, hourInMinutes, this.daysOfWeek, this.isMelodyOn,
                    this.selectedMelody.getUri(), this.selectedMelody.getTitle(), this.volume,
                    this.isVibrationOn, this.selectedVibration, this.isPostponeOn,
                    this.selectedPostpone, this.isRepeatOn, this.selectedRepeat,
                    this.isHoroscopeOn, this.weatherOn, insertedAlarm.isStarted());

        return mAlarmRepository.insertOrUpdateAlarm(insertedAlarm);
    }

    public Flowable<AlarmEntity> getAlarmById(long id) {
        return mAlarmRepository.getAlarmById(id);
    }

    public void scheduledAlarm(Context context){
        if(!StringHelper.containsTrue(daysOfWeek)){
            if(insertedAlarm.getAlarmDate().compareTo(new Date())<0) {
                int alarmDay = DateUtils.isTomorrow(insertedAlarm.getHour(), insertedAlarm.getMinute());
                Calendar cal = Calendar.getInstance();
                cal.setTime(insertedAlarm.getAlarmDate());
                cal.set(Calendar.DAY_OF_MONTH, alarmDay);
                insertedAlarm.setAlarmDate(cal.getTime());
            }
        }
        if(insertedAlarm.isStarted()) {
            AlarmManager.dismissAlarm(context, insertedAlarm);
            AlarmManager.schedule(context, insertedAlarm);
        }
    }

    public Flowable<List<MelodyEntity>> getMelodies() {
        return mAlarmRepository.getAllMelodies();
    }

    public Flowable<MelodyEntity> getMelodyById(long id) {
        return mAlarmRepository.getMelodyId(id);
    }

    public Flowable<MelodyEntity> getMelodyByName(String name) {
        return mAlarmRepository.getMelodyName(name);
    }

    public MelodyEntity getSelectedMelody() {
        return this.selectedMelody;
    }

    public void setSelectedMelody(MelodyEntity melody) {
        // set the melody inside the AlarmEntity

        if(this.insertedAlarm!=null) {
            AlarmEntity alarmEntity = getInsertedAlarm();
            alarmEntity.setMelodyUri(melody.getUri());
            alarmEntity.setMelodyName(melody.getTitle());
            this.insertedAlarm = alarmEntity;
        }
        this.selectedMelody = melody;
    }

    public void setSelectedVibration(String vibName) {
        if (this.insertedAlarm!=null)
            this.insertedAlarm.setVibrationPatter(vibName);
        this.selectedVibration = vibName;
    }

    public String getSelectedVibration() {
        return this.selectedVibration;
    }

    public void setSelectedPostpone(int i) {
        if (this.insertedAlarm!=null)
            this.insertedAlarm.setPostponeTime(i);
        this.selectedPostpone = i;
    }

    public int getSelectedPostpone() {
        return this.selectedPostpone;
    }

    public void setSelectedRepeat(int i) {
        if (this.insertedAlarm!=null)
            this.insertedAlarm.setRepeatTime(i);
        this.selectedRepeat = i;
    }

    public int getSelectedRepeat() {
        return this.selectedRepeat;
    }

    public boolean isVibrationOn() {
        return isVibrationOn;
    }

    public void setVibrationOn(boolean vibrationOn) {
        isVibrationOn = vibrationOn;
    }

    public boolean isPostponeOn() {
        return isPostponeOn;
    }

    public void setPostponeOn(boolean postponeOn) {
        isPostponeOn = postponeOn;
    }

    public boolean isRepeatOn() {
        return isRepeatOn;
    }

    public boolean isHoroscopeOn() {
        return isHoroscopeOn;
    }

    public boolean isWeatherOn() {
        return weatherOn;
    }

    public void setWeatherOn(boolean weatherOn) {
        this.weatherOn = weatherOn;
    }

    public void setRepeatOn(boolean repeatOn) {
        isRepeatOn = repeatOn;
    }

    public void setHoroscopeOn(boolean horoscopeOn) {
        this.isHoroscopeOn = horoscopeOn;
    }

    public boolean isMelodyOn() {
        return this.isMelodyOn;
    }

    public void setMelodyOn(boolean melodyOn) {
        this.isMelodyOn = melodyOn;
    }

    public void setIdInsertedAlarm(Long id) {
        this.insertedAlarm.setId(id);
    }

    public void setDuplicate() {
        this.duplicate = true;
    }

    public void setVolume(int progress) {
        this.volume = progress;
    }

    public int getVolume() {
        return this.volume;
    }
}