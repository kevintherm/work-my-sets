package com.example.workmysets.activities

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.workmysets.R
import com.example.workmysets.data.entities.exercise.entity.Exercise
import com.example.workmysets.data.entities.session.entity.Session
import com.example.workmysets.data.entities.workout.entity.WorkoutWithExercises
import com.example.workmysets.data.objects.TimestampPair
import com.example.workmysets.data.viewmodels.ExerciseViewModel
import com.example.workmysets.data.viewmodels.SessionViewModel
import com.example.workmysets.data.viewmodels.WorkoutViewModel
import com.example.workmysets.databinding.ActivitySessionExerciseBinding
import com.example.workmysets.ui.components.SessionExerciseStep
import com.example.workmysets.utils.Consts
import com.example.workmysets.utils.ErrorToast
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener
import java.time.Instant

class SessionExerciseActivity : AppCompatActivity() {
    private lateinit var b: ActivitySessionExerciseBinding

    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var workoutViewModel: WorkoutViewModel
    private lateinit var exerciseViewModel: ExerciseViewModel

    private lateinit var workoutWithExercises: WorkoutWithExercises
    private lateinit var exercise: Exercise

    private lateinit var session: Session
    private var setCount = 1
    private val setsTimestamp: MutableList<TimestampPair> = mutableListOf()
    private val repsPerSet: MutableList<Int> = mutableListOf()
    private val weightsPerSet: MutableList<Float> = mutableListOf()
    private val restPerSet: MutableList<Int> = mutableListOf()

    private val CURRENT_STEP = MutableLiveData(SessionExerciseStep.START_SET)
    private var isTimerRunning = false
    private var pauseOffset = 0L

