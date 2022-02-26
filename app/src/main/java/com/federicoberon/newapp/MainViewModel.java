package com.federicoberon.newapp;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainViewModel extends ViewModel {

    private String currentTitle;

    @Inject
    public MainViewModel() {
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public void setCurrentTitle(String currentTitle) {
        this.currentTitle = currentTitle;
    }
}