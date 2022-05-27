package service;

import hardy.FlensburgDummies;
import hardy.FlensburgMock;
import hardy.UMLPApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(classes = UMLPApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestServiceTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void testGreeting() {
    String msg = "Hi,_schoen,_dass_die_Rest_Verbindung_steht";
    String uri = String.format("http://localhost:%s/umlp/api/greeting/%s", port, msg);
    String result = restTemplate.getForObject(uri, String.class);
    Assertions.assertEquals(
        String.format("Greeting REST hat funktioniert! Der gesendete Text war: \"%s\"", msg),
        result);
  }

  @Test
  public void testAccessToNoData() {
    FlensburgMock.init();

    String type = "Person";
    String uri = String.format("http://localhost:%s/umlp/api/dbaccess/%s", port, type);
    String result = restTemplate.getForObject(uri, String.class);
    Assertions.assertEquals(
        String.format("Keine Entitäten vom Typ \"%s\" im System enthalten!", type), result);
  }

  @Test
  public void testAccessToWithData() {
    String baseUrl = String.format("http://localhost:%s/umlp/api/dbaccess/", port);

    int numberOfPersonInstances = 5;
    int numberOfCarInstance = 9;
    int numberOfHumanInstances = 10;
    FlensburgMock.init();
    new FlensburgDummies().inventDummiesGenerated(numberOfPersonInstances, numberOfCarInstance,
        numberOfHumanInstances);

    String type = "Person";
    String uri = baseUrl + type;
    String result = restTemplate.getForObject(uri, String.class);
    Assertions.assertEquals(numberOfPersonInstances, result.split(",").length);

    type = "Car";
    uri = baseUrl + type;
    result = restTemplate.getForObject(uri, String.class);
    Assertions.assertEquals(numberOfCarInstance, result.split(",").length);

    type = "Human";
    uri = baseUrl + type;
    result = restTemplate.getForObject(uri, String.class);
    Assertions.assertEquals(numberOfHumanInstances, result.split(",").length);
  }

}