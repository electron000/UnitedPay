package com.unitedpay.feature.soundbox

import android.content.Context
import android.speech.tts.TextToSpeech
import com.unitedpay.core.common.extensions.toInrCurrency
import java.util.Locale

enum class SoundboxLanguage {
    HINDI,
    ENGLISH,
    ASSAMESE,
    BENGALI
}

/**
 * Enterprise Virtual Soundbox Engine.
 * Plays high-clarity voice confirmations on the merchant/user device when payments arrive.
 */
class SoundboxEngine(
    private val context: Context,
    private val preferredLanguage: SoundboxLanguage = SoundboxLanguage.HINDI
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                configureLocale(preferredLanguage)
            }
        }
    }

    private fun configureLocale(language: SoundboxLanguage) {
        val locale = when (language) {
            SoundboxLanguage.HINDI -> Locale("hi", "IN")
            SoundboxLanguage.ENGLISH -> Locale("en", "IN")
            SoundboxLanguage.ASSAMESE -> Locale("as", "IN")
            SoundboxLanguage.BENGALI -> Locale("bn", "IN")
        }
        tts?.language = locale
    }

    /**
     * Announces received payment with brand signature.
     */
    fun announcePaymentReceived(amount: Double, payerName: String? = null) {
        if (!isInitialized) return

        val announcement = when (preferredLanguage) {
            SoundboxLanguage.HINDI -> {
                "United Pay par ${amount.toInt()} rupaye prapt hue"
            }
            SoundboxLanguage.ENGLISH -> {
                "Received ${amount.toInrCurrency(false)} on United Pay"
            }
            SoundboxLanguage.ASSAMESE -> {
                "United Pay t ${amount.toInt()} toka prapto hol"
            }
            SoundboxLanguage.BENGALI -> {
                "United Pay-te ${amount.toInt()} taka joma hoyechhe"
            }
        }

        tts?.speak(announcement, TextToSpeech.QUEUE_FLUSH, null, "SoundboxTxn_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
