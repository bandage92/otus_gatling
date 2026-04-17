package webtours;

import static io.gatling.javaapi.core.CoreDsl.*;

import java.time.Duration;
import io.gatling.javaapi.core.*;

public class StepLoadTest extends Simulation {
  
  // 100% load = 1.0 RPS (60 rpm, 3600 rph)
  private static final int STEP_DURATION_MINUTES = 5;
  
  {
    setUp(
        CommonScenario.mainScenario
            .injectOpen(
                nothingFor(Duration.ofMinutes(1)),
                
                // Each step: smooth rise for 30 sec + hold
                rampUsersPerSec(0.0).to(0.25).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.25).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.25).to(0.5).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.5).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.5).to(0.75).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.75).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.75).to(1.0).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.0).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(1.0).to(1.25).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.25).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(1.25).to(1.5).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.5).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(1.5).to(1.75).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.75).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(1.75).to(2.0).during(Duration.ofSeconds(30)),
                constantUsersPerSec(2.0).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(2.0).to(2.25).during(Duration.ofSeconds(30)),
                constantUsersPerSec(2.25).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(2.25).to(2.5).during(Duration.ofSeconds(30)),
                constantUsersPerSec(2.5).during(Duration.ofMinutes(STEP_DURATION_MINUTES))
            ).protocols(Webtours.httpProtocol)
    )
        .maxDuration(Duration.ofMinutes(60))
        .assertions(
            global().responseTime().max().lt(5000),
            global().failedRequests().count().lt(5L)
        );
  }
}