package webtours;

import static io.gatling.javaapi.core.CoreDsl.*;

import java.time.Duration;
import io.gatling.javaapi.core.*;

public class StepLoadTest extends Simulation {
  
  private static final int STEP_DURATION_MINUTES = 5;
  
  {
    setUp(
        CommonScenario.mainScenario
            .injectOpen(
                // === WARM UP (5 minutes) ===
                rampUsersPerSec(0.0).to(0.35).during(Duration.ofMinutes(5)),
                
                // === STAGE #1: 10% (0.35 RPS) ===
                constantUsersPerSec(0.35).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #2: 20% (0.70 RPS) ===
                rampUsersPerSec(0.35).to(0.70).during(Duration.ofSeconds(30)),
                constantUsersPerSec(0.70).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #3: 30% (1.05 RPS) ===
                rampUsersPerSec(0.70).to(1.05).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.05).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #4: 40% (1.40 RPS) ===
                rampUsersPerSec(1.05).to(1.40).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.40).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #5: 50% (1.75 RPS) ===
                rampUsersPerSec(1.40).to(1.75).during(Duration.ofSeconds(30)),
                constantUsersPerSec(1.75).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #6: 60% (2.10 RPS) ===
                rampUsersPerSec(1.75).to(2.10).during(Duration.ofSeconds(30)),
                constantUsersPerSec(2.10).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #7: 70% (2.45 RPS) ===
                rampUsersPerSec(2.10).to(2.45).during(Duration.ofSeconds(30)),
                constantUsersPerSec(2.45).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #8: 80% (2.80 RPS) ===
                rampUsersPerSec(2.45).to(2.80).during(Duration.ofSeconds(30)),
                constantUsersPerSec(2.80).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE #9: 90% (3.15 RPS) ===
                rampUsersPerSec(2.80).to(3.15).during(Duration.ofSeconds(30)),
                constantUsersPerSec(3.15).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === STAGE 10: 100% (3.50 RPS) ===
                rampUsersPerSec(3.15).to(3.50).during(Duration.ofSeconds(30)),
                constantUsersPerSec(3.50).during(Duration.ofMinutes(STEP_DURATION_MINUTES)),
                
                // === FINISH (3 minutes) ===
                rampUsersPerSec(3.50).to(0.0).during(Duration.ofMinutes(3))
            
            ).protocols(Webtours.httpProtocol)
    )
        .maxDuration(Duration.ofMinutes(70))
        .assertions(
            global().responseTime().percentile(95).lt(300),
            global().failedRequests().percent().lt(1.0)
        );
  }
}