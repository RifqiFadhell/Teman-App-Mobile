package id.teman.app.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun debounce(
    waitMs: Long = 300L,
    scope: CoroutineScope,
    destinationFunction: () -> Unit
) {
    var debounceJob: Job? = null
    debounceJob?.cancel()
    debounceJob = scope.launch {
        delay(waitMs)
        destinationFunction()
    }
}