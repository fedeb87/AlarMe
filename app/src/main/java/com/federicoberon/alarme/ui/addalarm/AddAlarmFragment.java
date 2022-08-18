package com.federicoberon.alarme.ui.addalarm;

import static com.federicoberon.alarme.MainActivity.ACCESS_LOCATION_CODE;
import static com.federicoberon.alarme.MainActivity.ENABLE_LOGS;
import static com.federicoberon.alarme.MainActivity.LAT_KEY;
import static com.federicoberon.alarme.MainActivity.LON_KEY;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.databinding.FragmentAddAlarmBinding;
import com.federicoberon.alarme.model.AlarmEntity;
import com.federicoberon.alarme.model.MelodyEntity;
import com.federicoberon.alarme.service.AlarmService;
import com.federicoberon.alarme.ui.CustomDatePicker;
import com.federicoberon.alarme.ui.addalarm.horoscope.HoroscopeDialogFragment;
import com.federicoberon.alarme.utils.DateUtils;
import com.federicoberon.alarme.utils.HoroscopeManager;
import com.federicoberon.alarme.utils.PostponeManager;
import com.federicoberon.alarme.utils.RepeatManager;
import com.federicoberon.alarme.utils.StringHelper;
import com.federicoberon.alarme.utils.VibrationManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AddAlarmFragment extends Fragment implements TimePicker.OnTimeChangedListener  {

    public static final String KEY_ALARM_ID = "alarm_id";
    public static final String KEY_DUPLICATE_ALARM = "duplicate selected alarm";
    private static final String LOG_TAG = "<<<AddAlarmFragment>>>";

    @Inject
    AddAlarmViewModel addAlarmViewModel;

    private FragmentAddAlarmBinding binding;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private Snackbar mSnackbar;

    @Inject
    SharedPreferences sharedPref;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Injects this activity to the just created Registration component
        ((AlarMeApplication)requireActivity().getApplication()).appComponent.inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddAlarmBinding.inflate(inflater, container, false);
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
                    throwable -> {
                        if(sharedPref.getBoolean(ENABLE_LOGS, false))
                            Log.e(LOG_TAG, "Unable to load alarm: ", throwable);
                    }));
            }else {
                addAlarmViewModel.setNextAlarm();
                retrieveAlarmValues();
            }
        }else {
            addAlarmViewModel.setNextAlarm();
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

    private void updatePhrasesValue(boolean phrasesOn) {
        if (!phrasesOn)
            binding.phrasesValue.setText(getString(R.string.no_phrases_string));
        else
            binding.phrasesValue.setText(getString(R.string.phrases_string));
    }

    private void updatePostponeValue(boolean postponeOn) {
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
        if(addAlarmViewModel.isTimeChange()){
            cal.set(Calendar.HOUR_OF_DAY, addAlarmViewModel.getmHour());
            cal.set(Calendar.MINUTE, addAlarmViewModel.getmMinutes());
        }

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
        addAlarmViewModel.setPostponeOn(alarmEntity.isPostponeOn());
        addAlarmViewModel.setRepeatOn(alarmEntity.isRepeatOn());
        addAlarmViewModel.setVibrationOn(alarmEntity.isVibrationOn());
        addAlarmViewModel.setHoroscopeOn(alarmEntity.isHoroscopeOn());
        addAlarmViewModel.setWeatherOn(alarmEntity.isWeatherOn());
        addAlarmViewModel.setSelectedPostpone(alarmEntity.getPostponeTime());
        addAlarmViewModel.setSelectedRepeat(alarmEntity.getRepeatTime());
        addAlarmViewModel.setSelectedVibration(alarmEntity.getVibrationPatter());

        if(addAlarmViewModel.getSelectedMelody() == null && addAlarmViewModel.getInsertedAlarm() != null) {
            addAlarmViewModel.setMelodyOn(alarmEntity.isMelodyOn());
            mDisposable.add(addAlarmViewModel.getMelodyByName(alarmEntity.getMelodyName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MelodyEntity>() {
                   @Override
                   public void onSuccess(@NonNull MelodyEntity melodyEntity) {
                       addAlarmViewModel.setSelectedMelody(melodyEntity);
                       retrieveAlarmValues();
                   }

                   @Override
                   public void onError(@NonNull Throwable throwable) {
                       if (throwable.getCause() == null) {
                           //your action if null
                           MelodyEntity melodyEntity = new MelodyEntity(alarmEntity.getMelodyName(), alarmEntity.getMelodyUri());
                           addAlarmViewModel.setSelectedMelody(melodyEntity);
                           retrieveAlarmValues();
                       }
                   }
               }));
        }else{
            retrieveAlarmValues();
        }
    }

    public void retrieveAlarmValues() {
        //**** selected melody ****//
        if (addAlarmViewModel.getSelectedMelody() == null){
            Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(requireActivity()
                    .getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            Ringtone defaultRingtone = RingtoneManager
                    .getRingtone(getActivity(), defaultRingtoneUri);

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
                    throwable -> {
                        if(sharedPref.getBoolean(ENABLE_LOGS, false))
                            Log.e(LOG_TAG, "Unable to get alarm: ", throwable);
                    }));

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
        updatePostponeValue(addAlarmViewModel.isPostponeOn());

        //**** selected repeat ****//
        binding.repeatSwitch.setChecked(addAlarmViewModel.isRepeatOn());
        updateRepeatValue(addAlarmViewModel.isRepeatOn());

        //**** selected sign ****//
        binding.horoscopeSwitch.setChecked(addAlarmViewModel.isHoroscopeOn());
        String horoscope_id = sharedPref.getString(getString(R.string.sign_name), "aries");
        binding.horoscopeValue.setText(HoroscopeManager.getName(requireContext(), horoscope_id));

        //**** selected weather ****//
        binding.weatherSwitch.setChecked(addAlarmViewModel.isWeatherOn());
        changeColorsView(binding.textViewWeather, null, addAlarmViewModel.isWeatherOn());

        //**** selected phrases ****//
        binding.phrasesSwitch.setChecked(addAlarmViewModel.isPhrasesOn());
        updatePhrasesValue(addAlarmViewModel.isPhrasesOn());
    }

    @Override
    public void onStart() {
        super.onStart();

        Calendar cal = Calendar.getInstance();
        if(addAlarmViewModel.isTimeChange()){
            cal.set(Calendar.HOUR_OF_DAY, addAlarmViewModel.getmHour());
            cal.set(Calendar.MINUTE, addAlarmViewModel.getmMinutes());
        }

        ((MainActivity)requireActivity()).setTimePickerHeader(cal);

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

        checkLocationPermissions();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NotificationManager nm = (NotificationManager) requireActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!nm.isNotificationPolicyAccessGranted()) {
                // Permissions were not granted, request for permission
                showPermissionsOnLockDialog();
            }
        }

        // change next alarm listener
        addAlarmViewModel.getNextAlarm().observe(getViewLifecycleOwner(), calendar ->{
            String textToSet = StringHelper.getFormatedAlarmDate(
                    requireContext(), addAlarmViewModel.getNextAlarm().getValue(),
                    addAlarmViewModel.getDaysOfWeek());

            ((MainActivity)requireActivity()).setCurrentTitle(textToSet);
            binding.textViewWhen.setText(textToSet);
        });

        // pick date listener
        ((MainActivity)requireActivity()).getBinding().appBarMain.timePicker.setAddStatesFromChildren(true);
        ((MainActivity)requireActivity()).getBinding().appBarMain.timePicker
                .setOnTimeChangedListener(this);

        // Listener for de SharedPreference
        mListener = (prefs, key) -> {
            if (key.equals(getString(R.string.sign_name))) {
                String horoscope_id = sharedPref.getString(getString(R.string.sign_name), "aries");
                binding.horoscopeValue.setText(HoroscopeManager.getName(requireContext(), horoscope_id));
            }
        };
        sharedPref.registerOnSharedPreferenceChangeListener(mListener);
    }

    private void checkLocationPermissions() {
        if(!sharedPref.contains(LAT_KEY) && addAlarmViewModel.isWeatherOn()){

            if (AlarmService.locationEnabled(requireContext())){
                if(ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(),
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED){

                    showSnackBar(R.string.access_location_permission_snackbar);

                }else{
                    dismissSnackbar();
                    // get location for first time
                    getCurrentLocation();
                }
            }else {
                // decirle que debe habilitar para acceder a su ubicacion actual
                showSnackBar2(R.string.enable_location_snackbar);
            }
        }else
            dismissSnackbar();
    }

    private void dismissSnackbar() {
        binding.okButton.setEnabled(true);
        addAlarmViewModel.setWeatherOn(binding.weatherSwitch.isChecked());
        if(mSnackbar != null)
            mSnackbar.dismiss();

    }

    private void showSnackBar2(int messageId) {
        binding.okButton.setEnabled(false);

        if (mSnackbar == null || !mSnackbar.isShown()) {
            mSnackbar = Snackbar.make(binding.getRoot(), messageId, Snackbar.LENGTH_INDEFINITE);
            mSnackbar.setAction(R.string.go_Settings, view -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
            mSnackbar.show();
        }
    }

    private void showSnackBar(int messageId) {

        binding.okButton.setEnabled(false);
        if (mSnackbar == null || !mSnackbar.isShown()) {
            mSnackbar = Snackbar.make(binding.getRoot(), messageId, Snackbar.LENGTH_INDEFINITE);
            mSnackbar.setAction(R.string.go_Settings, view -> startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().getPackageName()))));
            mSnackbar.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPref.unregisterOnSharedPreferenceChangeListener(mListener);
        // clear all the subscriptions
        mDisposable.clear();
        binding = null;
    }

    private void showPermissionsOnLockDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(getString(R.string.ring_permission_title))
            .setMessage(getString(R.string.ring_permission_msg))
            .setPositiveButton(getString(R.string.go_Settings), (dialog, which) -> {
                @SuppressLint("InlinedApi")
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            })
            .setNegativeButton(getString(R.string.cancel_button), null)
            .show();
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
                    throwable -> {
                        if(sharedPref.getBoolean(ENABLE_LOGS, false))
                            Log.e(LOG_TAG, "Unable to save Alarm", throwable);
                    }));

    }

    public void showDatePickerDialog(){
        new CustomDatePicker(addAlarmViewModel)
                .show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    // Fired when timepicker changed
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

    private void updateAllOptions(){
        // update views
        changeColorsView(binding.textViewMelody, binding.ringtoneValue, addAlarmViewModel.isMelodyOn());
        changeColorsView(binding.textViewVibration, binding.vibrationValue, addAlarmViewModel.isVibrationOn());
        changeColorsView(binding.textViewPostpone, binding.postponeValue, addAlarmViewModel.isPostponeOn());
        changeColorsView(binding.textViewRepeat, binding.repeatValue, addAlarmViewModel.isRepeatOn());
        changeColorsView(binding.textViewHoroscope, binding.horoscopeValue, addAlarmViewModel.isHoroscopeOn());
        changeColorsView(binding.textViewWeather, null, addAlarmViewModel.isWeatherOn());
        changeColorsView(binding.textViewPhrases, binding.phrasesValue, addAlarmViewModel.isPhrasesOn());

        binding.ringtoneSwitch.setChecked(addAlarmViewModel.isMelodyOn());
        binding.vibrationSwitch.setChecked(addAlarmViewModel.isVibrationOn());
        binding.postponeSwitch.setChecked(addAlarmViewModel.isPostponeOn());
        binding.repeatSwitch.setChecked(addAlarmViewModel.isRepeatOn());
        binding.horoscopeSwitch.setChecked(addAlarmViewModel.isHoroscopeOn());
        binding.weatherSwitch.setChecked(addAlarmViewModel.isWeatherOn());
        binding.phrasesSwitch.setChecked(addAlarmViewModel.isPhrasesOn());
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
        updatePostponeValue(isChecked);
    }

    public void offRepeat(){
        boolean isChecked = binding.repeatSwitch.isChecked();
        addAlarmViewModel.setRepeatOn(isChecked);
        changeColorsView(binding.textViewRepeat, binding.repeatValue, isChecked);
        updateRepeatValue(isChecked);
    }

    private void enableDaysOfWeek(boolean b) {
        binding.cbMonday.setEnabled(b);
        binding.cbTuesday.setEnabled(b);
        binding.cbWednesday.setEnabled(b);
        binding.cbThursday.setEnabled(b);
        binding.cbFriday.setEnabled(b);
        binding.cbSaturday.setEnabled(b);
        binding.cbSunday.setEnabled(b);
    }

    public void offPhrases(){
        boolean isChecked = binding.phrasesSwitch.isChecked();
        addAlarmViewModel.setPhrasesOn(isChecked);
        changeColorsView(binding.textViewPhrases, binding.phrasesValue, isChecked);
        updatePhrasesValue(isChecked);
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
        if(textView2!=null)
            textView2.setTextColor(color2);
    }

    public void openSignSelector(){
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        HoroscopeDialogFragment horoscopeDialogFragment = HoroscopeDialogFragment.newInstance();
        horoscopeDialogFragment.show(fm, "fragment_terms_dialog");
    }

    public void offHoroscope(){
        boolean isChecked = binding.horoscopeSwitch.isChecked();
        addAlarmViewModel.setHoroscopeOn(isChecked);
        changeColorsView(binding.textViewHoroscope, binding.horoscopeValue, isChecked);

        if(!sharedPref.contains(getString(R.string.sign_name))){
            openSignSelector();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
                dismissSnackbar();
            // Other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void offWeather(){
        boolean isChecked = binding.weatherSwitch.isChecked();

        if(isChecked){
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity()
                        , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                        , android.Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_CODE);
            }
        }

        addAlarmViewModel.setWeatherOn(isChecked);
        changeColorsView(binding.textViewWeather, null, isChecked);
        checkLocationPermissions();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putFloat(LAT_KEY, (float) location.getLatitude());
                        editor.putFloat(LON_KEY, (float) location.getLongitude());
                        editor.apply();

                    } else {
                        requestNewLocation(fusedLocationClient);
                    }
                });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    private void requestNewLocation(FusedLocationProviderClient fusedLocationClient) {

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat(LAT_KEY, (float) locationResult.getLastLocation().getLatitude());
                editor.putFloat(LON_KEY, (float) locationResult.getLastLocation().getLongitude());
                editor.apply();
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(mLocationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    public void readTitleOff(){
        addAlarmViewModel.setReadTitle(false);
        binding.imageTitleOn.setVisibility(View.GONE);
        binding.imageTitleOff.setVisibility(View.VISIBLE);
    }

    public void readTitleOn(){
        addAlarmViewModel.setReadTitle(true);
        binding.imageTitleOn.setVisibility(View.VISIBLE);
        binding.imageTitleOff.setVisibility(View.GONE);
    }
}