package com.federicoberon.alarme.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AboutViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    @Inject
    public AboutViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(String fragmentHint) {
        mText.setValue(fragmentHint);
    }
}