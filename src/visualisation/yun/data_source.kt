package visualisation.yun

/**
 * Created by Nb on 6/12/2015.
 * Data source for Arduino Yun.
 */

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.async.Callback
import com.mashape.unirest.http.exceptions.UnirestException
import org.apache.http.HttpStatus
import org.json.JSONObject
import visualisation.Config


/**
 * Data source host.
 */
internal val DATA_SOURCE_HOST = "http://" + Config.intermediateServerHost + ":" + Config.intermediateServerPort + "/"


/**
 * Terminate all existing connections.
 */
public fun terminateAllConnections() {
    Unirest.shutdown()
}


/**
 * Data source.
 */
open class DataSource {
}

/**
 * Data source exceptions.
 */
abstract class DataSourceException(message: String) : Exception(message) {}

class DataSourceMissing(message: String) : DataSourceException(message) {}
class DataSourceNotReturningValidData(message: String) : DataSourceException(message) {}


/**
 * Heart rate states.
 */
class HeartRateState {
    companion object {
        val SOURCE_ABSENT = 0
        val COLLECTING_DATA = 1
        val REPORTING_DATA = 2
        val SENSOR_ABSENT = -1
    }
}


/**
 * Heart rate.
 */
class HeartRate(internal val rawResponse: HttpResponse<JsonNode>) {
    /**
     * Initialise and validate incoming raw response.
     */
    init {
        if (rawResponse.status != HttpStatus.SC_OK)
            throw DataSourceNotReturningValidData("Data source returned a response of code ${rawResponse.status}")
        val result = validateJSON()
        if (!result.first)
            throw DataSourceNotReturningValidData("Following field is not present: ${result.second}")
    }

    companion object {
        internal val REQUIRED_FIELDS = listOf("state", "hr")
    }

    /**
     * Validate incoming JSON string.
     */
    private fun validateJSON(): Pair<Boolean, String> {
        if (rawResponse.body.isArray) return Pair(false, "root")
        for (field in REQUIRED_FIELDS) {
            if (!rawResponse.body.`object`.has(field)) return Pair(false, field)
        }
        return Pair(true, "")
    }

    /**
     * Raw JSON.
     */
    internal val rawJSON: JSONObject
        get() = rawResponse.body.`object`

    /**
     * Field shortcuts.
     */
    public val state: Int
        get() = rawJSON.getInt("state")
    public val rawHeartRate: Int
        get() = rawJSON.getInt("hr")

    /**
     * Heart rate.
     */
    public val heartRate: Int?
        get() = if (state == HeartRateState.REPORTING_DATA) this.rawHeartRate else null

    public override fun toString(): String {
        return "HeartRateData -> $heartRate"
    }
}


/**
 * Heart rate data source.
 */
class HeartRateDataSource : DataSource() {
    companion object {
        /**
         * Extension part of the heart rate data request url.
         */
        internal val HEART_RATE_EXTENSION_URL = "/api/heartrate/"

        /**
         * The heart rate data request url.
         */
        internal val HEART_RATE_REQUEST_URL: String
            get() = DATA_SOURCE_HOST + HEART_RATE_EXTENSION_URL

        /**
         * Get heart rate data.
         */
        public fun get_heart_rate(onSuccess: (HttpResponse<JsonNode>) -> Unit, onError: (UnirestException) -> Unit) {
            Unirest.get(HEART_RATE_REQUEST_URL).asJsonAsync(object : Callback<JsonNode> {
                override fun completed(p0: HttpResponse<JsonNode>?) = onSuccess.invoke(p0!!)
                override fun failed(p0: UnirestException?) = onError.invoke(p0!!)
                override fun cancelled() = Unit
            })
        }
    }
}
