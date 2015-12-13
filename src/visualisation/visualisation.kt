package visualisation

/**
 * Created by Nb on 6/12/2015.
 * Main applet of the visualisation of heart rate.
 */

import processing.core.*
import visualisation.yun.*

/**
 * Main class for the visualisation of heart rate.
 * It extends the PApplet for the Processing launcher
 * to find it.
 */
class HeartRateVisualisation : PApplet() {
    /**
     * Colours.
     */
    var RASPBERRY: Int = 0
    var CRANBERRY: Int = 0
    var WHITESMOKE: Int = 0
    var AZURE: Int = 0
    var CLOVE: Int = 0

    /**
     * Arduino adapter.
     */
    // lateinit var adapter: Adapter

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
    private var previousHeartRate: HeartRate? = null

    /**
     * Initialisation.
     */
    init {
        AZURE = color(132, 177, 224)
        RASPBERRY = color(217, 106, 106)
        CRANBERRY = color(158, 67, 81)
        WHITESMOKE = color(228, 232, 235)
        CLOVE = color(249, 142, 178)
    }

    /**
     * Constants.
     */
    companion object {
        /**
         * Window size.
         */
        val WIDTH = Config.width
        val HEIGHT = Config.height

        /**
         * Frame rate.
         */
        val FRAME_RATE = Config.frameRate

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
        val REQUEST_INTERVAL = Config.requestInterval

        /**
         * Waiting circle radius.
         */
        val LOADING_CIRCLE_RADIUS = Config.loadingCircleRadius
        val LOADING_CIRCLE_DIAMETER = LOADING_CIRCLE_RADIUS * 2
        val LOADING_CIRCLE_PERIOD = Config.loadingCirclePeriod // in seconds
        val LOADING_CIRCLE_ANGLE_STEP = PI / (FRAME_RATE * LOADING_CIRCLE_PERIOD)
        val LOADING_CIRCLE_STROKE_WEIGHT = Config.loadingCircleStrokeWeight
        val LOADING_CIRCLE_GAP_RADIUS = Config.loadingCircleGapRadius
    }

    /**
     * Tell whether it is time to fire a new request.
     */
    private val isTimeForNewRequest: Boolean
        get() = millis() - lastRequested > REQUEST_INTERVAL

    /**
     * Shortcut for processing a request if necessary.
     */
    private fun ifNecessaryToRequestHeartRate(processFunction: () -> Unit) {
        if (isTimeForNewRequest) {
            processFunction.invoke()
            lastRequested = millis()
        }
    }

    /**
     * Shortcut for painting loading circle.
     */
    private fun ifNecessaryToPaintLoadingCircle(paintFunction: () -> Unit) {
        if (heartRate?.state != HeartRateState.REPORTING_DATA) {
            paintFunction.invoke()
            if (heartRate?.state == HeartRateState.COLLECTING_DATA || heartRate?.state == HeartRateState.SENSOR_ABSENT || heartRate == null) {
                loadingCircleAngle += LOADING_CIRCLE_ANGLE_STEP
            }
        }
    }

    /**
     * Shortcut for painting heart rate.
     */
    private fun ifNecessaryToPaintHeartRate(paintFunction: () -> Unit) {
        if (heartRate?.state == HeartRateState.REPORTING_DATA) {
            paintFunction.invoke()
        }
    }

    private fun ifNecessaryToFeed(feedFunction: () -> Unit) {
        if (previousHeartRate?.state != heartRate?.state) {
            feedFunction.invoke()
        }
        previousHeartRate = heartRate
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

    /**
     * Paint hint.
     */
    private fun paintHint() {
        var hint = ""
        when (heartRate?.state) {
            HeartRateState.SOURCE_ABSENT -> hint = "please put the sensor on"
            HeartRateState.COLLECTING_DATA -> hint = "measuring"
            HeartRateState.REPORTING_DATA -> hint = "heart rate"
            HeartRateState.SENSOR_ABSENT -> hint = "discovering sensor"
            null -> hint = "discovering server"
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
        ifNecessaryToPaintLoadingCircle {
            paintLoadingCircle()
        }
        ifNecessaryToPaintHeartRate {
            paintHeartRate()
        }
        ifNecessaryToRequestHeartRate {
            HeartRateDataSource.get_heart_rate(
                    onSuccess = { rawResponse ->
                        heartRate = HeartRate(rawResponse)
                    },
                    onError = { exception ->
                        println(exception.message)
                        heartRate = null
                    }
            )
        }
        ifNecessaryToFeed {
            // adapter.feed(heartRate)
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
