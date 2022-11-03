package hardy;

// HWC-P

// Fügt die Basisklasse java.util.Date zum BaseClassObjectMapper hinzu
public class FlensburgBaseClassObjectMapper extends FlensburgBaseClassObjectMapperTOP {
  FlensburgBaseClassObjectMapper(){
    baseDeSers.put("java.util.Date", new DateDeSer());
  }
}
