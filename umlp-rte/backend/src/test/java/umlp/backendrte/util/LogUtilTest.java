/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.util;

import org.junit.jupiter.api.Test;
import umlp.backendrte.AbstractUMLPRTETest;

import static org.junit.jupiter.api.Assertions.*;

public class LogUtilTest extends AbstractUMLPRTETest {

  @Test
  public void originalPayloadUnaffectedTest() {
    String payload = "{\"id\":41, \"commandType\":\"promodb.commands.User_setPassword\", \"participantId\":1, \"gemId\":2, \"password\":\"should not be visible\", \"visiblepassword\":\"can be visible\"}";
    String payload_copy = new String(payload);
    String concealedPayload = LogUtil.concealSensitives(payload);

    assertTrue(payload.equals(payload_copy));
    assertFalse(payload.equals(concealedPayload));
  }

  @Test
  public void payloadConcealedTest() {
    String payload = "{\"id\":41, \"commandType\":\"promodb.commands.User_setPassword\", \"participantId\":1, \"gemId\":2, \"password\":\"should not be visible\", \"visiblepassword\":\"can be visible\"}";
    String concealedPayload = LogUtil.concealSensitives(payload);

    assertTrue(concealedPayload.contains("\"password\":\"???\""));
    assertFalse(concealedPayload.contains("\"password\":\"should not be visible\""));
    assertTrue(concealedPayload.contains("\"visiblepassword\":\"can be visible\""));
  }

  @Test
  public void formattedPayloadConcealedTest() {
    String payload = "{ \n"
        + "  \"id\":41,\n"
        + "  \"commandType\":\"promodb.commands.User_setPassword\",\n"
        + "  \"participantId\":1,\n"
        + "  \"gemId\":2,\n"
        + "  \"password\":\"should not be visible\", \n"
        + "  \"visiblepassword\":\"can be visible\"\n"
        + "}";
    String concealedPayload = LogUtil.concealSensitives(payload);

    assertTrue(concealedPayload.contains("\"password\":\"???\""));
    assertFalse(concealedPayload.contains("\"password\":\"should not be visible\""));
    assertTrue(concealedPayload.contains("\"visiblepassword\":\"can be visible\""));
  }

  @Test
  public void concealQuoationMarksTest() {
    String payload = "{\"id\":41, \"commandType\":\"promodb.commands.User_setPassword\", \"participantId\":1, \"gemId\":2, \"password\":\"hunter\\\"2\", \"visiblepassword\":\"can be visible\"}";
    String concealedPayload = LogUtil.concealSensitives(payload);

    assertTrue(concealedPayload.contains("\"password\":\"???\""));
    assertFalse(concealedPayload.contains("\"password\":\"???\"2\""));
  }

}
