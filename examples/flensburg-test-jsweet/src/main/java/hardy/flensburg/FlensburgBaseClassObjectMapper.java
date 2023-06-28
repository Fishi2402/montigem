/* (c) https://github.com/MontiCore/monticore */
package hardy.flensburg;

// HWC-P

import umlp.backendrte.common.SerializableDeSer;

// Fügt die Basisklasse java.util.Date zum BaseClassObjectMapper hinzu
public class FlensburgBaseClassObjectMapper extends FlensburgBaseClassObjectMapperTOP {
  FlensburgBaseClassObjectMapper(){
    baseDeSers.put("java.util.Date", new DateDeSer());
    baseDeSers.put("java.time.Instant", new SerializableDeSer());
    baseDeSers.put("java.time.Duration", new SerializableDeSer());
    baseDeSers.put("java.net.InetAddress", new SerializableDeSer());
  }
}
