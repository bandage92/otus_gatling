package webtours;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.http.HttpRequestActionBuilder;

public class Requests {
  
  // ==================== UC01 ====================
  public static HttpRequestActionBuilder openRootPageGetWelcome = http("UC01.1_OpenRootPage_GET_/welcome.pl")
      .get("/cgi-bin/welcome.pl")
      .queryParam("signOff", "true")
      .check(status().is(200))
      .check(substring("Web Tours").exists());
  
  public static HttpRequestActionBuilder openRootPageGetNav = http("UC01.2_OpenRootPage_GET_/nav.pl")
      .get("/cgi-bin/nav.pl")
      .queryParam("in", "home")
      .check(status().is(200))
      .check(substring("Web Tours Navigation Bar").exists())
      .check(substring("Username").exists())
      .check(regex("<input type=\"hidden\" name=\"userSession\" value=\"([^\"]+)\"/>").saveAs("userSession"));
  
  // ==================== UC02 ====================
  public static HttpRequestActionBuilder login = http("UC02_Login_POST_/login.pl")
      .post("/cgi-bin/login.pl")
      .formParam("userSession", "#{userSession}")
      .formParam("username", "#{username}")
      .formParam("password", "#{password}")
      .formParam("login.x", "42")
      .formParam("login.y", "9")
      .formParam("JSFormSubmit", "off")
      .check(status().is(200))
      .check(substring("Error").notExists());
  
  // ==================== UC03 ====================
  public static HttpRequestActionBuilder getCities = http("UC03_GetCities_GET_/reservations.pl")
      .get("/cgi-bin/reservations.pl")
      .queryParam("page", "welcome")
      .check(status().is(200))
      .check(substring("Flight Selections").exists())
      .check(regex("<option[^>]* value=\"([^\"]+)\">").findAll().saveAs("cities"));
  
  // ==================== UC04 ====================
  public static HttpRequestActionBuilder selectCitiesAndDates = http("UC04_SelectCities&Dates_POST_/reservations.pl")
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
      .check(substring("Find Flight").exists());
  
  // ==================== UC05 ====================
  public static HttpRequestActionBuilder selectFlight = http("UC05_SelectFlight_POST_/reservations.pl")
      .post("/cgi-bin/reservations.pl")
      .formParam("outboundFlight", "#{outboundFlight}")
      .formParam("numPassengers", "1")
      .formParam("advanceDiscount", "0")
      .formParam("seatPref", "None")
      .formParam("seatType", "Coach")
      .formParam("reserveFlights.x", "59")
      .formParam("reserveFlights.y", "6")
      .check(status().is(200))
      .check(substring("Payment Details").exists());
  
  // ==================== UC06 ====================
  public static HttpRequestActionBuilder payment = http("UC06_Payment_POST_/reservations.pl")
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
      .check(substring("Error").notExists());
}