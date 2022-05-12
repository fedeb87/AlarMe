package com.federicoberon.alarme.ui.themes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.federicoberon.alarme.AlarMeApplication;
import com.federicoberon.alarme.MainActivity;
import com.federicoberon.alarme.databinding.FragmentThemesBinding;
import com.federicoberon.alarme.utils.ThemeUtil;

public class ThemesFragment extends Fragment {

    private FragmentThemesBinding binding;
    private ThemeAdapter mAdapter;

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).getBinding().appBarMain.appBar.setExpanded(false, false);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = 140;
        ((MainActivity) requireActivity()).getBinding().appBarMain.fab.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((AlarMeApplication) requireActivity().getApplicationContext())
                .appComponent.inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentThemesBinding.inflate(inflater, container, false);
        //requireContext().getTheme().applyStyle(R.style.AppThemeDark, true);
        initRecyclerView();
        prepareThemeData();
        return binding.getRoot();
    }

    private void prepareThemeData() {
        MainActivity.mThemeList.clear();
        MainActivity.mThemeList.addAll(ThemeUtil.getThemeList(requireContext()));
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        mAdapter = new ThemeAdapter(MainActivity.mThemeList, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requireActivity().recreate();
                        //MainActivity.this.recreate();
                    }
                }, 400);
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //requireContext().getTheme().applyStyle(R.style.SimpleRemindMe, true);
        binding = null;
    }

}