    private fun View.fadeIn(duration: Long = 300L, onComplete: (() -> Unit)? = null) {
        this.visibility = View.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            this.duration = duration
            setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation) {
                    onComplete?.invoke()
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
            })
        }
        this.startAnimation(fadeIn)
    }

    private fun View.fadeOut(duration: Long = 300L, onComplete: (() -> Unit)? = null) {
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            this.duration = duration
            setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation) {
                    this@fadeOut.visibility = View.GONE
                    onComplete?.invoke()
                }

                override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
            })
        }
        this.startAnimation(fadeOut)
    }

    private fun CircularProgressIndicator.rotateAndBack(
        duration: Long = 600L,
        onComplete: (() -> Unit)? = null
    ) {
        val originalRotation = this.rotation
        val isClockwise = (0..1).random() == 0
        val delta = if (isClockwise) 360f else -360f

        val rotateForward =
            ObjectAnimator.ofFloat(this, View.ROTATION, originalRotation, originalRotation + delta)
                .apply {
                    this.duration = duration
                    interpolator = AccelerateDecelerateInterpolator()
                }

        val rotateBack =
            ObjectAnimator.ofFloat(this, View.ROTATION, originalRotation + delta, originalRotation)
                .apply {
                    this.duration = duration
                    interpolator = AccelerateDecelerateInterpolator()
                }

        AnimatorSet().apply {
            playSequentially(rotateForward, rotateBack)

            if (onComplete != null) {
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        onComplete()
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
            }

            start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        b.guideVideoWebView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        b.guideVideoWebView.restoreState(savedInstanceState)
    }

    private inner class CustomChromeClient : WebChromeClient() {
        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        private var mOriginalOrientation = 0
        private var mOriginalSystemUiVisibility = 0
        override fun getDefaultVideoPoster(): Bitmap? {
            return if (mCustomView == null) {
                null
            } else BitmapFactory.decodeResource(applicationContext.resources, 2130837573)
        }

        override fun onHideCustomView() {
            (window.decorView as FrameLayout).removeView(mCustomView)
            mCustomView = null
            window.decorView.systemUiVisibility = mOriginalSystemUiVisibility
            requestedOrientation = mOriginalOrientation
            mCustomViewCallback!!.onCustomViewHidden()
            mCustomViewCallback = null
        }

        override fun onShowCustomView(
            paramView: View?,
            paramCustomViewCallback: CustomViewCallback?
        ) {
            if (mCustomView != null) {
                onHideCustomView()
                return
            }
            mCustomView = paramView
            mOriginalSystemUiVisibility = window.decorView.systemUiVisibility
            mOriginalOrientation = requestedOrientation
            mCustomViewCallback = paramCustomViewCallback
            (window.decorView as FrameLayout).addView(mCustomView, FrameLayout.LayoutParams(-1, -1))
            window.decorView.systemUiVisibility = 3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivitySessionExerciseBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionViewModel = ViewModelProvider(this)[SessionViewModel::class]
        workoutViewModel = ViewModelProvider(this)[WorkoutViewModel::class]
        exerciseViewModel = ViewModelProvider(this)[ExerciseViewModel::class]

        val workoutId = intent.getLongExtra(Consts.ARG_WORKOUT_ID, -1)
        val exerciseId = intent.getLongExtra(Consts.ARG_EXERCISE_ID, -1)

        if (workoutId == -1L || exerciseId == -1L) {
            Toast.makeText(this, "Invalid workout or exercise.", Toast.LENGTH_LONG).show()
            finish()
        }

        workoutViewModel.findById(workoutId).observe(this) { workoutWithExercises = it }

        exerciseViewModel.findById(exerciseId).observe(this) {
            exercise = it
            b.topBar.titleText.text = exercise.name
            if (it.youtubeVideoId != null && it.youtubeVideoId!!.isNotEmpty() && savedInstanceState == null) {
                b.guideVideoWebView.webViewClient = WebViewClient()
                b.guideVideoWebView.webChromeClient = CustomChromeClient()
                val webSettings = b.guideVideoWebView.settings
                webSettings.javaScriptEnabled = true
                webSettings.allowFileAccess = true
                b.guideVideoWebView.settings.blockNetworkLoads = false
                b.guideVideoWebView.loadUrl("https://www.youtube.com/embed/${it.youtubeVideoId}")
                b.guideCard.fadeIn()
            }
        }

        session = Session(workoutId, exerciseId)
        session.startsAt = Instant.now().toString()

        CURRENT_STEP.observe(this, this::onLifecycleChange)

        onBackPressedDispatcher.addCallback {
            if (isTimerRunning && CURRENT_STEP.value != SessionExerciseStep.START_REST) handleToggleTimerButton()

            PopupDialog.getInstance(this@SessionExerciseActivity)
                .standardDialogBuilder()
                .createStandardDialog()
                .setHeading("Cancel session?")
                .setDescription("Are you sure you want to cancel this session? Your progress will be lost.")
                .setIcon(R.drawable.ic_question)
                .setIconColor(R.color.primary)
                .setCancelable(true)
                .setNegativeButtonText("No")
                .setPositiveButtonText("Yes, Cancel")
                .setPositiveButtonBackgroundColor(R.color.primary)
                .setPositiveButtonTextColor(R.color.white)
                .build(object : StandardDialogActionListener {
                    override fun onPositiveButtonClicked(dialog: Dialog) {
                        dialog.dismiss()
                        finish() // exit the activity
                    }

                    override fun onNegativeButtonClicked(dialog: Dialog) {
                        dialog.dismiss() // do nothing
                    }
                }).show()
        }
    }

    private fun handleToggleTimerButton() {
        if (isTimerRunning) {
            b.toggleTimerButton.isEnabled = false
            b.stopTimerButton.isEnabled = false

            isTimerRunning = false
            b.chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - b.chronometer.base
            b.toggleTimerButton.setImageResource(R.drawable.ic_pause_fill)

            b.circularProgressBar.rotateAndBack {
                b.toggleTimerButton.isEnabled = true
                b.stopTimerButton.isEnabled = true
            }
        } else {
            isTimerRunning = true
            b.chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            b.chronometer.start()
            b.toggleTimerButton.setImageResource(R.drawable.ic_play_fill)
            b.stopTimerButton.fadeIn(0)
            b.actionCardSubtext.text = "Waiting..."
            if (pauseOffset <= 0 && CURRENT_STEP.value == SessionExerciseStep.START_SET) {
                if (setsTimestamp.getOrNull(setCount - 1) == null) {
                    setsTimestamp.add(
                        TimestampPair(
                            Instant.now().toString(),
                            Instant.now().toString()
                        )
                    )
                }
            }
        }
    }

    private fun handleStopTimerButton() {
        b.chronometer.stop()
        b.chronometer.base = SystemClock.elapsedRealtime()
        b.toggleTimerButton.setImageResource(R.drawable.ic_play_fill)
        pauseOffset = 0

        if (setsTimestamp.getOrNull(setCount - 1) != null && CURRENT_STEP.value == SessionExerciseStep.START_SET) {
            setsTimestamp.getOrNull(setCount - 1)?.end = Instant.now().toString()
            b.circularProgressBar.rotateAndBack()
        }

        nextStep()
        isTimerRunning = false
    }

    private fun SessionExerciseStep.next(): SessionExerciseStep {
        val steps = SessionExerciseStep.values()
        val nextOrdinal = (this.ordinal + 1).coerceAtMost(steps.lastIndex)
        return steps[nextOrdinal]
    }

    private fun nextStep() {
        CURRENT_STEP.postValue(CURRENT_STEP.value.next())
    }

    private fun onLifecycleChange(step: SessionExerciseStep) {
//        Toast.makeText(this, step.toString(), Toast.LENGTH_SHORT).show()
        when (step) {
            SessionExerciseStep.START_SET -> {
                b.bottomButtonsCard.fadeOut(0)

                b.actionCardTitle.text = "Set $setCount"
                b.actionCardSubtext.text = "Start set $setCount of exercise"
                b.timerContainer.fadeIn()
                b.toggleTimerButton.fadeIn()

                b.chronometer.text = "Start"
                b.toggleTimerButton.setOnClickListener { handleToggleTimerButton() }
                b.stopTimerButton.setOnClickListener { handleStopTimerButton() }
                b.chronometer.setOnChronometerTickListener { }
            }

            SessionExerciseStep.TRACK_REPS -> {
                b.timerContainer.fadeOut()
                b.numberPickerContainer.fadeIn()
                b.continueButton.fadeIn()

                b.numberPickerText.text = "How many reps?"

                val min = 1
                val max = 15
                val step = 1

                val values = generateSequence(min) { it + step }
                    .takeWhile { it <= max }
                    .map { it.toString() }
                    .toList()
                    .toTypedArray()

                if (values.isNotEmpty()) {
                    b.numberPicker.maxValue = values.size - 1
                    b.numberPicker.isEnabled = true
                } else {
                    b.numberPicker.maxValue = 0
                    b.numberPicker.isEnabled = false
                }

                b.numberPicker.minValue = 0
                b.numberPicker.displayedValues = values

                b.continueButton.setOnClickListener {
                    while (repsPerSet.size < setCount) {
                        repsPerSet.add(0)
                    }
                    repsPerSet[setCount - 1] = values.getOrNull(b.numberPicker.value)?.toInt() ?: 1
                    nextStep()
                }
            }

            SessionExerciseStep.TRACK_WEIGHTS -> {
                b.numberPickerText.text = "How heavy? (KG)"

                val min = 5.0
                val max = 50.0
                val step = 2.5

                val values = generateSequence(min) { it + step }
                    .takeWhile { it <= max }
                    .map { String.format("%.1f", it) }
                    .toList()
                    .toTypedArray()

                b.numberPicker.minValue = 0
                b.numberPicker.displayedValues = values

                if (values.isNotEmpty()) {
                    b.numberPicker.maxValue = values.size - 1
                    b.numberPicker.isEnabled = true
                } else {
                    b.numberPicker.maxValue = 0
                    b.numberPicker.isEnabled = false
                }

                b.continueButton.setOnClickListener {
                    while (weightsPerSet.size < setCount) {
                        weightsPerSet.add(0f)
                    }
                    val numIndex = b.numberPicker.value
                    weightsPerSet[setCount - 1] = (values.getOrNull(numIndex)
                        ?.toFloat() ?: 1F)
                    nextStep()
                }
            }

            SessionExerciseStep.PROMPT_CONTINUE -> {
                b.numberPicker.fadeOut()
                b.continueButton.fadeOut()
                b.numberPickerText.text = "Add another set?"
                b.bottomButtonsCard.fadeIn()

                b.noteCard.fadeIn()

                val currentIndex =
                    workoutWithExercises.exercises.indexOfFirst { it.exerciseId == exercise.exerciseId }
                val nextExercise = workoutWithExercises.exercises.getOrNull(currentIndex + 1)

                if (nextExercise != null) {
                    b.finishExerciseButton.text = "Next Exercise"
                } else {
                    b.finishExerciseButton.text = "Finish Session"
                }

                b.finishExerciseButton.setOnClickListener {
                    CURRENT_STEP.postValue(SessionExerciseStep.FINISH)
                }

                b.continueButton.setOnClickListener {}
                b.anotherSetButton.setOnClickListener {
                    setCount++
                    nextStep()
                }
            }

            SessionExerciseStep.START_REST -> {
                b.numberPicker.fadeIn()
                b.continueButton.fadeIn()
                b.bottomButtonsCard.fadeOut()

                b.noteCard.fadeOut()

                b.numberPickerText.text = "Choose rest time (s)"
                b.actionCardTitle.text = "Next set: $setCount"

                val min = 0
                val max = 300
                val step = 15

                val values = generateSequence(min) { it + step }
                    .takeWhile { it <= max }
                    .map { it.toString() }
                    .toList()
                    .toTypedArray()

                b.numberPicker.minValue = 0
                b.numberPicker.displayedValues = values

                if (values.isNotEmpty()) {
                    b.numberPicker.maxValue = values.size - 1
                    b.numberPicker.isEnabled = true
                } else {
                    b.numberPicker.maxValue = 0
                    b.numberPicker.isEnabled = false
                }

                b.continueButton.setOnClickListener {

                    b.actionCardTitle.text = "Rest! Next set: $setCount"
                    b.numberPickerContainer.fadeOut()
                    b.timerContainer.fadeIn()

                    b.chronometer.text = "Rest"
                    if (!isTimerRunning) handleToggleTimerButton()

                    b.continueButton.fadeOut()
                    b.toggleTimerButton.fadeOut()
                    b.stopTimerButton.fadeOut()

                    val numIndex = b.numberPicker.value
                    val endTime = SystemClock.elapsedRealtime() + (values.getOrNull(numIndex)
                        ?.toLong() ?: 1L) * 1000
                    b.chronometer.base = endTime

                    while (restPerSet.size < setCount) {
                        restPerSet.add(0)
                    }

                    restPerSet[setCount - 1] = values.getOrNull(numIndex)
                        ?.toInt() ?: 1

                    b.chronometer.setOnChronometerTickListener {
                        val timeLeft = b.chronometer.base - SystemClock.elapsedRealtime()
                        if (timeLeft <= 0) {
                            b.chronometer.stop()
                            b.chronometer.text = "00:00"
                            isTimerRunning = false

                            CURRENT_STEP.postValue(SessionExerciseStep.START_SET)
                        } else {
                            val seconds = (timeLeft / 1000).toInt()
                            val minutes = seconds / 60
                            val remainingSeconds = seconds % 60
                            b.chronometer.text =
                                String.format("%02d:%02d", minutes, remainingSeconds)
                        }
                    }
                    b.chronometer.start()
                    isTimerRunning = true
                }
            }

            SessionExerciseStep.FINISH -> {
                session.endsAt = Instant.now().toString()
                session.repsPerSet = repsPerSet
                session.weightsPerSet = weightsPerSet
                session.setsTimestamp = setsTimestamp
                session.restsPerSet = restPerSet
                session.notes = b.noteInput.text.toString()
                session.isCompleted = true
                sessionViewModel.insert(session)

                val currentIndex =
                    workoutWithExercises.exercises.indexOfFirst { it.exerciseId == exercise.exerciseId }
                val nextExercise = workoutWithExercises.exercises.getOrNull(currentIndex + 1)
                if (nextExercise != null) {
                    PopupDialog.getInstance(this)
                        .standardDialogBuilder()
                        .createStandardDialog()
                        .setHeading("Next exercise?")
                        .setDescription(getString(R.string.start_exercise, nextExercise.name))
                        .setIcon(R.drawable.ic_question)
                        .setIconColor(R.color.primary)
                        .setCancelable(false)
                        .setNegativeButtonText("Finish")
                        .setNegativeButtonCornerRadius(16F)
                        .setPositiveButtonCornerRadius(16F)
                        .setPositiveButtonBackgroundColor(R.color.primary)
                        .setPositiveButtonTextColor(R.color.white)
                        .setPositiveButtonText(getString(R.string.confirm))
                        .build(object : StandardDialogActionListener {
                            override fun onPositiveButtonClicked(dialog: Dialog) {
                                Intent(
                                    this@SessionExerciseActivity,
                                    SessionExerciseActivity::class.java
                                ).apply {
                                    putExtra(
                                        Consts.ARG_EXERCISE_ID,
                                        nextExercise.exerciseId
                                    )
                                    putExtra(
                                        Consts.ARG_WORKOUT_ID,
                                        workoutWithExercises.workout.workoutId
                                    )
                                }.also {
                                    startActivity(it)
                                    finish()
                                }
                                dialog.dismiss()
                            }

                            override fun onNegativeButtonClicked(dialog: Dialog) {
                                Intent(
                                    this@SessionExerciseActivity,
                                    TrackSessionActivity::class.java
                                ).also {
                                    startActivity(it)
                                    finish()
                                }
                                dialog.dismiss()
                            }
                        }).show()
                } else {
                    Intent(
                        this@SessionExerciseActivity,
                        TrackSessionActivity::class.java
                    ).also {
                        startActivity(it)
                        finish()
                    }
                }
            }
        }
    }


}