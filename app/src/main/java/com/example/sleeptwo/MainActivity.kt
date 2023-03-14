package com.example.sleeptwo

import android.app.Service
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize MediaPlayer with the MP3 file from the "raw" directory
        mediaPlayer = MediaPlayer.create(this, R.raw.compressed)
        mediaPlayer.isLooping = true

        // Set a completion listener to handle the end of the audio playback
        mediaPlayer.setOnCompletionListener {
            // Do nothing
        }

        // Find the Button widget in the layout and set a click listener on it
        val playButton: Button = findViewById(R.id.playButton)
        playButton.setBackgroundColor(0xFF555555.toInt())

        playButton.setOnClickListener {
            if (mediaPlayer != null && mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                // If the MediaPlayer is not playing, start the playback
                mediaPlayer.start()
            }
        }

        // Start the service
        val intent = Intent(this, MyService::class.java)
        startService(intent)

        // Schedule pausing the audio at 6AM
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val calendar = Calendar.getInstance()
                val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                if ((hourOfDay > 5 && minute >= 52) && hourOfDay < 6) {
                    mediaPlayer.pause()
                }
            }
        }, 0, 60 * 1000)
    }

    override fun onResume() {
        super.onResume()
        // Resume or pause the audio playback depending on whether the user has pressed the play button before
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
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
