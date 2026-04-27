package webtours;

import static java.util.concurrent.ThreadLocalRandom.current;
import static io.gatling.javaapi.core.CoreDsl.*;
import static webtours.Requests.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import io.gatling.javaapi.core.*;

public class Actions {
  
  // ==================== Open Root Page + Save userSession ====================
  public static ChainBuilder openRootPage() {
    return exec(
        openRootPageGetWelcome,
        openRootPageGetNav
    ).exitHereIfFailed()
        .exec(session -> {
          String userSession = session.getString("userSession");
          if (userSession == null || userSession.isEmpty() || userSession.equals("0AA")) {
            throw new IllegalStateException("userSession is invalid: " + userSession);
          }
          System.out.println("userSession: " + userSession);
          return session;
        });
  }
  
  // ==================== Login ====================
  public static ChainBuilder login() {
    return exec(login)
        .exitHereIfFailed()
        .exec(session -> {
          System.out.println("Login: " + session.getString("username") + " / " + session.getString("password"));
          return session;
        });
  }
  
  // ==================== Get cities + random selection ====================
  public static ChainBuilder goToFlights() {
    return exec(getCities)
        .exitHereIfFailed()
        .exec(session -> {
          List<String> cities = session.getList("cities");
          
          if (cities.isEmpty()) {
            throw new IllegalStateException("No cities extracted from the response");
          }
          
          List<String> uniqueCities = cities.stream().distinct().toList();
          
          if (uniqueCities.size() < 2) {
            throw new IllegalStateException("Not enough distinct cities. Found: " + uniqueCities.size());
          }
          
          int cityCount = uniqueCities.size();
          int departureIndex = current().nextInt(0, cityCount);
          int arrivalIndex = current().nextInt(0, cityCount);
          
          while (arrivalIndex == departureIndex) {
            arrivalIndex = current().nextInt(0, cityCount);
          }
          
          session = session.set("departCity", uniqueCities.get(departureIndex));
          session = session.set("arrivalCity", uniqueCities.get(arrivalIndex));
          
          System.out.println("departCity: " + uniqueCities.get(departureIndex));
          System.out.println("arrivalCity: " + uniqueCities.get(arrivalIndex));
          
          return session;
        });
  }
  
  // ==================== Select cities/dates + extract flight ====================
  public static ChainBuilder selectCitiesAndDates() {
    return exec(selectCitiesAndDates)
        .exitHereIfFailed()
        .exec(session -> {
          List<String> flights = session.getList("outboundFlights");
          
          if (flights.isEmpty()) {
            throw new IllegalStateException("No outbound flights extracted. " +
                "departCity=" + session.getString("departCity") +
                ", arrivalCity=" + session.getString("arrivalCity"));
          }
          
          int randomIndex = current().nextInt(flights.size());
          String selectedFlight = flights.get(randomIndex);
          
          if (selectedFlight == null || selectedFlight.isEmpty()) {
            throw new IllegalStateException("Selected flight is empty");
          }
          
          System.out.println("Selected flight: " + selectedFlight);
          session = session.set("outboundFlight", selectedFlight);
          
          return session;
        });
  }
  
  // ==================== Select flight ====================
  public static ChainBuilder selectFlight() {
    return exec(selectFlight)
        .exitHereIfFailed();
  }
  
  // ==================== Payment ====================
  public static ChainBuilder payment() {
    return exec(payment)
        .exitHereIfFailed();
  }
  
  // ==================== Generate dates ====================
  public static ChainBuilder generateDates() {
    return exec(session -> {
      LocalDate today = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      
      String departDate = today.plusDays(2).format(formatter);
      String returnDate = today.plusDays(7).format(formatter);
      
      session = session.set("departDate", departDate);
      session = session.set("returnDate", returnDate);
      
      System.out.println("departDate: " + departDate);
      System.out.println("returnDate: " + returnDate);
      
      return session;
    }).exitHereIfFailed();
  }
}