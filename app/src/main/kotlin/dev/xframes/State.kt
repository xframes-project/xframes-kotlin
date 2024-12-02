package dev.xframes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Define a simple data holder class (like a ViewModel replacement)
data class CounterState(val count: Int)

class CounterService {
    private val _state = MutableStateFlow(CounterState(0))
    val state: StateFlow<CounterState> = _state

    fun increment() {
        _state.value = _state.value.copy(count = _state.value.count + 1)
    }
}

