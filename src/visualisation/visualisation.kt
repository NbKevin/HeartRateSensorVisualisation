/**
 * Created by Nb on 6/12/2015.
 * Main applet of the visualisation of heart rate.
 */

package visualisation

import processing.core.*
import visualisation.yun.*

/**
 * Main class for the visualisation of heart rate.
 */
class HeartRateVisualisation : PApplet() {
    /**
     * Colours.
     */
    var RASPBERRY: Int = 0
    var CRANBERRY: Int = 0
    var WHITESMOKE: Int = 0

    /**
     * Fonts.
     */
    lateinit var HEART_RATE_FONT: PFont
    lateinit var HINT_FONT: PFont

    /**
     * Loading data circle angle.
     */
    private var loadingCircleAngle = 0f

    /**
     * Timer.
     */
    private var lastRequested = millis()

    /**
     * Heart rate.
     */
    private var heartRate: HeartRate? = null

    /**
     * Initialisation.
     */
    init {
        RASPBERRY = color(217, 106, 106)
        CRANBERRY = color(158, 67, 81)
        WHITESMOKE = color(228, 232, 235)
    }

    /**
     * Constants.
     */
    companion object {
        /**
         * Window size.
         */
        val WIDTH = 720
        val HEIGHT = 720

        /**
         * Frame rate.
         */
        val FRAME_RATE = 60f

        /**
         * Font settings.
         */
        val HEART_RATE_FONT_NAME = "DIN Light"
        val HEART_RATE_FONT_SIZE = 200f
        val HINT_FONT_NAME = "DIN"
        val HINT_FONT_SIZE = 35f

        /**
         * Timer interval, in milliseconds.
         */
        val REQUEST_INTERVAL = 1000

        /**
         * Waiting circle radius.
         */
        val LOADING_CIRCLE_RADIUS = 175f
        val LOADING_CIRCLE_DIAMETER = LOADING_CIRCLE_RADIUS * 2
        val LOADING_CIRCLE_ANGLE_PERIOD = 3 // in seconds
        val LOADING_CIRCLE_ANGLE_STEP = PI / (FRAME_RATE * LOADING_CIRCLE_ANGLE_PERIOD)
        val LOADING_CIRCLE_STROKE_WEIGHT = 10f
        val LOADING_CIRCLE_GAP_RADIUS = 150f
    }

    /**
     * Tell whether it is time to fire a new request.
     */
    private val timeForNewRequest: Boolean
        get() = millis() - lastRequested > REQUEST_INTERVAL

    /**
     * Shortcut for processing a request if necessary.
     */
    private fun processRequestIfNecessary(processFunction: () -> Unit) {
        if (timeForNewRequest) {
            processFunction.invoke()
            lastRequested = millis()
        }
    }

    /**
     * Shortcut for painting loading circle.
     */
    private fun paintLoadingCircleIfNecessary(paintFunction: () -> Unit) {
        if (heartRate?.state != HeartRateState.REPORTING_DATA) {
            paintFunction.invoke()
            if (heartRate?.state == HeartRateState.COLLECTING_DATA || heartRate == null) {
                loadingCircleAngle += LOADING_CIRCLE_ANGLE_STEP
            }
        }
    }

    /**
     * Shortcut for painting heart rate.
     */
    private fun paintHeartRateIfNecessary(paintFunction: () -> Unit) {
        if (heartRate?.state == HeartRateState.REPORTING_DATA) {
            paintFunction.invoke()
        }
    }

    /**
     * Paint the circle animation while loading data.
     */
    private fun paintLoadingCircle() {
        // circle first
        strokeWeight(LOADING_CIRCLE_STROKE_WEIGHT)
        stroke(WHITESMOKE)
        noFill()
        ellipse(WIDTH / 2f, HEIGHT / 2.5f, LOADING_CIRCLE_DIAMETER, LOADING_CIRCLE_DIAMETER)

        // gap then
        noStroke()
        fill(RASPBERRY)
        ellipse(WIDTH / 2f + LOADING_CIRCLE_RADIUS * sin(loadingCircleAngle),
                HEIGHT / 2.5f - LOADING_CIRCLE_RADIUS * cos(loadingCircleAngle),
                LOADING_CIRCLE_GAP_RADIUS, LOADING_CIRCLE_GAP_RADIUS)
    }

    /**
     * Paint heart rate.
     */
    private fun paintHeartRate() {
        fill(WHITESMOKE)
        textFont(HEART_RATE_FONT)
        textAlign(CENTER, CENTER)
        text("${heartRate?.heartRate}", WIDTH / 2f, HEIGHT / 2.5f)
    }

    private fun paintHint() {
        var hint = ""
        when (heartRate?.state) {
            HeartRateState.SOURCE_ABSENT -> hint = "please put the sensor on"
            HeartRateState.COLLECTING_DATA -> hint = "collecting data"
            HeartRateState.REPORTING_DATA -> hint = "heart rate"
            null -> hint = "connecting to the sensor"
        }
        fill(WHITESMOKE)
        textFont(HINT_FONT)
        textAlign(CENTER, CENTER)
        text("$hint", WIDTH / 2f, HEIGHT / 1.25f)
    }

    /**
     * No-PDE start up settings.
     */
    override fun settings() {
        size(WIDTH, HEIGHT, P2D)
    }

    /**
     * Setup environment.
     */
    override fun setup() {
        colorMode(RGB)
        background(RASPBERRY)
        frameRate(FRAME_RATE)
        HEART_RATE_FONT = createFont(HEART_RATE_FONT_NAME, HEART_RATE_FONT_SIZE)
        HINT_FONT = createFont(HINT_FONT_NAME, HINT_FONT_SIZE)
        lastRequested = millis()
    }

    /**
     * Main loop.
     */
    override fun draw() {
        background(RASPBERRY)
        processRequestIfNecessary {
            try {
                heartRate = HeartRateDataSource.get_heart_rate()
            } catch (e: DataSourceException) {
                e.printStackTrace()
                throw e
            }
        }
        paintLoadingCircleIfNecessary {
            paintLoadingCircle()
        }
        paintHeartRateIfNecessary {
            paintHeartRate()
        }
        paintHint()
    }

    /**
     * Clean up environment.
     */
    override fun stop() {
        terminateAllConnections()
        println("Window closed")
    }
}

/**
 * Main entry point of the applet.
 */
fun main(args: Array<String>) {
    PApplet.main(arrayOf("visualisation.HeartRateVisualisation"))
}
