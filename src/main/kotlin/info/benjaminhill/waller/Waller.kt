package info.benjaminhill.waller

import lejos.hardware.Brick
import lejos.hardware.BrickFinder
import lejos.hardware.lcd.GraphicsLCD
import lejos.hardware.port.Port
import lejos.hardware.port.SensorPort
import lejos.utility.Delay
import lejos.utility.Stopwatch

/** Basic robot class, handles PID gyro and 2 wheels */
class Waller(gyroPort: Port = SensorPort.S1) : Runnable, AutoCloseable {

    private val brick: Brick = BrickFinder.getDefault()
    private val graphics: GraphicsLCD = brick.graphicsLCD
    private val gyro: PIDGyro = PIDGyro(gyroPort)
    private val wheels: RobotWheels = RobotWheels()

    private val angleSamples = mutableMapOf<Int, Double>()

    init {
        graphics.drawString(javaClass.simpleName, 0, 0,
                GraphicsLCD.VCENTER or GraphicsLCD.LEFT)
        println("Waller ready!")
    }

    override fun close() {
        gyro.close()
    }

    /** Increase the power until you flip forwards  */
    private fun wobbleToFindCenter(): Double {
        println("wobbleToFindCenter")
        var power = .2
        while (power <= 1.1) {
            var sign = -1
            while (sign <= 1) {
                println("Trying power: ${sign * power}")
                gyro.refreshSample()
                val startAngle = gyro.sample[Gyro.ANGLE]
                wheels.setBoth((sign * power))
                Delay.msDelay(400)
                wheels.setBoth((-sign * power)) // ka... flop.
                Delay.msDelay(400)
                wheels.flt() // no crashy
                Delay.msDelay(400)
                gyro.refreshSample()
                val endAngle = gyro.sample[Gyro.ANGLE]
                val diff = endAngle - startAngle
                if (Math.abs(diff) > 20) {
                    return ((startAngle + endAngle) / 2).toDouble()
                }
                wheels.returnHomeBlocking()
                sign += 2
            }
            power += .2f
        }
        throw IllegalStateException("Couldn't locate a center point")
    }

    override fun run() {
        val stopwatch = Stopwatch()
        val centerAngle = wobbleToFindCenter()
        println("Guessing at Center Angle: $centerAngle")
        gyro.startAngle = -centerAngle

        println("Calculated kpMax: ${gyro.kpMaxAtCurrentAngle}")

        buildSequenceDouble(0.0, gyro.kpMaxAtCurrentAngle * 2.0, 10).forEach { nextKp ->
            gyro.clear()
            gyro.kp = nextKp
            System.out.format("Trial: Kp:%.0f\tAngle:%.0f\tangleRate:%.3f\tCorrection:%.3f%n", gyro.kp, gyro.sample[Gyro.ANGLE], gyro.sample[Gyro.ANGLE_RATE],
                    gyro.correction)
            stopwatch.reset()

            do {
                val elapsed = stopwatch.elapsed()
                val correction = gyro.correction
                wheels.setBoth(gyro.correction)
                angleSamples[elapsed] = correction
            } while (elapsed < 5000)

            wheels.returnHomeBlocking()
            println("Samples: ${gyro.sampleCount}, sps: ${gyro.samplesPerSecond}")
        }
    }
}