package com.example.sleeptwo

import android.app.Service
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Button
import androidx.core.app.NotificationCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private val stopHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize MediaPlayer with the MP3 file from the "raw" directory
        mediaPlayer = MediaPlayer.create(this, R.raw.output)
        mediaPlayer.isLooping = true

        // Find the Button widget in the layout and set a click listener on it
        val playButton: Button = findViewById(R.id.playButton)
        playButton.setBackgroundColor(0xFF555555.toInt())

        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            } else {
                // If the MediaPlayer is not playing, start the playback
                mediaPlayer.start()
                // Schedule stopping the playback after 8 hours
                stopHandler.postDelayed({
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                    }
                }, 8 * 60 * 60 * 1000) // 8 hours in milliseconds
            }
        }

        // Start the service
        val intent = Intent(this, MyService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer when the activity is destroyed to free up system resources
        mediaPlayer.release()
        // Remove any pending stop commands
        stopHandler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed() {
        // Do nothing
    }

}


class MyService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the service in the foreground and show a notification
        val notification = NotificationCompat.Builder(this, "my_channel_id")
            .setContentTitle("My App")
            .setContentText("Playing audio")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
        startForeground(1, notification)

        // Return START_STICKY to indicate that the service should be restarted if it's killed by the system
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Not used
        return null
    }
}
