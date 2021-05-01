package io.github.simonvar.sfl.ui.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

interface PropertyObserver {
    val propertyObserverLifecycleOwner: LifecycleOwner

    infix fun <T> Flow<T>.bind(consumer: (T) -> Unit) {
        propertyObserverLifecycleOwner.lifecycleScope.launchWhenStarted {
            this@bind.collect {
                consumer(it)
            }
        }
    }
}
