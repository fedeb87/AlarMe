package com.federicoberon.newapp;

import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.federicoberon.newapp.databinding.ActivityMainBinding;
import com.federicoberon.newapp.ui.home.HomeFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Calendar;
import java.util.Objects;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity  {
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private ActivityMainBinding binding;
    private NavController navController;

    @Inject
    MainViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((SimpleRemindMeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);


        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        //Cursor cursor = manager.getCursor();
        //manager.getRingtoneUri()
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Cursor cursor = manager.getCursor();
        cursor.moveToNext();



/*
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String title2 = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            Uri ringtoneURI = manager.getRingtoneUri(cursor.getPosition());
            // Do something with the title and the URI of ringtone
            Log.w("MIO", "ringtoneURI-------" + ringtoneURI.getPath());
            Log.w("MIO", "title-------" + title);
            Log.w("MIO", "title2-------" + title2);
        }*/


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
    }

    /**
     * Show app title only when appbar are collapsed
     * @param binding
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
        navController.navigate(R.id.action_nav_home_to_addAlarmFragment);
    }

    public void goToHome() {
        navController.navigate(R.id.action_addAlarmFragment_to_nav_home);
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
        binding.appBarMain.linearLayoutTitles.setVisibility(View.VISIBLE);
        binding.appBarMain.timePicker.setVisibility(View.GONE);
        binding.appBarMain.textViewFirstTitle.setText(R.string.next_alarm_string);
        binding.appBarMain.textViewNextSecondTitle.setText(next_alarm);
        binding.appBarMain.fab.setVisibility(View.VISIBLE);
    }

    public void setTimePickerHeader(Calendar calendar){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.appBarMain.timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            binding.appBarMain.timePicker.setMinute(calendar.get(Calendar.MINUTE));
        }
        binding.appBarMain.linearLayoutTitles.setVisibility(View.GONE);
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