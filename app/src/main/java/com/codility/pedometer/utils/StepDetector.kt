package com.codility.pedometer.utils

import android.content.Context
import android.util.Log
import com.codility.pedometer.listener.StepListener
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Govind on 05/25/2018.
 */
class StepDetector {

    private val ACCEL_RING_SIZE = 50
    private val VEL_RING_SIZE = 10

    // change this threshold according to your sensitivity preferences
    private val STEP_THRESHOLD = 50f

    private val STEP_DELAY_NS = 250000000

    private var accelRingCounter = 0
    private val accelRingX = FloatArray(ACCEL_RING_SIZE)
    private val accelRingY = FloatArray(ACCEL_RING_SIZE)
    private val accelRingZ = FloatArray(ACCEL_RING_SIZE)
    private var velRingCounter = 0
    private val velRing = FloatArray(VEL_RING_SIZE)
    private var lastStepTimeNs: Long = 0
    private var oldVelocityEstimate = 0f
    val STEP2METERS = 0.715f
    private var stepsCount: Long = 0
    private var listener: StepListener? = null
    var dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var context : Context

    constructor(context : Context){
       this.context =  context;
    }

    fun registerListener(listener: StepListener) {
        this.listener = listener
    }

    fun updateAccelerometer(timeNs: Long, x: Float, y: Float, z: Float) {
        val currentAccel = FloatArray(3)
        currentAccel[0] = x
        currentAccel[1] = y
        currentAccel[2] = z

        // First onStepAndDistance is to update our guess of where the global z vector is.
        accelRingCounter++
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0]
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1]
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2]

        val worldZ = FloatArray(3)
        worldZ[0] = SensorFilter().sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE)
        worldZ[1] = SensorFilter().sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE)
        worldZ[2] = SensorFilter().sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE)

        val normalization_factor = SensorFilter().norm(worldZ)

        worldZ[0] = worldZ[0] / normalization_factor
        worldZ[1] = worldZ[1] / normalization_factor
        worldZ[2] = worldZ[2] / normalization_factor

        val currentZ = SensorFilter().dot(worldZ, currentAccel) - normalization_factor
        velRingCounter++
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ

        val velocityEstimate = SensorFilter().sum(velRing)
        //Log.e("Cnesor_event", "changeed  here =" + timeNs)


        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD
                && timeNs - lastStepTimeNs > STEP_DELAY_NS) {


            var todayStepCount = SharedPref.on(context).readLong(Constant.KEY_STEP_COUNT)
            var date = SharedPref.on(context).read(Constant.KEY_DATE)

            var todayDateString = dateFormat.format(Date(System.currentTimeMillis()))

            if (date.equals(todayDateString)) {

                todayStepCount = todayStepCount + 1L
            } else {
                todayStepCount = 1L
            }
            SharedPref.on(context).write(Constant.KEY_STEP_COUNT, todayStepCount)

            SharedPref.on(context).write(Constant.KEY_DATE, todayDateString)

            val distance = todayStepCount * STEP2METERS //Distance in meter

            SharedPref.on(context).write(Constant.KEY_DISTANCE,distance)

            listener!!.onStepAndDistance(todayStepCount, distance)

            lastStepTimeNs = timeNs
        }
        oldVelocityEstimate = velocityEstimate
    }
}