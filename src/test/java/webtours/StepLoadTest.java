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
                rampUsersPerSec(0.0).to(0.1).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.1).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.1).to(0.2).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.2).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.2).to(0.3).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.3).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.3).to(0.4).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.4).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.4).to(0.5).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.5).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.5).to(0.6).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.6).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.6).to(0.7).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.7).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.7).to(0.8).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.8).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.8).to(0.9).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.9).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                rampUsersPerSec(0.9).to(1.0).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.0).during(Duration.ofMinutes(STEP_DURATION_MINUTES))
            ).protocols(Webtours.httpProtocol)
    )
        .maxDuration(Duration.ofMinutes(40))
        .assertions(
            global().responseTime().max().lt(5000),
            global().failedRequests().count().lt(5L)
        );
  }
}