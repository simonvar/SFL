package io.github.simonvar.sfl

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.commitNow
import io.github.simonvar.sfl.databinding.ActivityMainBinding
import io.github.simonvar.sfl.ui.dictaphone.DictaphoneScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setWindowTransparency()

        supportFragmentManager.commitNow {
            add(R.id.container, DictaphoneScreen())
        }
    }

    private fun setWindowTransparency() {
        removeSystemInsets(window.decorView)
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun removeSystemInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, 0)
            )
        }
    }
}