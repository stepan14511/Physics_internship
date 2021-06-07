package com.example.summerinternship

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mySensorManager: SensorManager? = null
    private var mGravity: Sensor? = null
    private var mGeomagnetic: Sensor? = null
    private var y_axis_view: TextView? = null
    private var z_axis_view: TextView? = null
    private var mGravityArray: FloatArray? = null
    private var mGeomagneticArray: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        y_axis_view = findViewById(R.id.y_axis)
        z_axis_view = findViewById(R.id.z_axis)

        mySensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGravity = mySensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGeomagnetic = mySensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
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
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        print("Accuracy changed")
    }

    override fun onResume() {
        super.onResume()
        mySensorManager?.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL)
        mySensorManager?.registerListener(this, mGeomagnetic, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mySensorManager?.unregisterListener(this)
    }
}