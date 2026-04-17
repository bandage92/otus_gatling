package webtours;

import static io.gatling.javaapi.core.CoreDsl.*;

import io.gatling.javaapi.core.*;

public class CommonScenario {
  
  static FeederBuilder<String> feeder = csv("users.csv").random();
  
  public static ScenarioBuilder mainScenario = scenario("BuyTicket")
      .feed(feeder)
      .exec(Actions.openRootPage())
      .exec(Actions.login())
      .exec(Actions.generateDates())
      .exec(Actions.goToFlights())
      .exec(Actions.selectCitiesAndDates())
      .exec(Actions.selectFlight())
      .exec(Actions.payment());
}