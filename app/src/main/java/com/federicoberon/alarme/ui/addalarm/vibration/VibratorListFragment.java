package com.federicoberon.alarme.ui.addalarm.vibration;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMe;
import com.federicoberon.alarme.databinding.FragmentVibratorListBinding;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;
import com.federicoberon.alarme.utils.VibrationManager;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class VibratorListFragment extends Fragment {

    private static final String LOG_TAG = "VibratorListFragment";
    private FragmentVibratorListBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private VibListItemAdapter adapter;

    private Vibrator mVibrator;

    @Inject
    AddAlarmViewModel viewModel;

    public VibratorListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((AlarMe)requireActivity().getApplication()).appComponent.inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).getBinding().appBarMain.appBar.setExpanded(false, false);
        CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).
                        getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = 140;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mVibrator = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
        binding = FragmentVibratorListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set Listview data
        ArrayList<String> vibNames = new ArrayList<>(VibrationManager.getVibrations().keySet());
        adapter = new VibListItemAdapter(requireContext(), 0, vibNames,
                viewModel.getSelectedVibration(), viewModel.isVibrationOn());
        binding.vibrationsList.setAdapter(adapter);

        // on/off switch
        binding.onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableAllControls(isChecked, vibNames);
            }
        });
        binding.onOffSwitch.setChecked(viewModel.isVibrationOn());

        // add on click listener to update selections
        binding.vibrationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VibrationListItem vView = (VibrationListItem) view;
                String vibName = vView.getVibrationName();

                // preview vibration
                VibrationManager.vibrateByName(view.getContext(), vibName);
                viewModel.setSelectedVibration(vibName);

            }
        });

        // save button listener
        binding.okButton.setOnClickListener(view1 ->
                Navigation.findNavController(
                        binding.getRoot()).popBackStack(R.id.vibratorListFragment, true));

        // set vibrator checked in the list
        if (viewModel.getSelectedVibration() != null)
            binding.vibrationsList.setItemChecked(
                    vibNames.indexOf(viewModel.getSelectedVibration()), true);

    }

    private void enableAllControls(boolean isChecked, ArrayList<String> vibNames) {
        binding.vibrationsList.setEnabled(isChecked);
        viewModel.setVibrationOn(isChecked);

        adapter = new VibListItemAdapter(requireContext(), 0,
                vibNames , viewModel.getSelectedVibration(), viewModel.isVibrationOn());
        binding.vibrationsList.setAdapter(adapter);
        binding.vibrationsList.setItemChecked(
                vibNames.indexOf(viewModel.getSelectedVibration()), true);

        if (isChecked) {
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
        // clear all the subscriptions
        if (mVibrator.hasVibrator())
            mVibrator = null;
        mDisposable.clear();
        binding = null;
    }
}