package com.example.workmysets.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.workmysets.R
import com.example.workmysets.data.database.AppDatabase
import com.example.workmysets.data.models.Exercise
import com.example.workmysets.fragments.HomeFragment
import com.example.workmysets.fragments.SessionsFragment
import com.example.workmysets.fragments.WorkoutsFragment
import com.example.workmysets.ui.interfaces.ImplementBackButton
import com.example.workmysets.utils.Consts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity(), ImplementBackButton {
    private lateinit var bottomBar: SmoothBottomBar
    private var currentIndex = -1
    private val homeFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomBar = findViewById(R.id.bottomBar)

        if (intent.hasExtra(Consts.ARG_REDIRECT_PAGE)) {
            val page = intent.getIntExtra(Consts.ARG_REDIRECT_PAGE, 0)
            setCurrentFragment(page)
            bottomBar.itemActiveIndex = page
        } else {
            setCurrentFragment(bottomBar.itemActiveIndex)
        }

        bottomBar.onItemSelected = { index ->
            setCurrentFragment(index)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentIndex != homeFragmentIndex) {
                    bottomBar.itemActiveIndex = homeFragmentIndex
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
                setCurrentFragment(bottomBar.itemActiveIndex)
            }
        })
    }

    private fun setCurrentFragment(targetIndex: Int) {
        if (currentIndex == targetIndex) {
            return
        }

        val fromIndex = currentIndex
        currentIndex = targetIndex

        val direction = targetIndex - fromIndex

        val enterAnim = if (direction > 0) R.anim.slide_in_right else R.anim.slide_in_left
        val exitAnim = if (direction > 0) R.anim.slide_out_left else R.anim.slide_out_right

        val fragment = getFragments(targetIndex)

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun getFragments(index: Int): Fragment = when (index) {
        0 -> HomeFragment.newInstance()
        1 -> WorkoutsFragment.newInstance()
        2 -> SessionsFragment.newInstance()
        else -> throw IllegalStateException("Unexpected index $index")
    }

    override fun triggerBackButton() {
        onBackPressedDispatcher.onBackPressed()
    }


}
