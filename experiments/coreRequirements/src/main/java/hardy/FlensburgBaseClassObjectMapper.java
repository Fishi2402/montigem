/* (c) https://github.com/MontiCore/monticore */
package hardy;

// HWC-P

import umlp.rte.SerializableDeSer;

// Fügt die Basisklasse java.util.Date zum BaseClassObjectMapper hinzu
public class FlensburgBaseClassObjectMapper extends FlensburgBaseClassObjectMapperTOP {
  FlensburgBaseClassObjectMapper(){
    baseDeSers.put("java.util.Date", new DateDeSer());
    baseDeSers.put("java.time.Instant", new SerializableDeSer());
    baseDeSers.put("java.time.Duration", new SerializableDeSer());
    baseDeSers.put("java.net.InetAddress", new SerializableDeSer());
  }
}
