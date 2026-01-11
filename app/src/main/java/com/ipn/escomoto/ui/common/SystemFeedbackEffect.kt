package com.ipn.escomoto.ui.common

import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.media.MediaPlayer
import android.media.ToneGenerator
import com.ipn.escomoto.R

@Composable
fun SystemFeedbackEffect(isSuccess: Boolean) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // SONIDO
        try {
            if (isSuccess) {
                val mediaPlayer = MediaPlayer.create(context, R.raw.success_sound)
                mediaPlayer.setOnCompletionListener { mp ->
                    mp.release()
                }
                mediaPlayer.start()
            } else {
                val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val ringtone = RingtoneManager.getRingtone(context, notificationUri)
                ringtone.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // VIBRACIÓN
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= 31) {
                val vibratorManager = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (isSuccess) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(200)
                }
            } else {
                val pattern = longArrayOf(0, 100, 100, 500)
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}