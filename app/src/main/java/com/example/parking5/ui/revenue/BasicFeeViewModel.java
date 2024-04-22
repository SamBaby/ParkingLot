package com.example.parking5.ui.revenue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BasicFeeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BasicFeeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("車位營收");
    }

    public LiveData<String> getText() {
        return mText;
    }
}