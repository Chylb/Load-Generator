package resultService

import com.chylb.loadgenerator.common.LoadRequestMessage
import com.chylb.loadgenerator.common.LoadResultMessage
import javax.inject.Singleton

@Singleton
class Db {
    private val records: MutableMap<String?, Record> = HashMap()

    fun add(resultMsg: LoadResultMessage) {
        var record = records[resultMsg.requestKey]
        if (record == null) {
            record = Record()
        }
        record.key = resultMsg.requestKey!!.toLong()
        record.responseTimeSum += resultMsg.responseTimeSum
        record.maxResponseTime = Math.max(record.maxResponseTime, resultMsg.maxResponseTime)
        record.receivedParts++
        if (resultMsg.error != null) {
            record.errors.add(resultMsg.error)
        }
        records[resultMsg.requestKey] = record
    }

    fun add(key: String, requestMsg: LoadRequestMessage){
        var record = records[key]
        if (record == null) {
            record = Record()
        }
        record.key = key.toLong()
        record.totalParts = requestMsg.totalParts
        record.loopCount = requestMsg.loopCount
        records[key] = record
    }

    fun getAll() = records.values

    class Record {
        var key: Long = 0
        var responseTimeSum: Long = 0
        var maxResponseTime: Long = 0
        var receivedParts = 0
        var totalParts = 1
        var loopCount = 1
        var errors: MutableSet<String?> = LinkedHashSet()
    }
}