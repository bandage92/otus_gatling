package webtours;

import static java.util.concurrent.ThreadLocalRandom.current;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import io.gatling.javaapi.core.*;

public class Actions {
  
  // Open root page & get 'userSession'
  public static ChainBuilder openRootPage() {
    return exec(
        http("UC01.1_OpenRootPage_GET_/welcome.pl") // без этого запроса userSession выглядит как '0AA'
            .get("/cgi-bin/welcome.pl")
            .queryParam("signOff", "true")
            .check(status().is(200))
    )
        .exec(
            http("UC01.2_OpenRootPage_GET_/nav.pl")
                .get("/cgi-bin/nav.pl")
                .queryParam("in", "home")
                .check(status().is(200))
                .check(regex(
                    "<input type=\"hidden\" name=\"userSession\" value=\"([^\"]+)\"/>").saveAs("userSession"))
        )
        .exec(session -> {
          System.out.println("userSession: " + session.getString("userSession"));
          return session;
        });
  }
  
  // ================================================================
  
  // Login
  public static ChainBuilder login() {
    return exec(
        http("UC02_Login_POST_/login.pl")
            .post("/cgi-bin/login.pl")
            .formParam("userSession", "#{userSession}")
            .formParam("username", "#{username}")
            .formParam("password", "#{password}")
            .formParam("login.x", "42")
            .formParam("login.y", "9")
            .formParam("JSFormSubmit", "off")
            .check(status().is(200))
    )
        .exec(session -> {
          System.out.println("Login: " + session.getString("username") + " / " + session.getString("password"));
          return session;
        });
  }
  
  // ================================================================
  
  // Go to 'Flights' page & and get distinct 'cities'
  public static ChainBuilder goToFlights() {
    return exec(
            http("UC03_GetCities_GET_/reservations.pl")
                .get("/cgi-bin/reservations.pl")
                .queryParam("page", "welcome")
                .check(status().is(200))
                .check(regex("<option[^>]* value=\"([^\"]+)\">").findAll().saveAs("cities"))
        )
        .exec(session -> {
          List<String> cities = session.getList("cities");
          if (!cities.isEmpty()) {
            // Get random distinct cities
            List<String> uniqueCities = cities.stream().distinct().toList();
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
          }
          return session;
        });
  }
  
  // ================================================================
  
  // Select cities and dates & get 'outboundFlight'
  public static ChainBuilder selectCitiesAndDates() {
    return exec(
        http("UC04_SelectCities&Dates_POST_/reservations.pl")
            .post("/cgi-bin/reservations.pl")
            .formParam("advanceDiscount", "0")
            .formParam("depart", "#{departCity}")
            .formParam("departDate", "#{departDate}")
            .formParam("arrive", "#{arrivalCity}")
            .formParam("returnDate", "#{returnDate}")
            .formParam("numPassengers", "1")
            .formParam("seatPref", "None")
            .formParam("seatType", "Coach")
            .formParam("roundtrip", "true")
            .formParam("findFlights.x", "55")
            .formParam("findFlights.y", "10")
            .formParam(".cgifields", "roundtrip")
            .formParam(".cgifields", "seatType")
            .formParam(".cgifields", "seatPref")
            .check(status().is(200))
            .check(regex("name=\"outboundFlight\" value=\"([^\"]+)\"").findAll().saveAs("outboundFlights"))
    )
        .exec(session -> {
          List<String> flights = session.getList("outboundFlights");
          if (!flights.isEmpty()) {
            int randomIndex = current().nextInt(flights.size());
            String selectedFlight = flights.get(randomIndex);
            System.out.println("Selected flight: " + selectedFlight);
            
            session = session.set("outboundFlight", selectedFlight);
          }
          return session;
        });
  }
  
  // ================================================================
  
  // Select flight
  public static ChainBuilder selectFlight() {
    return exec(
        http("UC05_SelectFlight_POST_/reservations.pl")
            .post("/cgi-bin/reservations.pl")
            .formParam("outboundFlight", "#{outboundFlight}")
            .formParam("numPassengers", "1")
            .formParam("advanceDiscount", "0")
            .formParam("seatPref", "None")
            .formParam("seatType", "Coach")
            .formParam("reserveFlights.x", "59")
            .formParam("reserveFlights.y", "6")
            .check(status().is(200))
    );
  }
  
  // ================================================================
  
  // Payment
  public static ChainBuilder payment() {
    return exec(
        http("UC06_Payment_POST_/reservations.pl")
            .post("/cgi-bin/reservations.pl")
            .formParam("firstName", "#{firstName}")
            .formParam("lastName", "#{lastName}")
            .formParam("address1", "#{address1}")
            .formParam("address2", "#{address2}")
            .formParam("pass1", "#{firstName} #{lastName}")
            .formParam("creditCard", "#{creditCard}")
            .formParam("expDate", "#{expDate}")
            .formParam("saveCC", "on")
            .formParam("oldCCOption", "on")
            .formParam("numPassengers", "1")
            .formParam("seatPref", "None")
            .formParam("seatType", "Coach")
            .formParam("outboundFlight", "#{outboundFlight}")
            .formParam("advanceDiscount", "0")
            .formParam("returnFlight", "")
            .formParam("JSFormSubmit", "off")
            .formParam("buyFlights.x", "35")
            .formParam("buyFlights.y", "7")
            .formParam(".cgifields", "saveCC")
            .check(status().is(200))
            .check(substring("Invoice").exists())
    );
  }
  
  // ================================================================
  
  // Method to generate 'departDate' & 'returnDate'
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
    });
  }
}