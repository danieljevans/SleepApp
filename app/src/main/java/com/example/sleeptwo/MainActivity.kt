package com.example.sleeptwo
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.core.app.NotificationCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioManager: AudioManager

    companion object {
        const val REQUEST_WRITE_SETTINGS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize MediaPlayer with the MP3 file from the "raw" directory
        mediaPlayer = MediaPlayer.create(this, R.raw.compressed)
        mediaPlayer.isLooping = true

        // Set a prepared listener to handle the proper initialization of the MediaPlayer
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }

        // Find the Button widget in the layout and set a click listener on it
        val playButton: Button = findViewById(R.id.playButton)
        playButton.setBackgroundColor(0xFF555555.toInt())

        playButton.setOnClickListener {
            if (mediaPlayer != null && mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
            } else {
                // If the MediaPlayer is not playing, start the playback
                mediaPlayer.start()
            }
        }

        // Get the AudioManager instance
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Set the volume level to 5/15 of the maximum volume
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume * 4/15, 0)

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
                if (hourOfDay == 5 && minute == 54) {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                }
            }
        }, 0, 5 * 1000)
    }

    override fun onResume() {
        super.onResume()
        // Resume or pause the audio playback depending on whether the user has pressed the play button before
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        // Set the volume level to 5/15 of the maximum volume
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume * 6/15, 0)
    }

    override fun onBackPressed() {
        // Do nothing
    }

    private fun setScreenBrightness(brightness: Float) {
        val window = window
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness
        window.attributes = layoutParams
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