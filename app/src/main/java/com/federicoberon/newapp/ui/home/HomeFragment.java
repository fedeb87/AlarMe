package com.federicoberon.newapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.federicoberon.newapp.MainActivity;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.databinding.FragmentHomeBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.service.AlarmService;
import com.federicoberon.newapp.ui.addalarm.AddAlarmFragment;
import com.federicoberon.newapp.utils.AlarmManager;
import com.federicoberon.newapp.utils.StringHelper;

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
                    if(alarms.isEmpty())
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
                    if(alarms.get(0).isStarted())
                        setFragmentHeader(alarms.get(0));
                    else
                        setFragmentHeader(null);

                },
                throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));
    }

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

            if (homeViewModel.getMultiselect_list().size() > 0)
                homeViewModel.getActionMode().setTitle("" + homeViewModel.getMultiselect_list().size());
            else
                homeViewModel.getActionMode().setTitle("");
            refreshAdapter();
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
                    Log.w("MIO", "Alarma actualizada con el id: " + alarmEntity.getId());
                    Log.w("MIO", "Alarma actualizada con el titulo: " + alarmEntity.getTitle());
                },
                throwable -> Log.e(LOG_TAG, "Unable to get milestones: ", throwable)));

    }

    @Override
    public void onItemClicked(View view, int position) {
        if (homeViewModel.isMultiSelect())
            multi_select(position);
        else {
            long alarmId = (long) view.getTag();
            Bundle args = new Bundle();

            args.putLong(AddAlarmFragment.KEY_ALARM_ID, alarmId);

            View itemDetailFragmentContainer = binding.getRoot()!=null?binding.getRoot().findViewById(R.id.item_detail_nav_container):null;
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, args);
            } else {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_addAlarmFragment, args);
            }
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

    // ************** esto es para multipple selccion
    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.ctx_menu, menu);
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
                case R.id.ctx_menu_remove:
                    deleteAlarm();
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
            homeViewModel.setActionMode(null);
            homeViewModel.setMultiSelect(false);
            homeViewModel.clearMultiselect_list();
            adapter.setSelectedAlarmsList(new ArrayList<>());
            refreshAdapter();
        }
    };


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

    private void deleteAlarm() {

        new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.delete_alarm_title))
                .setMessage(getString(R.string.delete_alarm_msg))
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                    }

                })
                .setNegativeButton(getString(R.string.cancel_button), null)
                .show();
    }
}