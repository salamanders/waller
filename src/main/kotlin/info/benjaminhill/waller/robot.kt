/**
 * Example Lejos EV3 Robot Application
 */
package info.benjaminhill.waller

/**
 * @author Jörn Franke <jornfranke></jornfranke>@gmail.com>
 */
import lejos.hardware.BrickFinder
import lejos.hardware.Button
import lejos.hardware.Sound
import lejos.hardware.lcd.Font
import lejos.hardware.lcd.GraphicsLCD
import lejos.utility.Delay

fun main(args: Array<String>) {
    println("Starting app")
    val g = BrickFinder.getDefault().graphicsLCD!!
    val screenWidth = g.width
    val screenHeight = g.height
    Button.LEDPattern(4)
    Sound.beepSequenceUp()
    g.font = Font.getDefaultFont()
    g.drawString("Lejos EV3 Gradle", screenWidth / 2, screenHeight / 2, GraphicsLCD.BASELINE or GraphicsLCD.HCENTER)
    Button.LEDPattern(3)
    Delay.msDelay(4000)
    Button.LEDPattern(5)
    g.clear()
    g.refresh()
    Sound.beepSequence()
    Delay.msDelay(500)
    Button.LEDPattern(0)

    val waller = Waller()
    waller.run()
}
