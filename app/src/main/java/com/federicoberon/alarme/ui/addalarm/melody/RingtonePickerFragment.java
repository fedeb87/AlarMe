package com.federicoberon.alarme.ui.addalarm.melody;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMe;
import com.federicoberon.alarme.databinding.FragmentRingtonePickerBinding;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class RingtonePickerFragment extends Fragment {

    private FragmentRingtonePickerBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    AudioManager mAudioManager;

    @Inject
    AddAlarmViewModel viewModel;
    private MediaPlayer mMediaPlayer;

    public RingtonePickerFragment() { // Required empty public constructor
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
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = 140;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRingtonePickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //binding.seekBarVolume.set
        binding.onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableAllControls(isChecked);
            }
        });

        mMediaPlayer = new MediaPlayer();
        binding.onOffSwitch.setChecked(viewModel.isMelodyOn());

        binding.melodyLayout.setOnClickListener(view1 -> Navigation.findNavController(
                binding.getRoot()).navigate(R.id.action_ringtoneListFragment));

        // seekbar behaviour
        viewModel.setVolume(mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        binding.seekBarVolume.setProgress(viewModel.getVolume());
        binding.seekBarVolume.setMax(mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_ALARM));

        binding.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                        progress, 0);
                viewModel.setVolume(progress);
                playMelody(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (viewModel.getSelectedMelody() != null)
            binding.textviewMelodyValue.setText(viewModel.getSelectedMelody().getTitle());
    }

    private void playMelody(int progress) {

        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setWakeMode(requireContext(), AudioManager.MODE_RINGTONE);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setDataSource(requireContext(), Uri.parse(viewModel.getSelectedMelody().getUri()));
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setVolume(progress, progress);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enableAllControls(boolean isEnabled) {
        binding.melodyLayout.setEnabled(isEnabled);
        binding.seekBarVolume.setEnabled(isEnabled);
        viewModel.setMelodyOn(isEnabled);

        if (isEnabled) {
            binding.onOffSwitch.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            binding.melodyLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_view));
            binding.onOffSwitch.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.material_on_surface_disabled));
            binding.seekBarVolume.setProgressDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.seek_bar));
            binding.seekBarVolume.setThumb(ContextCompat.getDrawable(requireContext(), R.drawable.seek_thumb));
            binding.onOffSwitch.setText(getString(R.string.on_string));
        }else {
            binding.onOffSwitch.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_transparent));
            binding.melodyLayout.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.rounded_view_unchecked));
            binding.onOffSwitch.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white_transparent));
            binding.seekBarVolume.setProgressDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.seek_bar_unchecked));
            binding.seekBarVolume.setThumb(ContextCompat.getDrawable(requireContext(), R.drawable.seek_thumb_unchecked));
            binding.onOffSwitch.setText(getString(R.string.off_string));

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        mDisposable.clear();
        binding = null;
    }
}