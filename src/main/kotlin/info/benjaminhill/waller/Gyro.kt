package info.benjaminhill.waller

import lejos.hardware.port.Port
import lejos.hardware.port.SensorPort
import lejos.hardware.sensor.EV3GyroSensor
import lejos.robotics.SampleProvider

/** Wrap the gyro sensor to get an Angle on-demand */
open class Gyro(gyroPort: Port = SensorPort.S1) : AutoCloseable {
    val sample: FloatArray
    var sampleCount = 0L
    private val sampleProviderGyro: SampleProvider
    private val sensorGyro = EV3GyroSensor(gyroPort)
    var startAngle = 0.0 // Correction for start leaning vs vertical
    private var startTimeMs = 0L

    /**
     * @return since last clear()
     */
    val samplesPerSecond: Double
        get() = sampleCount / ((System.currentTimeMillis() - startTimeMs) / 1000.0)

    init {
        sensorGyro.currentMode = 2 // angle and angleSpeed
        sampleProviderGyro = sensorGyro.angleAndRateMode
        sample = FloatArray(sampleProviderGyro.sampleSize())
        println("Gyro calibrating")
        sensorGyro.reset()
        clear()
    }

    fun clear() {
        sampleCount = 0
        startTimeMs = System.currentTimeMillis()
    }

    override fun close() {
        sensorGyro.close()
        clear()
    }

    /** 0=Angle, 1=AngleRate  */
    fun refreshSample() {
        sampleProviderGyro.fetchSample(sample, 0)
        sample[ANGLE] += startAngle.toFloat()
        sampleCount++
    }

    companion object {
        const val ANGLE = 0
        const val ANGLE_RATE = 1 // not used
    }

}
