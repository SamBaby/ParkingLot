package com.example.parking5.ui.regular;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegularViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public RegularViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("月租管理");
    }

    public LiveData<String> getText() {
        return mText;
    }
}