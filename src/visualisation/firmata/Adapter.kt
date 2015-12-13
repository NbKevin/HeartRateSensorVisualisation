package visualisation.firmata

/**
 * Created by Nb on 10/12/2015.
 * Firmata adapter.
 */

import processing.core.*
import cc.arduino.Arduino
import visualisation.yun.HeartRate
import visualisation.yun.HeartRateState

/**
 * Arduino adapter.
 * Intended for the interaction between the heart rate sensor and the physical representation
 * mechanism, not working yet.
 */
class Adapter(internal val applet: PApplet, COMName: String? = null, internal val pin: Int = 1) {
    /**
     * Arduino instance.
     */
    internal val arduino: Arduino

    /**
     * Initialise the arduino.
     */
    init {
        this.arduino = Arduino(this.applet, COMName ?: Arduino.list()[0], 57600)
        this.arduino.pinMode(pin, Arduino.OUTPUT)
    }

    /**
     * Feed data to the arduino.
     */
    public fun feed(heartRate: HeartRate?) {
        if (heartRate?.state == HeartRateState.REPORTING_DATA) {
            this.arduino.digitalWrite(pin, Arduino.HIGH)
        }
    }
}
