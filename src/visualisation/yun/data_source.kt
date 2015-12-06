package visualisation.yun

/**
 * Created by Nb on 6/12/2015.
 * Data source for Arduino Yun.
 */

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import org.apache.http.HttpStatus
import org.json.JSONObject


/**
 * Data source host.
 */
internal val DATA_SOURCE_HOST = "http://localhost:7000"


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
        internal val REQUIRED_FIELDS = listOf("state", "micro_period_rate", "report_period_rate")
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
    public val microPeriodRate: Int
        get() = rawJSON.getInt("micro_period_rate")
    public val reportPeriodRate: Int
        get() = rawJSON.getInt("report_period_rate")

    /**
     * Heart rate.
     */
    public val heartRate: Int?
        get() = if (state == HeartRateState.REPORTING_DATA) this.reportPeriodRate else null

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
        internal val HEART_RATE_REQUEST_URL_EXTENSION = "/api/heartrate/"

        /**
         * The heart rate data request url.
         */
        internal val HEART_RATE_REQUEST_URL: String
            get() = DATA_SOURCE_HOST + HEART_RATE_REQUEST_URL_EXTENSION

        /**
         * Get heart rate data.
         */
        public fun get_heart_rate(): HeartRate {
            val response: HttpResponse<JsonNode>
            try {
                response = Unirest.get(HEART_RATE_REQUEST_URL).asJson()
            } catch (e: UnirestException) {
                e.printStackTrace()
                throw DataSourceMissing("Cannot retrieve data from heart rate API")
            }
            return HeartRate(response)
        }
    }
}
