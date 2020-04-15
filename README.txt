!!!
Streaming through a reactive type requires an Executor to write to the response.
Please, configure a TaskExecutor in the MVC config under "async support".
The SimpleAsyncTaskExecutor currently in use is not suitable under load.
-------------------------------
Controller:	com.yuta4.hat.controllers.GameController
Method:		notStartedEvents
Returning:	reactor.core.publisher.Flux<com.yuta4.hat.dto.JoinGameDto>
!!!