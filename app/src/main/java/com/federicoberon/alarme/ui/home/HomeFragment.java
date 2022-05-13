package com.federicoberon.alarme.ui.home;

import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.ALARM_ENTITY;
import static com.federicoberon.alarme.broadcastreceiver.AlarmBroadcastReceiver.IS_PREVIEW;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.databinding.FragmentHomeBinding;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.service.AlarmService;
import com.federicoberon.alarme.ui.addalarm.AddAlarmFragment;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;
import com.federicoberon.alarme.utils.AlarmManager;
import com.federicoberon.alarme.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements AlarmAdapter.EventListener {

    private static final String LOG_TAG = "HomeFragment";

    @Inject
    HomeViewModel homeViewModel;

    @Inject
    AddAlarmViewModel addAlarmViewModel;

    private FragmentHomeBinding binding;
    private AlarmAdapter adapter;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((AlarMeApplication) requireActivity().getApplicationContext())
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

        setupViewModel();
    }

    private void setupViewModel() {
        if (homeViewModel.getMultiselect_list().size()>0){
            homeViewModel.setMultiSelect(true);
            if (homeViewModel.getActionMode() == null) {
                homeViewModel.setActionMode(requireActivity().startActionMode(mActionModeCallback));
            }
        }
        adapter.setSelectedAlarmsList(homeViewModel.getMultiselect_list());
        binding.milestonesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView(binding.milestonesRecyclerView);
        showAllAlarms();
    }

    /**
     * Run when open fragment, get all milestones without filters
     */
    private void showAllAlarms() {
        mDisposable.add(homeViewModel.getAllAlarms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alarms -> {
                    if(alarms.isEmpty() || alarms==null)
                        setFragmentHeader(null);
                    else
                        getFirstAlarmEnable();
                    homeViewModel.setAlarms(alarms);
                    adapter.setAlarms(alarms);

                },
                throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));

    }

    private void getFirstAlarmEnable() {
        // listen for changes in active/inactive alarms
        mDisposable.add(homeViewModel.getFirstAlarmStarted()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alarms -> {
                    if(alarms.size() > 0 &&  alarms.get(0).isStarted()) {
                        setFragmentHeader(alarms.get(0));
                    }else
                        setFragmentHeader(null);

                },
                throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));
    }

    /**
     * Call setHeader on MainActivity to set next alarm title
     * @param alarmEntity next alarm
     */
    private void setFragmentHeader(AlarmEntity alarmEntity) {
        String header;
        if (alarmEntity == null || !alarmEntity.isStarted())
            header = "";
        else
            header = StringHelper.getFormatedAlarmDate(requireContext(), alarmEntity);

        ((MainActivity) requireActivity()).setHeader(header);
        ((MainActivity) requireActivity()).setCurrentTitle(" ");

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        adapter = new AlarmAdapter(binding.getRoot(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        // settings to swipe item
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public void multi_select(int position) {
        if (homeViewModel.getActionMode() != null) {
            if (homeViewModel.getMultiselect_list().contains(homeViewModel.getAlarms().get(position))) {
                homeViewModel.removeMultiselect_list(homeViewModel.getAlarms().get(position));
                adapter.removeSelectedAlarmsList(homeViewModel.getAlarms().get(position));
            }else {
                homeViewModel.addMultiselect_list(homeViewModel.getAlarms().get(position));
                adapter.addSelectedAlarmsList(homeViewModel.getAlarms().get(position));
            }


            if (homeViewModel.getMultiselect_list().size() > 0) {
                homeViewModel.getActionMode().setTitle("" + homeViewModel.getMultiselect_list().size());
                adapter.onBindViewHolder((AlarmAdapter.AlarmViewHolder)
                                binding.milestonesRecyclerView.findViewHolderForAdapterPosition(position),
                        position);
            }else {
                homeViewModel.getActionMode().setTitle("");
                homeViewModel.finishActionMode();
                refreshAdapter();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                    //Log.w("MIO", "Alarma actualizada con el id: " + alarmEntity.getId());
                    Log.w("MIO", " ");
                    //Log.w("MIO", "Alarma actualizada con el titulo: " + alarmEntity.getTitle());
                },
                throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));

    }

    @Override
    public void onItemClicked(View view, int position, boolean duplicate) {
        if (homeViewModel.isMultiSelect())
            multi_select(position);
        else {
            addAlarmViewModel.restart();
            long alarmId = (long) view.getTag();
            Bundle args = new Bundle();

            args.putLong(AddAlarmFragment.KEY_ALARM_ID, alarmId);
            args.putBoolean(AddAlarmFragment.KEY_DUPLICATE_ALARM, duplicate);

            Navigation.findNavController(view).navigate(R.id.action_nav_home_to_addAlarmFragment, args);
        }
    }

    @Override
    public void onItemLongClicked(View view, int position) {

        if (!homeViewModel.isMultiSelect()) {
            homeViewModel.clearMultiselect_list();
            homeViewModel.setMultiSelect(true);
        }

        if (homeViewModel.getActionMode() == null) {
            homeViewModel.setActionMode(requireActivity().startActionMode(mActionModeCallback));
        }
        multi_select(position);
    }

    @Override
    public void onDeleteMenuClicked(int position) {
        deleteAlarm(position);
    }

    @Override
    public void onDeleteSwiped(AlarmEntity alarmEntity) {
        mDisposable.add(homeViewModel.deleteAlarm(alarmEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(id -> Log.w("MIO", "Alarma borrada correctamente"),
            throwable -> Log.e("MIO", "Unable to delete alarm from database ", throwable)));
    }

    private void deleteAlarm(int position) {
        new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.delete_alarm_title))
                .setMessage(getString(R.string.delete_alarm_msg))
                .setPositiveButton(getString(R.string.ok_button), (dialog, which) -> {
                    // stop alarm service
                    AlarmEntity alarmEntity = homeViewModel.getAlarms().get(position);
                    AlarmManager.dismissAlarm(requireContext(), alarmEntity);

                    homeViewModel.deleteAlarm(alarmEntity)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(id -> adapter.removeAlarmFromList(position),
                            throwable -> Log.e("MIO", "Unable to delete alarm from database ", throwable));

                })
                .setNegativeButton(getString(R.string.cancel_button), null)
                .show();
    }

    @Override
    public void onPreviewMenuClicked(AlarmEntity alarmEntity) {
        // start service
        startAlarmService(alarmEntity);
    }

    private void startAlarmService(AlarmEntity alarmEntity) {

        Intent intentService = new Intent(requireContext(), AlarmService.class);
        intentService.putExtra(ALARM_ENTITY, alarmEntity);
        intentService.putExtra(IS_PREVIEW, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intentService);
        } else {
            requireContext().startService(intentService);
        }
    }

    // ************** this are for multiple selection
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items

            mode.setTitle(null);
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.ctx_menu, menu);
            for (int i = 0; i < 4; i++) {
                menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ctx_menu_active:
                    activateAlarms();
                    return true;
                case R.id.ctx_menu_inactive:
                    inactiveAlarms();
                    return true;
                case R.id.ctx_menu_remove:
                    deleteAlarms();
                    return true;
                case R.id.ctx_menu_select_all:
                    select_all();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            homeViewModel.getActionMode().setTitle("");
            homeViewModel.finishActionMode();

            homeViewModel.setActionMode(null);
            homeViewModel.setMultiSelect(false);
            homeViewModel.clearMultiselect_list();
            adapter.setSelectedAlarmsList(new ArrayList<>());


            refreshAdapter();
        }
    };

    private void inactiveAlarms() {
        List<Long> alarmsIdToActivate = new ArrayList<>();
        for (AlarmEntity alarm : homeViewModel.getMultiselect_list()) {
            alarmsIdToActivate.add(alarm.getId());
        }

        // cancel scheduled alarms
        mDisposable.add(homeViewModel.getAlarmsToDelete(alarmsIdToActivate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alarmsToActivate -> {
                    // dismiss alarms
                    AlarmManager.dismissAlarm(requireContext(), alarmsToActivate);

                },
                throwable -> Log.e("MIO", "Unable to dismiss alarms", throwable)));


        //
        mDisposable.add(homeViewModel.inactiveAlarms(alarmsIdToActivate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    adapter.setAlarms(homeViewModel.getAlarms());
                    Log.w("MIO", "Alarmas desactivados correctamente " + alarmsIdToActivate.size());
                },
                throwable -> Log.e("MIO", "Unable to activate alarms", throwable)));
    }

    private void activateAlarms() {
        List<Long> alarmsIdToActivate = new ArrayList<>();
        for (AlarmEntity alarm : homeViewModel.getMultiselect_list()) {
            alarmsIdToActivate.add(alarm.getId());
        }

        // schedule alarms
        mDisposable.add(homeViewModel.getAlarmsToDelete(alarmsIdToActivate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(alarmsToActivate -> {

                    // start alarm service
                    for(AlarmEntity alarmEntity : alarmsToActivate){
                        if(!alarmEntity.isStarted())
                            AlarmManager.schedule(requireContext(), alarmEntity);

                    }
                },
                throwable -> Log.e("MIO", "Unable to schedule alarms", throwable)));


        //
        mDisposable.add(homeViewModel.activeAlarms(alarmsIdToActivate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    adapter.setAlarms(homeViewModel.getAlarms());
                    Log.w("MIO", "Alarmas activadas correctamente " + alarmsIdToActivate.size());
                },
                throwable -> Log.e("MIO", "Unable to activate alarms", throwable)));
    }


    @SuppressLint("NotifyDataSetChanged")
    public void refreshAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void select_all() {
        if (homeViewModel.getActionMode() != null) {
            if (homeViewModel.getMultiselect_list().size() == homeViewModel.getAlarms().size()){
                homeViewModel.clearMultiselect_list();
                adapter.setSelectedAlarmsList(new ArrayList<>());

                homeViewModel.getActionMode().setTitle("");
            }else{
                homeViewModel.selectAllClases();
                adapter.setSelectedAlarmsList((ArrayList<AlarmEntity>) homeViewModel.getAlarms());
                homeViewModel.getActionMode().setTitle("" + homeViewModel.getMultiselect_list().size());
            }
            refreshAdapter();
        }
    }

    private void deleteAlarms() {
        new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.delete_alarms_title))
                .setMessage(getString(R.string.delete_alarms_msg))
                .setPositiveButton(getString(R.string.ok_button), (dialog, which) -> {

                    List<Long> alarmsIdToDelete = new ArrayList<>();
                    for (AlarmEntity alarm : homeViewModel.getMultiselect_list()) {
                        alarmsIdToDelete.add(alarm.getId());
                    }

                    mDisposable.add(homeViewModel.getAlarmsToDelete(alarmsIdToDelete)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(alarmsToDelete -> {
                                    // stop alarm service
                                    AlarmManager.dismissAlarm(requireContext(), alarmsToDelete);

                                    homeViewModel.deleteAlarms(alarmsIdToDelete)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() -> {
                                                        // stop alarm service
                                                        Log.w("MIO", "Alarma borrada correctamente");
                                                    },
                                                    throwable -> Log.e("MIO", "Unable to delete alarm from database ", throwable));

                                    adapter.setAlarms(homeViewModel.getAlarms());
                            },
                            throwable -> Log.e("MIO", "Unable to cancel alarm: ", throwable)));

                    homeViewModel.finishActionMode();
                })
                .setNegativeButton(getString(R.string.cancel_button), null)
                .show();
    }
}