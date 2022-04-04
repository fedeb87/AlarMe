package com.federicoberon.alarme.ui.home;

import android.view.ActionMode;

import androidx.lifecycle.ViewModel;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.repositories.AlarmRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Singleton
public class HomeViewModel extends ViewModel {

    private final AlarmRepository mAlarmRepository;
    private long currentAlarmId;
    private boolean isMultiSelect = false;
    private List<AlarmEntity> alarms;
    private ArrayList<AlarmEntity> multiselect_list;
    private ActionMode mActionMode;

    @Inject
    public HomeViewModel(AlarmRepository repo) {
        this.mAlarmRepository = repo;
        this.currentAlarmId = -1;
        multiselect_list = new ArrayList<>();
    }

    public Flowable<List<AlarmEntity>> getAllAlarms() {
        return mAlarmRepository.getAllAlarms();
    }

    public Flowable<List<AlarmEntity>> getFirstAlarmStarted() {
        return mAlarmRepository.getFirstAlarmStarted();
    }

    public long getCurrentAlarmId(){
        return currentAlarmId;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    public void selectAllClases() {
        this.multiselect_list = new ArrayList<>();
        this.multiselect_list.addAll(alarms);
    }

    public void clearMultiselect_list() {
        this.multiselect_list = new ArrayList<>();
    }

    public void addMultiselect_list(AlarmEntity alarmEntity) {
        this.multiselect_list.add(alarmEntity);
    }

    public void removeMultiselect_list(AlarmEntity alarmEntity) {
        this.multiselect_list.remove(alarmEntity);
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void setActionMode(ActionMode mActionMode) {
        this.mActionMode = mActionMode;
    }

    public void finishActionMode() {
        this.mActionMode.finish();
    }

    public ArrayList<AlarmEntity> getMultiselect_list() {
        return multiselect_list;
    }

    public Flowable<AlarmEntity> getAlarmById(long id) {
        this.currentAlarmId = id;
        return mAlarmRepository.getAlarmById(id);
    }

    public Maybe<Integer> deleteAlarm(AlarmEntity alarmEntity) {
        return mAlarmRepository.deleteAlarm(alarmEntity);
    }

    public Completable deleteAlarms(List<Long> ids) {
        return mAlarmRepository.deleteAlarms(ids);
    }

    public Completable activeAlarms(List<Long> ids) {
        return mAlarmRepository.updateAlarms(ids, true);
    }

    public Completable inactiveAlarms(List<Long> ids) {
        return mAlarmRepository.updateAlarms(ids, false);
    }

    public Maybe<Long> disableAlarm(AlarmEntity alarmEntity) {
        return mAlarmRepository.insertOrUpdateAlarm(alarmEntity);
    }

    public void setAlarms(List<AlarmEntity> alarms) {
        this.alarms = alarms;
    }

    public List<AlarmEntity> getAlarms() {
        return alarms;
    }

    public Flowable<List<AlarmEntity>> getAlarmsToDelete(List<Long> ids) {
        return mAlarmRepository.getAlarmByIds(ids);
    }
}