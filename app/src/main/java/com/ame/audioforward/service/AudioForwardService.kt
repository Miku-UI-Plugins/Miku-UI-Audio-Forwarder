package com.ame.audioforward.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.ame.audioforward.R
import com.ame.audioforward.core.AudioForwardThread
import com.ame.audioforward.core.CHANNEL_ID
import com.ame.audioforward.core.CHANNEL_NAME
import com.ame.audioforward.core.KEY_IP

class AudioForwardService : Service() {
    companion object {
        private const val TAG = "AudioForwardService"
        private var afThread: AudioForwardThread? = null

        fun isRunning(): Boolean {
            if (afThread != null) {
                return afThread?.isAlive == true
            }
            return false
        }

        fun stopService() {
            afThread?.stopForwarding()
            afThread = null
        }
    }

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun init(ip: String) {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
        val notification =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentText(getString(R.string.tip_running))
                // Create the notification to display while the service is running
                .build()
        ServiceCompat.startForeground(
            this,
            100, // Could be any number
            notification,
            if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE
            ) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            },
        )
        afThread = AudioForwardThread(ip)
        afThread?.start()
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * [android.os.IBinder] is usually for a complex interface
     * that has been [described using
 * aidl]({@docRoot}guide/components/aidl.html).
     *
     *
     * *Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process*.  More information about the main thread can be found in
     * [Processes and
 * Threads]({@docRoot}guide/topics/fundamentals/processes-and-threads.html).
     *
     * @param intent The Intent that was used to bind to this service,
     * as given to [ Context.bindService][android.content.Context.bindService].  Note that any extras that were included with
     * the Intent at that point will *not* be seen here.
     *
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val ip = intent?.getStringExtra(KEY_IP)
        if (ip != null) {
            init(ip)
        }
        return START_NOT_STICKY
    }
}
