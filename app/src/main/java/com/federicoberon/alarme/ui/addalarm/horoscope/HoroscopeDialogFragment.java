package com.federicoberon.alarme.ui.addalarm.horoscope;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.databinding.FragmentHoroscopeDialogBinding;

import javax.inject.Inject;

public class HoroscopeDialogFragment extends DialogFragment {
    private FragmentHoroscopeDialogBinding binding;

    @Inject
    SharedPreferences sharedPref;

    public HoroscopeDialogFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Injects this activity to the just created Registration component
        ((AlarMeApplication)requireActivity().getApplication()).appComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHoroscopeDialogBinding.inflate(inflater, container, false);

        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String selectedSign = sharedPref.getString(getString(R.string.sign_name), "aries");

        configRootView();
        populateUI(selectedSign);
    }

    private void populateUI(String selectedSign) {
        switch(selectedSign) {
            case "aries":
                binding.aries.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "taurus":
                binding.taurus.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "gemini":
                binding.gemini.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "cancer":
                binding.cancer.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "leo":
                binding.leo.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "virgo":
                binding.virgo.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "libra":
                binding.libra.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "scorpio":
                binding.scorpio.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "sagittarius":
                binding.sagittarius.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "capricorn":
                binding.capricorn.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "aquarius":
                binding.aquarius.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            case "pisces":
                binding.pisces.setBackgroundColor(getResources().getColor(R.color.purple_700));
                break;
            default:
                break;
        }
    }

    public static HoroscopeDialogFragment newInstance() {
        return new HoroscopeDialogFragment();
    }

    public void configRootView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.95f);
        int dialogWindowHeight = (int) (displayHeight * 0.70f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        getDialog().getWindow().setAttributes(layoutParams);
    }

    public void signSelected(View view){

        String sign;
        switch(view.getId()){
            case R.id.taurus:
                sign = "taurus";
                break;
            case R.id.gemini:
                sign = "gemini";
                break;
            case R.id.cancer:
                sign = "cancer";
                break;
            case R.id.leo:
                sign = "leo";
                break;
            case R.id.virgo:
                sign = "virgo";
                break;
            case R.id.libra:
                sign = "libra";
                break;
            case R.id.scorpio:
                sign = "scorpio";
                break;
            case R.id.sagittarius:
                sign = "sagittarius";
                break;
            case R.id.capricorn:
                sign = "capricorn";
                break;
            case R.id.aquarius:
                sign = "aquarius";
                break;
            case R.id.pisces:
                sign = "pisces";
                break;
            default:
                sign = "aries";
                break;

        }

        //SharedPreferences sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.sign_name), sign);
        editor.apply();
        dismiss();
    }
}
