package com.inspection.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.inspection.R

class VoiceNoteBottomSheet : BottomSheetDialogFragment() {

    private var onSaveCallback: ((String) -> Unit)? = null
    private var recognizer: SpeechRecognizer? = null

    companion object {
        fun show(fm: FragmentManager, onSave: (String) -> Unit) {
            val sheet = VoiceNoteBottomSheet()
            sheet.onSaveCallback = onSave
            sheet.show(fm, "VoiceNoteBottomSheet")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_voice_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvSpeechText = view.findViewById<TextView>(R.id.tvSpeechText)
        val btnHold = view.findViewById<LottieAnimationView>(R.id.btnHoldToTalk)
        val lottieWave = view.findViewById<LottieAnimationView>(R.id.lottieWave)
        val listeningText = view.findViewById<TextView>(R.id.listeningText)
        val saveBtn = view.findViewById<Button>(R.id.dialogSaveBtn)
        val closeBtn = view.findViewById<ImageView>(R.id.dialogCloseBtn)

        closeBtn.setOnClickListener { dismiss() }

        saveBtn.setOnClickListener {
            onSaveCallback?.invoke(tvSpeechText.text.toString())
            dismiss()
        }

        saveBtn.isEnabled = false

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        recognizer = speechRecognizer

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
        }

        var isHolding = false
        var isRestarting = false
        var accumulatedText = ""

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                listeningText.visibility = View.GONE
            }

            override fun onRmsChanged(rmsdB: Float) {
                val scale = 1f + (rmsdB / 12f).coerceIn(0f, 2f)
                lottieWave.scaleX = scale
                lottieWave.scaleY = scale
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val text = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!text.isNullOrEmpty()) {
                    tvSpeechText.text = accumulatedText + text
                    saveBtn.isEnabled = tvSpeechText.text.toString().isNotEmpty()
                }
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (!text.isNullOrEmpty()) {
                    accumulatedText += "$text "
                    tvSpeechText.text = accumulatedText
                }
                if (isHolding) {
                    restartListening()
                } else {
                    stopUI()
                }
            }

            override fun onError(error: Int) {
                if (isHolding) restartListening() else stopUI()
            }

            override fun onEndOfSpeech() {}
            override fun onBeginningOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            private fun restartListening() {
                if (isRestarting) return
                isRestarting = true
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        speechRecognizer.startListening(intent)
                    } catch (_: Exception) {}
                    isRestarting = false
                }, 150)
            }

            fun stopUI() {
                lottieWave.pauseAnimation()
                lottieWave.visibility = View.GONE
                listeningText.visibility = View.GONE
            }
        })

        btnHold.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isHolding = true
                    accumulatedText = ""
                    tvSpeechText.text = ""
                    saveBtn.isEnabled = false
                    vibrate(40)
                    btnHold.playAnimation()
                    speechRecognizer.startListening(intent)
                }
                MotionEvent.ACTION_UP -> {
                    isHolding = false
                    btnHold.pauseAnimation()
                    speechRecognizer.stopListening()
                }
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recognizer?.destroy()
        recognizer = null
    }

    private fun vibrate(duration: Long = 40) {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }
}
