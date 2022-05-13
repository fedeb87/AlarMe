package com.federicoberon.alarme.ui.addalarm.repeat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.databinding.FragmentRepeatPickerBinding;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;
import com.federicoberon.alarme.utils.RepeatManager;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class RepeatPickerFragment extends Fragment {

    private FragmentRepeatPickerBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private RepeatListItemAdapter adapter;

    @Inject
    AddAlarmViewModel viewModel;

    public RepeatPickerFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((AlarMeApplication)requireActivity().getApplication()).appComponent.inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).getBinding().appBarMain.appBar.setExpanded(false, false);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = 140;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRepeatPickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> vibNames = new ArrayList<>(RepeatManager.getRepeatOptions(requireContext()).values());
        adapter = new RepeatListItemAdapter(requireContext(), 0,
                vibNames , RepeatManager.getRepeat(viewModel.getSelectedRepeat()), viewModel.isRepeatOn());
        binding.repeatList.setAdapter(adapter);

        // on/off switch
        binding.onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> enableAllControls(isChecked, vibNames));
        binding.onOffSwitch.setChecked(viewModel.isRepeatOn());

        // add on click listener to update selections
        binding.repeatList.setOnItemClickListener((adapterView, view1, i, l) -> viewModel.setSelectedRepeat(RepeatManager.getRepeatTime(i)));

        // set postpone checked in the list
        binding.repeatList.setItemChecked(
                vibNames.indexOf(RepeatManager.getRepeat(viewModel.getSelectedRepeat())), true);

    }

    private void enableAllControls(boolean isEnabled, ArrayList<String> vibNames) {

        binding.repeatList.setEnabled(isEnabled);
        viewModel.setRepeatOn(isEnabled);

        adapter = new RepeatListItemAdapter(requireContext(), 0,
                vibNames , RepeatManager.getRepeat(viewModel.getSelectedRepeat()), viewModel.isRepeatOn());
        binding.repeatList.setAdapter(adapter);
        binding.repeatList.setItemChecked(
                vibNames.indexOf(RepeatManager.getRepeat(viewModel.getSelectedRepeat())), true);

        if (isEnabled) {
            binding.onOffSwitch.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.onOffSwitch.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.material_on_surface_disabled));
            binding.onOffSwitch.setText(getString(R.string.on_string));
        }else {
            binding.onOffSwitch.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_transparent));
            binding.onOffSwitch.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_transparent));
            binding.onOffSwitch.setText(getString(R.string.off_string));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDisposable.clear();
        binding = null;
    }
}