package resultService

import com.chylb.loadgenerator.common.Constants
import resultService.dto.LoadResult
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.inject.Inject


@Controller
class ResultController {
    @Inject
    var db: Db? = null

    @Get("/all")
    @Produces(MediaType.APPLICATION_JSON)
    fun all() = results().collect(Collectors.toList())

    @Get("/last")
    @Produces(MediaType.APPLICATION_JSON)
    fun getLast(@QueryValue count: Int): MutableList<LoadResult> {
        return results().limit(count.toLong()).collect(Collectors.toList())
    }

    private fun results(): Stream<LoadResult> {
            return db!!.getAll().stream().map { record: Db.Record -> toDto(record) }
                .sorted(Comparator.comparingLong{ res: LoadResult -> res.key }.reversed())
    }

    private fun toDto(record: Db.Record): LoadResult {
        var state = if (record.receivedParts == record.totalParts) "success" else "running"
        if (!record.errors.isEmpty()) state = "failed"
        return LoadResult(
            record.key,
            record.totalParts * Constants.CONCURRENT_USERS_PER_SLAVE,
            record.responseTimeSum / Constants.CONCURRENT_USERS_PER_SLAVE / record.loopCount / record.totalParts,
            record.maxResponseTime,
            state,
            record.errors as Set<String>?
        )
    }
}