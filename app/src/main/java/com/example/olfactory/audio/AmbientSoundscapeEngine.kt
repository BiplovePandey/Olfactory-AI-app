package com.example.olfactory.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

data class SoundscapeMeta(
    val id: String,
    val title: String,
    val description: String,
    val atmosphereKey: String,
    val icon: String
)

class AmbientSoundscapeEngine {

    companion object {
        val PRESETS = listOf(
            SoundscapeMeta(
                id = "none",
                title = "Silent Atelier",
                description = "Serene silence for focused olfactory analysis.",
                atmosphereKey = "default",
                icon = "🔇"
            ),
            SoundscapeMeta(
                id = "monsoon_deg",
                title = "Monsoon on Kannauj Degs",
                description = "Gentle raindrops falling on antique copper distillation vessels and wet alluvial earth.",
                atmosphereKey = "earthy",
                icon = "🌧️"
            ),
            SoundscapeMeta(
                id = "temple_breeze",
                title = "Evening Temple Breeze",
                description = "Gentle night wind rustling through sandalwood groves and sacred night-blooming jasmine.",
                atmosphereKey = "khus",
                icon = "🍃"
            ),
            SoundscapeMeta(
                id = "amber_hearth",
                title = "Atelier Amber Hearth",
                description = "Distant crackling charcoal embers warming copper hydro-distillation cauldrons.",
                atmosphereKey = "oud",
                icon = "🔥"
            )
        )
    }

    private val _activeSoundscape = MutableStateFlow("none")
    val activeSoundscape: StateFlow<String> = _activeSoundscape.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun toggleSoundscape(presetId: String? = null) {
        val target = presetId ?: if (_activeSoundscape.value != "none") _activeSoundscape.value else "monsoon_deg"
        if (_isPlaying.value && (presetId == null || _activeSoundscape.value == presetId)) {
            stop()
        } else {
            start(target)
        }
    }

    fun start(presetId: String) {
        if (presetId == "none") {
            stop()
            _activeSoundscape.value = "none"
            return
        }

        stop()
        _activeSoundscape.value = presetId
        _isPlaying.value = true

        playbackJob = scope.launch {
            runAudioLoop(presetId)
        }
    }

    fun stop() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        try {
            audioTrack?.setVolume(clamped)
        } catch (_: Exception) {}
    }

    private suspend fun runAudioLoop(presetId: String) {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)

        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.setVolume(_volume.value)
            track.play()

            val buffer = ShortArray(bufferSize / 2)
            var phase1 = 0.0
            var phase2 = 0.0
            var lpFilter = 0.0

            while (kotlinx.coroutines.currentCoroutineContext().isActive && _isPlaying.value) {
                for (i in buffer.indices) {
                    val sample: Double = when (presetId) {
                        "monsoon_deg" -> {
                            // Pink noise with soft low-pass filter (rain) + intermittent copper droplet ping
                            val white = Random.nextDouble(-1.0, 1.0)
                            lpFilter = lpFilter * 0.94 + white * 0.06
                            val rain = lpFilter * 0.75
                            // Rare droplet ping (high resonant tone decaying)
                            val drop = if (Random.nextInt(12000) == 0) sin(phase1 * 12.0) * 0.4 else 0.0
                            phase1 += (2.0 * PI * 440.0) / sampleRate
                            (rain + drop) * 0.6
                        }
                        "temple_breeze" -> {
                            // Sandalwood temple breeze: dual pure harmonic sinusoidal drone with slight modulation
                            phase1 += (2.0 * PI * 146.83) / sampleRate // D3 note
                            phase2 += (2.0 * PI * 220.0) / sampleRate  // A3 note
                            val drone = (sin(phase1) * 0.5 + sin(phase2) * 0.3)
                            val breath = Random.nextDouble(-0.1, 0.1)
                            (drone + breath) * 0.35
                        }
                        "amber_hearth" -> {
                            // Warm crackle: low-frequency rumble + occasional crackle spike
                            val rumbleWhite = Random.nextDouble(-0.8, 0.8)
                            lpFilter = lpFilter * 0.98 + rumbleWhite * 0.02
                            val crackle = if (Random.nextInt(4000) == 0) Random.nextDouble(0.5, 1.0) else 0.0
                            (lpFilter * 0.8 + crackle * 0.3) * 0.45
                        }
                        else -> 0.0
                    }

                    val intSample = (sample * 32767.0).toInt().coerceIn(-32768, 32767)
                    buffer[i] = intSample.toShort()
                }

                track.write(buffer, 0, buffer.size)
            }
        } catch (_: Exception) {
            // Audio hardware fallback handling
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (_: Exception) {}
            audioTrack = null
        }
    }
}
