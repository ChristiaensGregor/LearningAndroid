package com.gregorchristiaens.learningandroid.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SecondViewModel : ViewModel() {

    private var _defaultText = MutableLiveData("Text from ViewModel")
    val defaultText: LiveData<String> = _defaultText

}