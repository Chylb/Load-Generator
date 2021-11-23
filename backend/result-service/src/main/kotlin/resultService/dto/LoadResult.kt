package resultService.dto

class LoadResult(
    var key: Long = 0,
    var concurrentUsers: Int = 0,
    var averageResponseTime: Long = 0,
    var maxResponseTime: Long = 0,
    var state: String? = null,
    var errors: Set<String>? = null,
)
{
}