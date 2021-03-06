package com.federicoberon.alarme.ui.addalarm.postpone;

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
import com.federicoberon.alarme.databinding.FragmentPostponePickerBinding;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;
import com.federicoberon.alarme.utils.PostponeManager;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class PostponePickerFragment extends Fragment {

    private FragmentPostponePickerBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private PostponeListItemAdapter adapter;

    @Inject
    AddAlarmViewModel viewModel;

    public PostponePickerFragment() {}

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
        binding = FragmentPostponePickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set Listview data
        ArrayList<String> vibNames = new ArrayList<>(PostponeManager.getPostponeOptions(requireContext()).values());
        adapter = new PostponeListItemAdapter(requireContext(), 0,
                vibNames , PostponeManager.getPostpone(viewModel.getSelectedPostpone()), viewModel.isPostponeOn());
        binding.postponeList.setAdapter(adapter);

        // on/off switch
        binding.onOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> enableAllControls(isChecked, vibNames));

        binding.onOffSwitch.setChecked(viewModel.isPostponeOn());

        // add on click listener to update selections
        binding.postponeList.setOnItemClickListener((adapterView, view1, i, l) -> viewModel.setSelectedPostpone(PostponeManager.getPostponeTime(i)));

        // set postpone checked in the list
        binding.postponeList.setItemChecked(
                vibNames.indexOf(PostponeManager.getPostpone(viewModel.getSelectedPostpone())), true);

    }

    private void enableAllControls(boolean isEnabled, ArrayList<String> vibNames) {
        binding.postponeList.setEnabled(isEnabled);
        viewModel.setPostponeOn(isEnabled);

        adapter = new PostponeListItemAdapter(requireContext(), 0,
                vibNames , PostponeManager.getPostpone(viewModel.getSelectedPostpone()), viewModel.isPostponeOn());
        binding.postponeList.setAdapter(adapter);
        binding.postponeList.setItemChecked(
                vibNames.indexOf(PostponeManager.getPostpone(viewModel.getSelectedPostpone())), true);

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