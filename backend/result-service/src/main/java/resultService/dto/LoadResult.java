package resultService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Setter
@Getter
public class LoadResult {
    private final long key;
    private final long concurrentUsers;
    private final long averageResponseTime;
    private final long maxResponseTime;
    private final String state;
    private final Set<String> errors;
}
