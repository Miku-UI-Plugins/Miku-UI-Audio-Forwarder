package com.ame.audioforward.core

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Audio forward thread, capturing audio data and sending it to the specified IP address.
 */
class AudioForwardThread(
    private val ip: String,
) : Thread() {
    private var audioRecord: AudioRecord? = null
    private var isRunning = false

    @SuppressLint("MissingPermission") // Permission is for the weak! We're system services!
    override fun run() {
        isRunning = true
        val socket = DatagramSocket()
        val address = InetAddress.getByName(ip)
        val port = 39392
        val recBufSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIGURATION, AUDIO_ENCODING) * 2
        audioRecord =
            AudioRecord(
                MediaRecorder.AudioSource.REMOTE_SUBMIX,
                SAMPLE_RATE,
                CHANNEL_CONFIGURATION,
                AUDIO_ENCODING,
                recBufSize,
            )
        val recBuf = ByteArray(recBufSize)
        audioRecord?.startRecording()

        try {
            while (isRunning) {
                // Loop to get audio data
                audioRecord?.read(recBuf, 0, recBufSize)

                val packet = DatagramPacket(recBuf, recBuf.size, address, port)
                socket.send(packet)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopForwarding() {
        isRunning = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        interrupt()
    }

    companion object {
        // Audio configurations
        const val SAMPLE_RATE: Int = 48000
        const val CHANNEL_CONFIGURATION: Int = AudioFormat.CHANNEL_CONFIGURATION_STEREO
        const val AUDIO_ENCODING: Int = AudioFormat.ENCODING_PCM_16BIT
    }
}
