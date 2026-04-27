package webtours;

import static io.gatling.javaapi.core.CoreDsl.*;

import java.time.Duration;
import io.gatling.javaapi.core.*;

public class Debug extends Simulation {
  
  private static final int STEP_DURATION_MINUTES = 5;
  
  {
    setUp(
        CommonScenario.mainScenario
            .injectOpen(
                atOnceUsers(1)
            ).protocols(Webtours.httpProtocol)
    )
        .maxDuration(Duration.ofMinutes(STEP_DURATION_MINUTES))
        .assertions(
            global().failedRequests().count().is(0L)
        );
  }
}