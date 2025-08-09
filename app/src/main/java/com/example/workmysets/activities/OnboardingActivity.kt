package com.example.workmysets.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.workmysets.R
import com.example.workmysets.data.entities.user.entity.User
import com.example.workmysets.data.viewmodels.OnboardingViewModel
import com.example.workmysets.data.viewmodels.UserViewModel
import com.example.workmysets.databinding.ActivityOnboardingBinding
import com.example.workmysets.fragments.onboarding.FinishFragment
import com.example.workmysets.fragments.onboarding.StatsFragment
import com.example.workmysets.fragments.onboarding.UserFragment
import com.example.workmysets.fragments.onboarding.WelcomeFragment

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding

    private val viewModel: OnboardingViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val pageList = mutableListOf<Fragment>()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.hasUsers().observe(this) { hasUsers ->
            if (hasUsers) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        pageList.add(WelcomeFragment.newInstance())
        pageList.add(UserFragment.newInstance())
        pageList.add(StatsFragment.newInstance())
        pageList.add(FinishFragment.newInstance())

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, pageList[0])
            .commit()

        onBackPressedDispatcher.addCallback(this) {
            if (currentIndex > 0) {
                back()
            }
        }
    }

    private fun showPage(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return

        val direction = toIndex - fromIndex

        val enterAnim = if (direction > 0) R.anim.slide_in_right else R.anim.slide_in_left
        val exitAnim = if (direction > 0) R.anim.slide_out_left else R.anim.slide_out_right

        val fragment = pageList[toIndex]

        supportFragmentManager.beginTransaction().setCustomAnimations(enterAnim, exitAnim)
            .replace(R.id.fragmentContainer, fragment).commit()
    }

    fun next() {
        if (currentIndex + 1 < pageList.size) {
            val fromIndex = currentIndex
            currentIndex++
            showPage(fromIndex, currentIndex)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun back() {
        if (currentIndex - 1 >= 0) {
            val fromIndex = currentIndex
            currentIndex--
            showPage(fromIndex, currentIndex)
        }
    }

}