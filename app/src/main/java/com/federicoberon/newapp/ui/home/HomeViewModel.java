package com.federicoberon.newapp.ui.home;

import androidx.lifecycle.ViewModel;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.repositories.AlarmRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Singleton
public class HomeViewModel extends ViewModel {

    private final AlarmRepository mAlarmRepository;
    private String mQuery;
    private long currentAlarmId;

    @Inject
    public HomeViewModel(AlarmRepository repo) {
        this.mAlarmRepository = repo;
        this.currentAlarmId = -1;
    }

    public Flowable<List<AlarmEntity>> getAlarms(String query) {
        if (query.isEmpty()) return mAlarmRepository.getAllAlarms();
        if (mQuery!=null && !mQuery.isEmpty()) return mAlarmRepository.getAlarms(mQuery);
        mQuery = query;
        return mAlarmRepository.getAlarms(query);
    }

    public long getCurrentAlarmId(){
        return currentAlarmId;
    }

    public Flowable<AlarmEntity> getAlarmById(long id) {
        this.currentAlarmId = id;
        return mAlarmRepository.getAlarmById(id);
    }

    public Completable deleteAlarm() {
        return mAlarmRepository.deleteAlarm(currentAlarmId);
    }

    public Maybe<Long> disableAlarm(AlarmEntity alarmEntity) {
        return mAlarmRepository.insertOrUpdateAlarm(alarmEntity);
    }
}