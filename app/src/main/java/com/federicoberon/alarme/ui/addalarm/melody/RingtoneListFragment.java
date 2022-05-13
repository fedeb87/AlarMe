package com.federicoberon.alarme.ui.addalarm.melody;

import static android.app.Activity.RESULT_OK;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMeApplication;
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
                // In another case set custom melody
                if(binding.radioGroupRingtone.getCheckedRadioButtonId() == 0
                        || binding.radioGroupRingtone.getCheckedRadioButtonId() == -1)
                    // update views
                    setCustomMelodyVisible(viewModel.getSelectedMelody().getTitle());

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

        binding.okButton.setOnClickListener(view13 -> {
            if(binding.radioGroupRingtone.getCheckedRadioButtonId() == -1){
                Navigation.findNavController(binding.getRoot()).popBackStack(R.id.ringtoneListFragment, true);
            }
            mDisposable.add(viewModel.getMelodyById(binding.radioGroupRingtone.getCheckedRadioButtonId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(melody -> {
                    viewModel.setSelectedMelody(melody);
                    Navigation.findNavController(binding.getRoot()).popBackStack(R.id.ringtoneListFragment, true);
                    },
            throwable -> Log.e("MIO", "Unable to get milestones: ", throwable)));

        });

        // select melody from storage listener
        binding.musicFromDisk.setOnClickListener(view12 -> {

            // check storage permissions
            if (ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(requireActivity()
                        , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("audio/*");
            chooseFile = Intent.createChooser(chooseFile, getString(R.string.choose_a_file));
            startActivityForResult(chooseFile, 101);

        });

        binding.selectedFromDisk.setOnClickListener(view1 -> {
            binding.radioGroupRingtone.clearCheck();
            playRingtone(viewModel.getSelectedMelody().getUri());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            String filePath = getFilePath(fileUri);
            String fileName = getFileName(fileUri);
            playRingtone(filePath);

            // update views
            setCustomMelodyVisible(fileName);

            // update viewmodel
            MelodyEntity _melody = new MelodyEntity(fileName, filePath);
            viewModel.setSelectedMelody(_melody);
        }
    }

    private void setCustomMelodyVisible(String fileName) {
        binding.radioGroupRingtone.clearCheck();
        binding.selectedFromDisk.setVisibility(View.VISIBLE);
        binding.selectedFromDisk.setChecked(true);
        binding.selectedFromDisk.setText(fileName);
        binding.selectedFromDisk.setPadding(40, 0,0,0);
    }

    private String getFilePath(Uri fileUri) {
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(fileUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = requireActivity().getContentResolver().
                query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        if(filePath.equals("")){
            cursor = requireActivity().getContentResolver().
                    query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
        }
        cursor.close();
        return filePath;
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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
        radiobutton.setOnClickListener(view -> {
            //binding.selectedFromDisk.setVisibility(View.GONE);
            playRingtone(melody.getUri());
            binding.selectedFromDisk.setChecked(false);
        });
        return radiobutton;
    }

    private void playRingtone(String uri) {
        if(uri!=null){
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setWakeMode(requireContext(), AudioManager.MODE_RINGTONE);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setDataSource(requireContext(), Uri.parse(uri));
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setVolume(viewModel.getVolume(), viewModel.getVolume());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                Toast.makeText(requireContext(), getString(R.string.no_sound), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
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