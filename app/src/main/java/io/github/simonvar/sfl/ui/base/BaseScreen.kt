package io.github.simonvar.sfl.ui.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

abstract class BaseScreen(
    @LayoutRes contentLayoutId: Int,
) : Fragment(contentLayoutId), PropertyObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
}