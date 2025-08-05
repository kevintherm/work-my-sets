package com.example.workmysets.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.workmysets.R
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.databinding.ActivitySessionDetailBinding
import com.example.workmysets.utils.AppLocale
import com.example.workmysets.utils.Consts
import com.example.workmysets.utils.ErrorToast
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SessionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySessionDetailBinding
    private lateinit var sessionViewModel: SessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySessionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!intent.hasExtra(Consts.ARG_SESSION_ID)) {
            ErrorToast(this, "Session not found!").show()
        }

        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class]

        sessionViewModel.allSessions.observe(this) { sessions ->
            val find = sessions.find {
                it.session.sessionId == intent.getLongExtra(
                    Consts.ARG_SESSION_ID,
                    -1
                )
            }
            if (find == null) {
                ErrorToast(this, "Session not found!").show()
            }

            if (find != null) {
                binding.topBar.titleText.text = find.exercise.name

                binding.topBar.backButton.visibility = View.VISIBLE
                binding.topBar.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
                binding.topBar.actionButton.visibility = View.VISIBLE
                binding.topBar.actionButton.isClickable = false
                binding.topBar.actionButton.isFocusable = false
                binding.topBar.actionButton.setImageDrawable(getDrawable(R.drawable.ic_session))

                binding.totalSets.text = find.session.repsPerSet.size.toString()

                val combinedDetails = find.session.repsPerSet.zip(find.session.weightsPerSet).joinToString("\n") {
                    "${it.first} Rep(s) using ${it.second} Kg"
                }

                binding.setDetails.text = combinedDetails

                binding.avgRest.text =
                    find.session.restsPerSet.takeIf { it.isNotEmpty() }?.average()
                        ?.let { "$it min" } ?: "0.0 min"

                val start = Instant.parse(find.session.startsAt)
                val end = Instant.parse(find.session.endsAt)
                val duration = Duration.between(start, end)
                binding.duration.text = duration.toMinutes().toDouble().toString().plus(" min")

                binding.notes.text = find.session.notes

                val formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy hh:mm a", Locale.US)
                val startsAt = Instant.parse(find.session.startsAt).atZone(AppLocale.ZONE).toLocalDateTime().format(formatter)
                val endsAt = Instant.parse(find.session.endsAt).atZone(AppLocale.ZONE).toLocalDateTime().format(formatter)

                binding.startsAt.text = "Starts at $startsAt"
                binding.endsAt.text = "Ends at $endsAt"

            }

        }

    }
}