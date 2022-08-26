package com.gregorchristiaens.learningandroid.second

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SecondViewModel : ViewModel() {

    private val startValue = 10
    private var _time = MutableLiveData(10)
    val time: LiveData<Int> = _time

    /**
     * This Coroutine Flow is what we call a Cold Flow.
     * This flow will not do anything until it is subscribed to.
     * Another possible flow declaration would be a Hot Flow and these would run regardless of subscribers.
     */
    private val countDownFlow = flow<Int> {
        var currentValue = startValue
        emit(startValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    fun startCountDown() {
        viewModelScope.launch {
            /**
             * The collect function will collect each incoming value change and process it.
             * Think of this as a FIFO, if the processing we do in this function is slow and new values
             * arrive before our processing of the old is done. We will build up a small delay. But each value will still be processed.
             */
            countDownFlow.collect { time ->
                _time.value = time
            }
            /**
             * The collectLatest function will collect each incoming value change but only process the last received value.
             * Think of this as a LIFO, if the processing we do in this function is slow and new values arrive before our processing
             * of the old is done. We will cancel the processing and instead start processing the new value.
             *
             * In the case of our timer we can add a manual delay of 1,5 s to this ui update.
             * The incoming time values arrive every 1 s so we will never have time to update the ui because the processing will restart
             * after every 1 s. Only our final value of 0 will be displayed.
             *
             * In a lot of common UI changes you will always want to display the latest value available.
             * Showing previous or outdated values often has no use.
             */
            /*countDownFlow.collectLatest { time ->
                delay(1500L)
                _time.value = time
            }*/
        }
    }

}