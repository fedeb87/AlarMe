package com.federicoberon.newapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

/**
 * Immutable model class for a Milestone
 */
@Entity(tableName = "alarms")
public class AlarmEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String title;
    @ColumnInfo(name = "alarm_date")
    private Date alarmDate;
    private int hour;
    private int minute;

    @Ignore
    private boolean[] daysOfWeek;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    private boolean melodyOn;
    private long melodyId;
    private String melodyName;

    private boolean vibrationOn;
    private String vibrationPatter;

    private boolean postponeOn;
    @ColumnInfo(name = "postpone_time")
    private int postponeTime;

    private boolean repeatOn;
    @ColumnInfo(name = "repeat_time")
    private int repeatTime;
    //private String discardMethod;

    private boolean started;


    public AlarmEntity() {
    }

    public AlarmEntity(long id, @NonNull String title, Date alarmDate, int hour, int minute,
                       boolean[] daysOfWeek, boolean melodyOn, long melodyId, String melodyName,
                       boolean vibrationOn, String vibrationPatter, boolean postponeOn,
                       int postponeTime, boolean repeatOn, int repeatTime, boolean started) {
        this.id = id;
        this.title = title;
        this.alarmDate = alarmDate;
        this.hour = hour;
        this.minute = minute;
        this.daysOfWeek = daysOfWeek;
        this.monday = daysOfWeek[1];
        this.tuesday = daysOfWeek[2];
        this.wednesday = daysOfWeek[3];
        this.thursday = daysOfWeek[4];
        this.friday = daysOfWeek[5];
        this.saturday = daysOfWeek[6];
        this.sunday = daysOfWeek[0];
        this.melodyOn = melodyOn;
        this.melodyId = melodyId;
        this.melodyName = melodyName;
        this.vibrationOn = vibrationOn;
        this.vibrationPatter = vibrationPatter;
        this.postponeOn = postponeOn;
        this.postponeTime = postponeTime;
        this.repeatOn = repeatOn;
        this.repeatTime = repeatTime;
        this.started = started;
    }

    @Ignore
    public AlarmEntity(@NonNull String title, Date alarmDate, int hour, int minute,
                       boolean[] daysOfWeek, boolean melodyOn, long melodyId, String melodyName,
                       boolean vibrationOn, String vibrationPatter, boolean postponeOn,
                       int postponeTime, boolean repeatOn, int repeatTime, boolean started) {
        this.title = title;
        this.alarmDate = alarmDate;
        this.hour = hour;
        this.minute = minute;
        this.daysOfWeek = daysOfWeek;
        this.monday = daysOfWeek[1];
        this.tuesday = daysOfWeek[2];
        this.wednesday = daysOfWeek[3];
        this.thursday = daysOfWeek[4];
        this.friday = daysOfWeek[5];
        this.saturday = daysOfWeek[6];
        this.sunday = daysOfWeek[0];
        this.melodyOn = melodyOn;
        this.melodyId = melodyId;
        this.melodyName = melodyName;
        this.vibrationOn = vibrationOn;
        this.vibrationPatter = vibrationPatter;
        this.postponeOn = postponeOn;
        this.postponeTime = postponeTime;
        this.repeatOn = repeatOn;
        this.repeatTime = repeatTime;
        this.started = started;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public Date getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(Date alarmDate) {
        this.alarmDate = alarmDate;
    }

    public int getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public int getPostponeTime() {
        return postponeTime;
    }

    public void setPostponeTime(int postponeTime) {
        this.postponeTime = postponeTime;
    }

    public long getMelodyId() {
        return melodyId;
    }

    public void setMelodyId(long melodyId) {
        this.melodyId = melodyId;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(boolean[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getMelodyName() {
        return melodyName;
    }

    public void setMelodyName(String melodyName) {
        this.melodyName = melodyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmEntity alarmEntity = (AlarmEntity) o;
        return title.equals(alarmEntity.title) && alarmDate.equals(alarmEntity.alarmDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, alarmDate);
    }
}
