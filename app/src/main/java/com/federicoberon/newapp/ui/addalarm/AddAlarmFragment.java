package com.federicoberon.newapp.ui.addalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import com.federicoberon.newapp.MainActivity;
import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.databinding.FragmentAddAlarmBinding;
import com.federicoberon.newapp.model.AlarmEntity;
import com.federicoberon.newapp.ui.CustomDatePicker;
import com.federicoberon.newapp.ui.addalarm.horoscope.HoroscopeDialogFragment;
import com.federicoberon.newapp.utils.DateUtils;
import com.federicoberon.newapp.utils.HoroscopeManager;
import com.federicoberon.newapp.utils.PostponeManager;
import com.federicoberon.newapp.utils.RepeatManager;
import com.federicoberon.newapp.utils.StringHelper;
import com.federicoberon.newapp.utils.VibrationManager;

import java.util.Calendar;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AddAlarmFragment extends Fragment implements TimePicker.OnTimeChangedListener {

    public static final String KEY_ALARM_ID = "alarm_id";
    public static final String KEY_DUPLICATE_ALARM = "duplicate selected alarm";
    private static final String LOG_TAG = "AddAlarmFragment";

    @Inject
    AddAlarmViewModel addAlarmViewModel;
    private FragmentAddAlarmBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Inject
    SharedPreferences sharedPref;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Injects this activity to the just created Registration component
        ((SimpleRemindMeApplication)requireActivity().getApplication()).appComponent.inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddAlarmBinding.inflate(inflater, container, false);

        //sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);

        Bundle bundle = this.getArguments();

        if (bundle!=null){
            if(bundle.containsKey(KEY_ALARM_ID)){

                    mDisposable.add(addAlarmViewModel.getAlarmById((Long) bundle.get(KEY_ALARM_ID))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(alarmEntity -> {

                            if(addAlarmViewModel.getInsertedAlarm()==null) {
                                addAlarmViewModel.setInsertedAlarm(alarmEntity);
                            }
                            if(bundle.containsKey(KEY_DUPLICATE_ALARM))
                                if(((boolean)bundle.get(KEY_DUPLICATE_ALARM)))
                                    addAlarmViewModel.setDuplicate();

                            populateUI(alarmEntity);
                    },
                    throwable -> Log.e(LOG_TAG, "Unable to load alarm: ", throwable)));
            }else {
                addAlarmViewModel.setNextAlarm(Calendar.getInstance());
                retrieveAlarmValues();
            }
        }else {
            addAlarmViewModel.setNextAlarm(Calendar.getInstance());
            retrieveAlarmValues();
        }

        return binding.getRoot();
    }

    private void updateMelodyValue(boolean melodyOn) {
        if(melodyOn)
            binding.ringtoneValue.setText(addAlarmViewModel.getSelectedMelody().getTitle());
        else
            binding.ringtoneValue.setText(getString(R.string.no_melody_string));
    }

    private void updateVibrationValue(boolean vibrationOn) {
        if (vibrationOn) {
            if (addAlarmViewModel.getSelectedVibration() == null)
                addAlarmViewModel.setSelectedVibration(VibrationManager.DEFAULT_VIBRATION);

            binding.vibrationValue.setText(addAlarmViewModel.getSelectedVibration());
        } else
            binding.vibrationValue.setText(getString(R.string.no_vibrate_string));
    }

    private void updateRepeatValue(boolean repeatOn) {
        if (!repeatOn)
            binding.repeatValue.setText(getString(R.string.no_repeat_string));
        else
            binding.repeatValue.setText(RepeatManager.getRepeatOptions(requireContext())
                    .get(addAlarmViewModel.getSelectedRepeat()));
    }

    private void updatePosptponeValue(boolean postponeOn) {
        if (!postponeOn)
            binding.postponeValue.setText(getString(R.string.no_postpone_string));
        else
            binding.postponeValue.setText(PostponeManager.getPostponeOptions(requireContext())
                    .get(addAlarmViewModel.getSelectedPostpone()));
    }



    /**
     * Update de fields in case of update alarm
     * @param alarmEntity data to load
     */
    private void populateUI(AlarmEntity alarmEntity) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(alarmEntity.getAlarmDate());
        ((MainActivity)requireActivity()).setTimePickerHeader(cal);

        addAlarmViewModel.setDaysOfWeek(DateUtils.getArrayBooleanDays(alarmEntity));

        binding.cbMonday.setChecked(alarmEntity.isMonday());
        binding.cbTuesday.setChecked(alarmEntity.isTuesday());
        binding.cbWednesday.setChecked(alarmEntity.isWednesday());
        binding.cbThursday.setChecked(alarmEntity.isThursday());
        binding.cbFriday.setChecked(alarmEntity.isFriday());
        binding.cbSaturday.setChecked(alarmEntity.isSaturday());
        binding.cbSunday.setChecked(alarmEntity.isSunday());
        binding.editTextAlarmName.setText(alarmEntity.getTitle());

        // only update viewmodel
        if(addAlarmViewModel.getSelectedMelody() == null && addAlarmViewModel.getInsertedAlarm() != null) {
            addAlarmViewModel.setMelodyOn(alarmEntity.isMelodyOn());
            mDisposable.add(addAlarmViewModel.getMelodyByName(alarmEntity.getMelodyName())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(melody -> {
                                addAlarmViewModel.setSelectedMelody(melody);
                                addAlarmViewModel.setPostponeOn(alarmEntity.isPostponeOn());
                                addAlarmViewModel.setRepeatOn(alarmEntity.isRepeatOn());
                                addAlarmViewModel.setVibrationOn(alarmEntity.isVibrationOn());
                                addAlarmViewModel.setHoroscopeOn(alarmEntity.isHoroscopeOn());
                                addAlarmViewModel.setSelectedPostpone(alarmEntity.getPostponeTime());
                                addAlarmViewModel.setSelectedRepeat(alarmEntity.getRepeatTime());
                                addAlarmViewModel.setSelectedVibration(alarmEntity.getVibrationPatter());
                                retrieveAlarmValues();
                            },
                            throwable -> Log.e("MIO", "Unable to get milestones: ", throwable)));
        }else{
            retrieveAlarmValues();
        }
    }

    private void retrieveAlarmValues() {
        //**** selected melody ****//
        if (addAlarmViewModel.getSelectedMelody() == null){
            Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(requireActivity()
                    .getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            Ringtone defaultRingtone = RingtoneManager
                    .getRingtone(getActivity(), defaultRingtoneUri);
            defaultRingtone.getTitle(requireContext());

            mDisposable.add(addAlarmViewModel.getMelodyByName(defaultRingtone.getTitle(requireContext()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(melody -> {
                                addAlarmViewModel.setSelectedMelody(melody);
                                if(addAlarmViewModel.isMelodyOn())
                                    binding.ringtoneValue.setText(melody.getTitle());
                                else
                                    binding.ringtoneValue.setText(getString(R.string.no_melody_string));
                            },
                            throwable -> Log.e("MIO", "Unable to get alarm: ", throwable)));

        }else {
            if (addAlarmViewModel.isMelodyOn())
                binding.ringtoneValue.setText(addAlarmViewModel.getSelectedMelody().getTitle());
            else
                binding.ringtoneValue.setText(getString(R.string.no_melody_string));
        }
        binding.ringtoneSwitch.setChecked(addAlarmViewModel.isMelodyOn());

        //**** selected vibration ****//
        binding.vibrationSwitch.setChecked(addAlarmViewModel.isVibrationOn());
        updateVibrationValue(addAlarmViewModel.isVibrationOn());

        //**** selected postpone ****//
        binding.postponeSwitch.setChecked(addAlarmViewModel.isPostponeOn());
        updatePosptponeValue(addAlarmViewModel.isPostponeOn());

        //**** selected repeat ****//
        binding.repeatSwitch.setChecked(addAlarmViewModel.isRepeatOn());
        updateRepeatValue(addAlarmViewModel.isRepeatOn());

        //**** selected repeat ****//
        binding.horoscopeSwitch.setChecked(addAlarmViewModel.isHoroscopeOn());

        String horoscope_id = sharedPref.getString(getString(R.string.sign_name), "aries");
        binding.horoscopeValue.setText(HoroscopeManager.getName(requireContext(), horoscope_id));
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((MainActivity)requireActivity()).setTimePickerHeader(Calendar.getInstance());
        }

        binding.setViewModel(addAlarmViewModel);
        binding.setFragment(this);

        // DatePicker
        binding.imageViewCalendar.setOnClickListener(v -> showDatePickerDialog());

        binding.okButton.setOnClickListener(v -> onSaveButtonClicked());
    }

    @Override
    public void onResume() {
        super.onResume();
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.nav_header_height);
        updateAllOptions();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // change next alarm listener
        addAlarmViewModel.getNextAlarm().observe(this, calendar ->{
            String textToSet = StringHelper.getFormatedAlarmDate(
                    requireContext(), addAlarmViewModel.getNextAlarm().getValue(),
                    addAlarmViewModel.getDaysOfWeek());

            ((MainActivity)requireActivity()).setCurrentTitle(textToSet);
            binding.textViewWhen.setText(textToSet);
        });

        // pick date listener
        ((MainActivity)requireActivity()).getBinding().appBarMain
                .timePicker.setOnTimeChangedListener(this);


        // Listener for de SharedPreference
        mListener = (prefs, key) -> {
            String horoscope_id = sharedPref.getString(getString(R.string.sign_name), "aries");
            binding.horoscopeValue.setText(HoroscopeManager.getName(requireContext(), horoscope_id));
        };
        sharedPref.registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPref.unregisterOnSharedPreferenceChangeListener(mListener);
        // clear all the subscriptions
        mDisposable.clear();
        binding = null;
    }

    private void onSaveButtonClicked() {
        mDisposable.add(addAlarmViewModel.saveAlarm(
                Objects.requireNonNull(binding.editTextAlarmName.getText()).toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                        addAlarmViewModel.setIdInsertedAlarm(id);
                        addAlarmViewModel.scheduledAlarm(requireActivity());
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_addAlarmFragment_to_nav_home);
                    },
                    throwable -> Log.e(LOG_TAG, "Unable to save Alarm", throwable)));

    }

    public void showDatePickerDialog(){
        new CustomDatePicker(addAlarmViewModel, binding.textViewWhen)
                .show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    // this fire when timepicker change
    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        addAlarmViewModel.setTime(hour, minute);
    }

    // callbacks from layout
    public void openRingtoneSelector(){
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_select_ringtone);
    }

    public void openVibrationSelector(){
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_select_vibrator);
    }

    public void openRepeatSelector(){
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_select_repeat);
    }

    public void openPostponeSelector(){
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_select_postpone);
    }

    public void openDiscardSelector(){
        Log.w("MIO", "SELECTORRRRRRRR");
    }

    private void updateAllOptions(){
        // update views
        changeColorsView(binding.textViewMelody, binding.ringtoneValue, addAlarmViewModel.isMelodyOn());
        changeColorsView(binding.textViewVibration, binding.vibrationValue, addAlarmViewModel.isVibrationOn());
        changeColorsView(binding.textViewPostpone, binding.postponeValue, addAlarmViewModel.isPostponeOn());
        changeColorsView(binding.textViewRepeat, binding.repeatValue, addAlarmViewModel.isRepeatOn());
        changeColorsView(binding.textViewHoroscope, binding.horoscopeValue, addAlarmViewModel.isHoroscopeOn());

        binding.ringtoneSwitch.setChecked(addAlarmViewModel.isMelodyOn());
        binding.vibrationSwitch.setChecked(addAlarmViewModel.isVibrationOn());
        binding.postponeSwitch.setChecked(addAlarmViewModel.isPostponeOn());
        binding.repeatSwitch.setChecked(addAlarmViewModel.isRepeatOn());
        binding.horoscopeSwitch.setChecked(addAlarmViewModel.isHoroscopeOn());
    }

    public void offRingtone(){
        boolean isChecked = binding.ringtoneSwitch.isChecked();
        addAlarmViewModel.setMelodyOn(isChecked);
        changeColorsView(binding.textViewMelody, binding.ringtoneValue, isChecked);
        updateMelodyValue(isChecked);
    }

    public void offVibration(){
        boolean isChecked = binding.vibrationSwitch.isChecked();
        addAlarmViewModel.setVibrationOn(isChecked);
        changeColorsView(binding.textViewVibration, binding.vibrationValue, isChecked);
        updateVibrationValue(isChecked);
    }

    public void offPostpone(){
        boolean isChecked = binding.postponeSwitch.isChecked();
        addAlarmViewModel.setPostponeOn(isChecked);
        changeColorsView(binding.textViewPostpone, binding.postponeValue, isChecked);
        updatePosptponeValue(isChecked);
    }

    public void offRepeat(){
        boolean isChecked = binding.repeatSwitch.isChecked();
        addAlarmViewModel.setRepeatOn(isChecked);
        changeColorsView(binding.textViewRepeat, binding.repeatValue, isChecked);
        updateRepeatValue(isChecked);
    }

    private void changeColorsView(TextView textView1, TextView textView2, boolean isChecked) {
        int color1;
        int color2;
        if(isChecked) {
            color1 = ContextCompat.getColor(requireContext(), R.color.white);
            color2 = ContextCompat.getColor(requireContext(), R.color.purple_200);
        }else {
            color1 = ContextCompat.getColor(requireContext(), R.color.white_transparent);
            color2 = ContextCompat.getColor(requireContext(), R.color.white_transparent);
        }
        textView1.setTextColor(color1);
        textView2.setTextColor(color2);
    }

    public void openSignSelector(){
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        HoroscopeDialogFragment horoscopeDialogFragment = HoroscopeDialogFragment.newInstance();
        horoscopeDialogFragment.show(fm, "fragment_terms_dialog");
    }

    public void offSign(){
        boolean isChecked = binding.horoscopeSwitch.isChecked();
        addAlarmViewModel.setHoroscopeOn(isChecked);
        changeColorsView(binding.textViewHoroscope, binding.horoscopeValue, isChecked);
    }
}