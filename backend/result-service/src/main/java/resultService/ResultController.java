package resultService;

import com.ociet.loadgenerator.common.Constants;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;
import resultService.dto.LoadResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ResultController {
    @Inject
    Db db;

    @Get("/all")
    @Produces(MediaType.APPLICATION_JSON)
    List<LoadResult> getAll() {
        return results().collect(Collectors.toList());
    }

    @Get("/last")
    @Produces(MediaType.APPLICATION_JSON)
    List<LoadResult> getLast(@QueryValue int count) {
        return results().limit(count).collect(Collectors.toList());
    }

    private Stream<LoadResult> results() {
        return db.getAll().stream().map(this::toDto).sorted(Comparator.comparingLong(LoadResult::getKey).reversed());
    }

    private LoadResult toDto(Db.Record record) {
        String state = record.getReceivedParts() == record.getTotalParts() ? "success" : "running";
        if (!record.getErrors().isEmpty()) state = "failed";

        return new LoadResult(
                record.getKey(),
                record.getTotalParts() * Constants.CONCURRENT_USERS_PER_SLAVE,
                record.getResponseTimeSum() / Constants.CONCURRENT_USERS_PER_SLAVE / record.getLoopCount() / record.getTotalParts(),
                record.getMaxResponseTime(),
                state,
                record.getErrors());
    }
}
