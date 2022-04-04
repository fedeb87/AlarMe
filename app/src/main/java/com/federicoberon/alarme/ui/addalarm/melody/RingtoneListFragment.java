package com.federicoberon.alarme.ui.addalarm.melody;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMe;
import com.federicoberon.alarme.databinding.FragmentRingtoneListBinding;
import com.federicoberon.alarme.model.MelodyEntity;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RingtoneListFragment extends Fragment {

    private static final String LOG_TAG = "RingtoneListFragment";
    private FragmentRingtoneListBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private MediaPlayer mMediaPlayer;

    @Inject
    AudioManager mAudioManager;

    @Inject
    AddAlarmViewModel viewModel;

    public RingtoneListFragment() {
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
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = 140;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMediaPlayer = new MediaPlayer();
        binding = FragmentRingtoneListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDisposable.add(viewModel.getMelodies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(melodies -> {

                for (MelodyEntity melody :melodies)
                    binding.radioGroupRingtone.addView(createOneRadioButton(melody));

                if (viewModel.getSelectedMelody() != null)
                    binding.radioGroupRingtone.check((int) viewModel.getSelectedMelody().getId());

            },
            throwable -> Log.e("MIO", "Unable to get milestones: ", throwable)));


        // seekbar behaviour
        binding.seekBarVolume.setProgress(viewModel.getVolume());
        binding.seekBarVolume.setMax(mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_ALARM));

        binding.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                        progress, 0);
                viewModel.setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDisposable.add(viewModel.getMelodyById(binding.radioGroupRingtone.getCheckedRadioButtonId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(melody -> {
                        viewModel.setSelectedMelody(melody);
                        Navigation.findNavController(binding.getRoot()).popBackStack(R.id.ringtoneListFragment, true);
                        //Navigation.findNavController(binding.getRoot()).navigate(R.id.action_back_to_ringtonePickerFragment);
                        },
                throwable -> Log.e("MIO", "Unable to get milestones: ", throwable)));

            }
        });
    }

    private RadioButton createOneRadioButton(MelodyEntity melody) {
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 24, 5, 24);

        MaterialRadioButton radiobutton = new MaterialRadioButton(requireContext());

        radiobutton.setId((int) melody.getId());
        radiobutton.setLayoutParams(params);
        radiobutton.setTextSize(20);
        radiobutton.setPadding(40, 0,0,0);
        radiobutton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        radiobutton.setText(melody.getTitle());
        radiobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playRingtone(melody.getUri());
            }
        });
        return radiobutton;
    }

    private void playRingtone(String uri) {
        // todo no se si pedirlo aca, o cuando
        // todo creo que deberia poner un mensaje antes de mandarlo a esa pantalla
        NotificationManager nm = (NotificationManager) requireActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!nm.isNotificationPolicyAccessGranted()) {
                // Permissions were not granted, request for permission
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult( intent, 101 );
                startActivity(intent);
            }

        }

        if(uri!=null){
            try {
                /*mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);*/
                mMediaPlayer.reset();
                mMediaPlayer.setWakeMode(requireContext(), AudioManager.MODE_RINGTONE);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setDataSource(requireContext(), Uri.parse(uri));
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setVolume(viewModel.getVolume(), viewModel.getVolume());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // clear all the subscriptions
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        mDisposable.clear();
        binding = null;
    }
}