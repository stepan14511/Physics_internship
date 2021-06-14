package com.example.summerinternship

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mySensorManager: SensorManager? = null
    private var mLinearAcceleration: Sensor? = null
    private var y_axis_view: TextView? = null
    private var z_axis_view: TextView? = null
    private var globalTimerSensors: Date = Date()
    private var accelerationVector: Vector<Float> = Vector()
    private var isExperimentRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Making view portrait only
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // I use them for showing instant data if needed
        y_axis_view = findViewById(R.id.y_axis)
        z_axis_view = findViewById(R.id.z_axis)

        mySensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLinearAcceleration = mySensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        // Set on click listeners for the two buttons of the experiment
        val startButton = findViewById<Button>(R.id.startbutton)
        startButton.setOnClickListener{
            // Global variable just for knowing if experiment is running
            isExperimentRunning = true
            // Clearing vector of experiment data
            accelerationVector.removeAllElements()
            // Register the sensor in the system
            mySensorManager?.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL)
        }
        val endButton = findViewById<Button>(R.id.endbutton)
        endButton.setOnClickListener {
            // Stopping experiment
            isExperimentRunning = false
            // Removing load from the sensor
            mySensorManager?.unregisterListener(this)
            // Printing result of the experiment into logs
            Log.d("EXPERIMENTS RESULTS", accelerationVector.toString())
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        /*if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mGravityArray = event.values
        }
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD){
            mGeomagneticArray = event.values
        }
        if (mGravityArray != null && mGeomagneticArray != null) {
            var R = FloatArray(9)
            var I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, mGravityArray, mGeomagneticArray)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                y_axis_view?.text = Math.toDegrees(orientation[1].toDouble()).toInt().toString()
                z_axis_view?.text = Math.toDegrees(orientation[2].toDouble()).toInt().toString()
            }
        }*/
        // If the data came from right sensor and an experiment is running, then do the code below
        if ((event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) && isExperimentRunning) {
            val currentTime: Date = Date() // Get current data
            if (currentTime.time - globalTimerSensors.time > 100) { // If more than 100ms passed from last data write, calculate new value
                globalTimerSensors = currentTime // Update time of last experiment to the new one
                // Find vector length of the acceleration
                var normalized = sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2])
                // Save data
                accelerationVector.addElement(normalized)
                // Print it to the screen, just in case
                y_axis_view?.text = normalized.toString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        print("Accuracy changed")
    }

    override fun onResume() {
        super.onResume()
        // Registering sensor only if experiment is running
        if(isExperimentRunning) {
            mySensorManager?.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Remove listeners from sensors to save energy
        mySensorManager?.unregisterListener(this)
    }
}