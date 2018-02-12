package info.benjaminhill.waller

import lejos.hardware.port.Port
import lejos.hardware.port.SensorPort

/** Extend the @see Gyro to handle PID calibration  */
class PIDGyro(gyroPort: Port = SensorPort.S1, private var kd: Double = 0.0, var kp: Double = 0.0, private var ki: Double = 0.0) : Gyro(gyroPort) {
    private var cumulativeError = 0.0
    private var lastTs = System.currentTimeMillis()

    val kpMaxAtCurrentAngle: Double
        get() {
            refreshSample()
            val errorAngle = (0 - sample[Gyro.ANGLE]).toDouble()
            return 1 / errorAngle
        }

    /**
     * Kp * errorAngle = big boost towards goal
     * Ki * cumulativeError = long term end up at goal, instead of falling short a constant offset
     * Kd * ANGE_RATE = don't overshoot/oscillate, allows a bigger Kp
     */
    val correction: Double
        get() {
            refreshSample()
            val newTime = System.currentTimeMillis()
            val timeStep = newTime - lastTs
            lastTs = newTime

            val errorAngle = (0 - sample[Gyro.ANGLE]).toDouble()
            cumulativeError += timeStep * errorAngle

            return (kp * errorAngle
                    + ki * cumulativeError
                    + kd * sample[ANGLE_RATE])
        }

}
