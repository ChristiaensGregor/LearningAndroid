package com.gregorchristiaens.learningandroid.second

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SecondViewModel : ViewModel() {

    /**
     * StateFlow Begin
     */
    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    /**
     * StateFlow End
     */

    /**
     * SharedFlow Start
     */
    /**
     * The replay variable in the constructor of a MutableShardFlow allows you to
     * set a cache. In this case the first 5 values will be stored an cashed for any future collectors.
     */
    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5)
    val sharedFlow = _sharedFlow.asSharedFlow()

    private fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }

    /**
     * SharedFlow is a hard flow if we initialize our ViewModel and call SquareNumber before
     * assigning anny collectors the values are lost. This Flow only stores a value once it has a collector.
     */
    init {
        squareNumber(4)
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("SharedFlow: First Collect: $it")
            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(4000L)
                println("SharedFlow: Second Collect: $it")
            }
        }
        //squareNumber(4)
    }

    /**
     * SharedFlow End
     */

    private val startValue = 10
    private var _time = MutableLiveData("10")
    val time: LiveData<String> = _time

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
             * Basic Flow Operators
             */
            /**
             * You can filter the incoming values and only process those values that pass the filter.
             * In this case we only want to display the even numbers.
             *
             * You can map the incoming values and change their value.
             * In this case we want to use the counter in the ui so we turn it into a String.
             * Do be wary of the order of these operators. They are handled sequentially so turning the values
             * into a String and then trying to perform a Int operation on it would not work.
             *
             * You can perform a onEach operation on the values.
             * Similar to collect this basically allows you to do some processing on the values.
             * The difference being that after the processing this returns a flow with the processed values
             * while collect will not return anything.
             */
            countDownFlow.filter { time ->
                time % 2 == 0
            }.map { time ->
                time.toString()
            }.onEach { time ->
                println(time)
            }.collect { time ->
                _time.value = time
            }

            /**
             * Terminal Flow Operators - these end the flow once finished
             */
            /**
             * You can count the number of elements with the count operator.
             * This allows you to add a filter and then counts all the matching values.
             */
            val evenCount = countDownFlow.count {
                it % 2 == 0
            }
            println(evenCount)
            /**
             * You can reduce the number of elements down to one element with the reduce operator.
             * This gives you the previous value (accumulator in this case) and the current value (value in this case)
             * and you can perform any operation as long as there is one single result to pas on to the next cycle.
             */
            val addAllTimeValues = countDownFlow.reduce { accumulator, value ->
                accumulator + value
            }
            println(addAllTimeValues)
            /**
             * This works in a very similar fashion to reduce but you can pass along an initial value.
             * In this case for example -15.
             */
            val addAllTimeValuesMinStartup = countDownFlow.fold(-15) { total, value ->
                total + value
            }
            println(addAllTimeValuesMinStartup)

            /**
             * Flattening Flow Operators -
             */
        }
    }

    /**
     * Transforms elements emitted by the original flow by applying transform, that returns another flow, and then concatenating and flattening these flows.
     *  In most cases a normal map is a lot more efficient so take care using this.
     */
    private fun collectFlow1() {
        val flow_1 = (1..5).asFlow()
        viewModelScope.launch {
            flow_1.flatMapConcat { id ->
                getRecipeById(id)
            }.collect { value ->
                println(value)
            }
        }
    }

    /**
     * Transforms elements emitted by the original flow by applying transform, that returns another flow, and then merging and flattening these flows.
     * In most cases a normal map is a lot more efficient so take care using this.
     */
    private fun collectFlow2() {
        val flow_1 = (1..5).asFlow()
        viewModelScope.launch {
            flow_1.flatMapMerge { id ->
                getRecipeById(id)
            }.collect { value ->
                println(value)
            }
        }
    }

    /**
     * Only the last flow to produce a result is returned
     */
    private fun collectFlow3() {
        val flow_1 = (1..5).asFlow()
        viewModelScope.launch {
            flow_1.flatMapLatest { id ->
                getRecipeById(id)
            }.collect { value ->
                println(value)
            }
        }
    }

    private fun getRecipeById(id: Int): Flow<Int> {
        return (6..10).asFlow()
    }


    /**
     * The flow emits the different dishes each after a delay.
     * In the CoroutineScope we process the incoming dishes.
     * The returned values would be:
     * Appetizer is delivered
     * Now eating Appetizer
     * ...
     * And only once Appetizer is finished processing do we handle the next received value.
     * Main dish is delivered
     * ...
     */
    private fun collectFlow4() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach { dish ->
                println("FLOW: $dish is delivered")
            }.collect { dish ->
                println("FLOW: Now eating $dish")
                delay(1500L)
                println("FLOW: Finished eating $dish")
            }
        }
    }

    /**
     * Using a buffer we can allow the flow to handle the incoming values on separate coroutines.
     * This means that the Dessert could arrive before we finish eating the Main dish.
     * This results in faster processing of values in the before collect phases.
     * Do note that in collect we will still process them in the order they arrived.
     *
     * We wont start eating the main dish before we finish eating the appetizer.
     * The collect phase is still sequential and handles things one one plane.
     *
     * This will be significantly faster while also maintaining some sequence relation in the collect phase.
     * You can basically do anything non sequential in the post collect processing. (filtering , mapping, ect)
     */
    private fun collectFlow5() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach { dish ->
                println("FLOW: $dish is delivered")
            }.buffer().collect { dish ->
                println("FLOW: Now eating $dish")
                delay(1500L)
                println("FLOW: Finished eating $dish")
            }
        }
    }

    /**
     * Conflate will once a value is received start Collecting, if other values come in it will resume collection for the last value it received.
     * So if we start eating (collect phase) the Appetizer we can still receive values and handle map, onEach.
     * But we won't start collecting until our appetizer is finished. Once finished instead of taking the first value received after Appetizer.
     * We take the last value we received in this case Dessert.
     *
     * Fast processing but can skip collection of incoming values. Great to make sure the user gets a fast initial view and then always updated to get the last new value.
     */
    private fun collectFlow6() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach { dish ->
                println("FLOW: $dish is delivered")
            }.conflate().collect { dish ->
                println("FLOW: Now eating $dish")
                delay(1500L)
                println("FLOW: Finished eating $dish")
            }
        }
    }

    /**
     * This will receive the values from the flow, and start processing on a separate coroutine.
     * If another value arrives before we finished processing we will abort our process and instead start collection of the new value.
     * If their were to be a constant flow of updates this would take longer to give a user a first view. But it will result in the fastest time
     * from delivery to on screen processing wise.
     */
    private fun collectFlow7() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        viewModelScope.launch {
            flow.onEach { dish ->
                println("FLOW: $dish is delivered")
            }.collectLatest { dish ->
                println("FLOW: Now eating $dish")
                delay(1500L)
                println("FLOW: Finished eating $dish")
            }
        }
    }


}