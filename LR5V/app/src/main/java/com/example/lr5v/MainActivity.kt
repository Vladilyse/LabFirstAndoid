package com.example.lr5v

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var tvSensorStatus: TextView
    private lateinit var tvX: TextView
    private lateinit var tvY: TextView
    private lateinit var tvZ: TextView
    private lateinit var tvAcceleration: TextView
    private lateinit var tvGForce: TextView
    private lateinit var tvPeakValue: TextView
    private lateinit var tvMotionStatus: TextView

    private lateinit var progressGForce: ProgressBar

    private var peakGForce = 0.0f

    companion object {
        private const val GRAVITY_EARTH = 9.81f
        private const val KEY_PEAK_G_FORCE = "key_peak_g_force"
        private const val KEY_SENSOR_STATUS = "key_sensor_status"
        private const val KEY_X_TEXT = "key_x_text"
        private const val KEY_Y_TEXT = "key_y_text"
        private const val KEY_Z_TEXT = "key_z_text"
        private const val KEY_ACCEL_TEXT = "key_accel_text"
        private const val KEY_GFORCE_TEXT = "key_gforce_text"
        private const val KEY_PEAK_TEXT = "key_peak_text"
        private const val KEY_MOTION_TEXT = "key_motion_text"
        private const val KEY_PROGRESS = "key_progress"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            tvSensorStatus.text = "Accelerometer not available on this device"
            tvMotionStatus.text = "Unavailable"
        } else {
            tvSensorStatus.text = "Accelerometer is available"
        }

        if (savedInstanceState != null) {
            peakGForce = savedInstanceState.getFloat(KEY_PEAK_G_FORCE, 0.0f)
            tvSensorStatus.text = savedInstanceState.getString(KEY_SENSOR_STATUS, tvSensorStatus.text.toString())
            tvX.text = savedInstanceState.getString(KEY_X_TEXT, "X: 0.00")
            tvY.text = savedInstanceState.getString(KEY_Y_TEXT, "Y: 0.00")
            tvZ.text = savedInstanceState.getString(KEY_Z_TEXT, "Z: 0.00")
            tvAcceleration.text = savedInstanceState.getString(KEY_ACCEL_TEXT, "Acceleration: 0.00 m/s²")
            tvGForce.text = savedInstanceState.getString(KEY_GFORCE_TEXT, "G-Force: 0.00 g")
            tvPeakValue.text = savedInstanceState.getString(KEY_PEAK_TEXT, "Peak: 0.00 g")
            tvMotionStatus.text = savedInstanceState.getString(KEY_MOTION_TEXT, "Waiting for data...")
            progressGForce.progress = savedInstanceState.getInt(KEY_PROGRESS, 0)
        }
    }

    private fun initViews() {
        tvSensorStatus = findViewById(R.id.tvSensorStatus)
        tvX = findViewById(R.id.tvX)
        tvY = findViewById(R.id.tvY)
        tvZ = findViewById(R.id.tvZ)
        tvAcceleration = findViewById(R.id.tvAcceleration)
        tvGForce = findViewById(R.id.tvGForce)
        tvPeakValue = findViewById(R.id.tvPeakValue)
        tvMotionStatus = findViewById(R.id.tvMotionStatus)
        progressGForce = findViewById(R.id.progressGForce)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val gForce = acceleration / GRAVITY_EARTH

        if (gForce > peakGForce) {
            peakGForce = gForce
        }

        tvX.text = "X: %.2f".format(x)
        tvY.text = "Y: %.2f".format(y)
        tvZ.text = "Z: %.2f".format(z)

        tvAcceleration.text = "Acceleration: %.2f m/s²".format(acceleration)
        tvGForce.text = "G-Force: %.2f g".format(gForce)
        tvPeakValue.text = "Peak: %.2f g".format(peakGForce)

        val progressValue = (gForce * 100).toInt().coerceIn(0, 400)
        progressGForce.progress = progressValue

        tvMotionStatus.text = when {
            gForce < 1.05f -> "Stable"
            gForce < 1.30f -> "Moderate motion"
            gForce < 2.00f -> "Strong motion"
            else -> "Extreme motion"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putFloat(KEY_PEAK_G_FORCE, peakGForce)
        outState.putString(KEY_SENSOR_STATUS, tvSensorStatus.text.toString())
        outState.putString(KEY_X_TEXT, tvX.text.toString())
        outState.putString(KEY_Y_TEXT, tvY.text.toString())
        outState.putString(KEY_Z_TEXT, tvZ.text.toString())
        outState.putString(KEY_ACCEL_TEXT, tvAcceleration.text.toString())
        outState.putString(KEY_GFORCE_TEXT, tvGForce.text.toString())
        outState.putString(KEY_PEAK_TEXT, tvPeakValue.text.toString())
        outState.putString(KEY_MOTION_TEXT, tvMotionStatus.text.toString())
        outState.putInt(KEY_PROGRESS, progressGForce.progress)
    }
}