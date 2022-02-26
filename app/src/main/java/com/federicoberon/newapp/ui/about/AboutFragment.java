package com.federicoberon.newapp.ui.about;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.federicoberon.newapp.R;
import com.federicoberon.newapp.SimpleRemindMeApplication;
import com.federicoberon.newapp.databinding.FragmentAboutBinding;

import javax.inject.Inject;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Inject
    AboutViewModel aboutViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((SimpleRemindMeApplication) requireActivity().getApplicationContext())
                .appComponent.inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // TODO this fragment is not developed

        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        aboutViewModel.setText(getResources().getString(R.string.about_fragment_hint));
        aboutViewModel.getText().observe(getViewLifecycleOwner(), s -> binding.textAbout.setText(s));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}