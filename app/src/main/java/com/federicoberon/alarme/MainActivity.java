package com.federicoberon.alarme;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.federicoberon.alarme.databinding.ActivityMainBinding;
import com.federicoberon.alarme.ui.addalarm.AddAlarmViewModel;
import com.federicoberon.alarme.ui.home.HomeFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity  {
    public static final String GENERATED_USER_CODE = "random";
    private static final int DATA_CHECK_CODE = 101;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private ActivityMainBinding binding;
    private NavController navController;

    @Inject
    MainViewModel mMainViewModel;

    @Inject
    AddAlarmViewModel mAddAlarmViewModel;

    @Inject
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((AlarMe) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        setCollapsingToolbarBehaviour(binding);

        binding.appBarMain.fab.setOnClickListener(view -> goToAddMilestone());

        drawer = binding.drawerLayout;
        Toolbar mToolbar = binding.appBarMain.toolbar;
        setSupportActionBar(mToolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navController.addOnDestinationChangedListener((navController, navDestination, bundle)
                -> binding.appBarMain.appBar.setExpanded(true, true));

        // set code number for current user
        if (!sharedPref.contains(GENERATED_USER_CODE)){
            // this is the first time that app runs
            int code = new Random().nextInt(367);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(GENERATED_USER_CODE, code);
            editor.apply();
        }

        // check storage permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
    }

    /**
     * Show app title only when appbar are collapsed
     * @param binding binding of main activity
     */
    private void setCollapsingToolbarBehaviour(ActivityMainBinding binding) {
        CollapsingToolbarLayout collapsingToolbarLayout = binding.appBarMain.collapsingToolbar;
        AppBarLayout appBarLayout = binding.appBarMain.appBar;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(mMainViewModel.getCurrentTitle());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void goToAddMilestone() {
        mAddAlarmViewModel.restart();
        navController.navigate(R.id.action_nav_home_to_addAlarmFragment);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (Objects.equals(getForegroundFragment(), HomeFragment.class)) finish();
            super.onBackPressed();
        }
    }

    private Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setHeader(String next_alarm){
        if(next_alarm.isEmpty()){
            binding.appBarMain.linearLayoutTitlesActive.setVisibility(View.GONE);
            binding.appBarMain.linearLayoutTitlesInactive.setVisibility(View.VISIBLE);
        }else {
            binding.appBarMain.linearLayoutTitlesActive.setVisibility(View.VISIBLE);
            binding.appBarMain.linearLayoutTitlesInactive.setVisibility(View.GONE);
        }
        binding.appBarMain.timePicker.setVisibility(View.GONE);
        binding.appBarMain.textViewFirstTitle.setText(R.string.next_alarm_string);
        binding.appBarMain.textViewNextSecondTitle.setText(next_alarm);
        binding.appBarMain.fab.setVisibility(View.VISIBLE);
    }

    public void setTimePickerHeader(Calendar calendar){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.appBarMain.timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            binding.appBarMain.timePicker.setMinute(calendar.get(Calendar.MINUTE));
        }else{
            binding.appBarMain.timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            binding.appBarMain.timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
        binding.appBarMain.linearLayoutTitlesActive.setVisibility(View.GONE);
        binding.appBarMain.linearLayoutTitlesInactive.setVisibility(View.GONE);
        binding.appBarMain.timePicker.setVisibility(View.VISIBLE);
        binding.appBarMain.timePicker.setIs24HourView(true);
        binding.appBarMain.fab.setVisibility(View.GONE);
    }

    public void setCurrentTitle(String title){
        mMainViewModel.setCurrentTitle(title);
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }
}