package webtours;

import static io.gatling.javaapi.core.CoreDsl.*;

import java.time.Duration;
import io.gatling.javaapi.core.*;

public class ReliabilityTest extends Simulation {
  
  // 100% = 3.0 RPS (according to SLA p95 < 300 ms)
  // 80% of maximum = 2.40 RPS
  private static final double TARGET_RPS = 2.40;
  private static final int TEST_DURATION_MINUTES = 60;
  
  {
    setUp(
        CommonScenario.mainScenario
            .injectOpen(
                // === WARM UP (5 minutes) ===
                rampUsersPerSec(0.0).to(TARGET_RPS).during(Duration.ofMinutes(5)),
                
                // === MAIN TEST (60 minutes) ===
                constantUsersPerSec(TARGET_RPS).during(Duration.ofMinutes(TEST_DURATION_MINUTES)),
                
                // === FINISH (3 minutes) ===
                rampUsersPerSec(TARGET_RPS).to(0.0).during(Duration.ofMinutes(3))
            ).protocols(Webtours.httpProtocol)
    )
        .maxDuration(Duration.ofMinutes(70))
        .assertions(
            global().responseTime().percentile(95).lt(300),
            global().failedRequests().percent().lt(1.0)
        );
  }
}