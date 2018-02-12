package info.benjaminhill.waller

import lejos.hardware.motor.UnregulatedMotor
import lejos.hardware.port.MotorPort

import lejos.hardware.port.Port
import lejos.utility.Delay

/** Right = A, Left = D  */
class RobotWheels(wheelLeftPort: Port = MotorPort.A, wheelRightPort: Port = MotorPort.D) : AutoCloseable {

    private val wheelLeft = UnregulatedMotor(wheelLeftPort)
    private val wheelRight = UnregulatedMotor(wheelRightPort)
    private val startTacho = wheelRight.tachoCount // "home"

    override fun close() {
        wheelLeft.close()
        wheelRight.close()
    }

    /** Free floating wheels */
    fun flt() {
        wheelLeft.flt()
        wheelRight.flt()
    }

    fun returnHomeBlocking() {
        println("Homing: start")
        while (wheelRight.tachoCount > startTacho) {
            setBoth(-.4)
            Delay.msDelay(50)
            println("Homing: backup, tc:${wheelRight.tachoCount}, goal:$startTacho")
        }
        flt()
        while (wheelRight.tachoCount < startTacho) {
            setBoth(.4)
            Delay.msDelay(50)
            println("Homing: forward, tc:${wheelRight.tachoCount}, goal:$startTacho")
        }
        flt()
        Delay.msDelay(250)
        println("Homing: homed")
    }

    fun setBoth(pct: Double) {
        val power = Math.min(100, Math.abs(100.0 * pct).toInt())
        wheelLeft.power = power
        wheelRight.power = power
        if (pct >= 0.0) {
            wheelLeft.forward()
            wheelRight.forward()
        } else {
            wheelLeft.backward()
            wheelRight.backward()
        }
    }

    fun stop() {
        wheelLeft.stop()
        wheelRight.stop()
    }
}
