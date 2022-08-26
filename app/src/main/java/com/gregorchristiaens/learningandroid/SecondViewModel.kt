package com.gregorchristiaens.learningandroid

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SecondViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    /**
     * Found no leaks in early test phases.
     * In theory the AndroidViewModel should provide a safe access to the application
     * and with it the application context.
     * The application context should in most cases outlive the fragment or activity.
     * This does however create issues when it comes to unit testing because
     * these should not have to deal with the android lifecycle and such.
     */
    private val context = getApplication<Application>().applicationContext
    private var connectivityObserver: ConnectivityObserver = NetworkConnectivityObserver(context)
    private var _status = MutableLiveData(ConnectivityObserver.Status.Unavailable)
    val status: LiveData<ConnectivityObserver.Status> = _status

    init {
        viewModelScope.launch {
            connectivityObserver.observe().collect {
                _status.value = it
                Log.d("Connectivity", status.toString())
            }
        }
    }

}