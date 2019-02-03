package com.codility.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.codility.pedometer.listener.StepListener
import com.codility.pedometer.utils.Constant
import com.codility.pedometer.utils.SharedPref
import com.codility.pedometer.utils.StepDetector
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Govind on 05/25/2018.
 */
class MainActivity : AppCompatActivity(), SensorEventListener, StepListener {



    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private val TEXT_NUM_STEPS = "Number of Steps ="



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Get an instance of the SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)
        showCurrentValue()
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST)


    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector!!.updateAccelerometer(event.timestamp, event.values[0], event.values[1], event.values[2])
        }
    }

    override fun onStepAndDistance(timeNs: Long, distance: Float) {
        tvSteps.text = TEXT_NUM_STEPS.plus(timeNs) +" \nDistance ="+(distance/1000)+" km"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun showCurrentValue(){
        val steps = SharedPref.readLong(Constant.KEY_STEP_COUNT)
        var distance = SharedPref.readFloat(Constant.KEY_DISTANCE)
        tvSteps.text = TEXT_NUM_STEPS.plus(steps) +" \nDistance ="+(distance / 1000)+" km"
    }
}
