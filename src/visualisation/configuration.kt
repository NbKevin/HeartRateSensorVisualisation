package visualisation

/**
 * Created by Nb on 9/12/2015.
 * Configurations.
 */

import java.io.FileInputStream
import java.util.Properties


class Configuration {
    companion object {
        /**
         * Path to XML configurations.
         */
        internal val CONFIGURATION_PATH = "cfg/configuration.xml"
    }

    /**
     * Properties.
     */
    internal val properties: Properties

    /**
     * Load properties from XML configurations.
     */
    init {
        this.properties = Properties()
        this.properties.loadFromXML(FileInputStream(CONFIGURATION_PATH))
    }

    /**
     * Shortcuts.
     */
    public val width: Int
        get() = this.properties.getProperty("width").toInt()
    public val height: Int
        get() = this.properties.getProperty("height").toInt()
    public val frameRate: Float
        get() = this.properties.getProperty("frameRate").toFloat()
    public val requestInterval: Int
        get() = this.properties.getProperty("requestInterval").toInt()
    public val loadingCircleRadius: Float
        get() = this.properties.getProperty("loadingCircleRadius").toFloat()
    public val loadingCirclePeriod: Int
        get() = this.properties.getProperty("loadingCirclePeriod").toInt()
    public val loadingCircleStrokeWeight: Float
        get() = this.properties.getProperty("loadingCircleStrokeWeight").toFloat()
    public val loadingCircleGapRadius: Float
        get() = this.properties.getProperty("loadingCircleGapRadius").toFloat()
    public val intermediateServerHost: String
        get() = this.properties.getProperty("intermediateServerHost").toString()
    public val intermediateServerPort: String
        get() = this.properties.getProperty("intermediateServerPort").toString()
}


internal val Config = Configuration()
