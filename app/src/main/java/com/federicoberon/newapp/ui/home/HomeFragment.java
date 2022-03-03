package com.federicoberon.newapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.newapp.MainActivity;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.databinding.FragmentHomeBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.utils.StringHelper;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements AlarmAdapter.EventListener {

    private static final String LOG_TAG = "HomeFragment";

    @Inject
    HomeViewModel homeViewModel;

    private FragmentHomeBinding binding;
    private AlarmAdapter adapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((SimpleRemindMeApplication) requireActivity().getApplicationContext())
                .appComponent.inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.nav_header_height);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(binding.milestonesRecyclerView);
        showAllAlarms();
    }

    /**
     * Get all milestones without filters
     */
    private void showAllAlarms() {

        mDisposable.add(homeViewModel.getAlarms("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alarms -> {
                    adapter.setAlarms(alarms);
                    // get the first alarma and set fragment header
                    if(alarms.isEmpty())
                        setFragmentHeader(null);
                    else
                        setFragmentHeader(alarms.get(0));

                },
                throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));
    }

    private void setFragmentHeader(AlarmEntity alarmEntity) {
        String header;
        if (alarmEntity == null || !alarmEntity.isStarted())
            header = "";
        else
            // todo podria hacer que diga el tiempo restante hasta la proxima alarma
            header = StringHelper.getFormatedAlarmDate(requireContext(), alarmEntity);

        ((MainActivity) requireActivity()).setHeader(header);
        ((MainActivity) requireActivity()).setCurrentTitle(" ");

    }

    private void setupRecyclerView(
            RecyclerView recyclerView) {
        adapter = new AlarmAdapter(
                binding.getRoot(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // clear all the subscriptions
        mDisposable.clear();
        binding = null;
    }

    /**
     * Called when on / off alarm from the list
     * @param alarmEntity
     */
    @Override
    public void onEvent(AlarmEntity alarmEntity) {

        mDisposable.add(homeViewModel.disableAlarm(alarmEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    Log.w("MIO", "Alarma actualizada con el id: " + alarmEntity.getId());
                    Log.w("MIO", "Alarma actualizada con el estado: " + alarmEntity.isStarted());
                },
                throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));
        ;
    }
}