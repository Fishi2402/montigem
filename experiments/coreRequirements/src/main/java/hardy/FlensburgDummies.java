package hardy;

//+ GEN4T        : generated for test infrastuktur
//+ GEN-P        : generated for the product (z.B. eine Demoversion)

// auch speziell gut geeignet für TOP

/*+
  Aufgaben des Dummy-Erzeugers:
  * Setzt einige Dummy-Objekte
  * und setzt den Manager auf Simulation   
*/

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class FlensburgDummies {

  // really handwritten
  // could we generate such a half way intelligent dummy set?
  public void inventDummiesOne() {
  
    FlensburgMock.secretlyAdaptnextGemIds(1101);
    
    Car c1101 = AbstractFlensburgManager.carBuilder().build();
    Car c1102 = AbstractFlensburgManager.carBuilder().build();
    Car c1103 = AbstractFlensburgManager.carBuilder().build();
    Car c1104 = AbstractFlensburgManager.carBuilder().build();
    Car c1105 = AbstractFlensburgManager.carBuilder().build();
    Car c1106 = AbstractFlensburgManager.carBuilder().build();
    
    FlensburgMock.secretlyAdaptnextGemIds(2101);
    
    Human h2101 = AbstractFlensburgManager.humanBuilder().age(21).build();
    
    FlensburgMock.secretlyAdaptnextGemIds(3101);
    
    Person p3101 = AbstractFlensburgManager.personBuilder()
                        .age(25).nickname("Anna").owns(Collections.singleton(c1101)).build();
    Person p3102 = AbstractFlensburgManager.personBuilder()
                        .age(26).nickname("Bert").build();
    Person p3103 = AbstractFlensburgManager.personBuilder()
                        .age(3).nickname("Charly")
                        // TODO AHe: cumbersome, can not use addOwns since it is already defined by Person with return type boolean
                        .owns(new HashSet<>(Arrays.asList(c1102, c1103))).build();
    Person p3104 = AbstractFlensburgManager.personBuilder()
                        .age(104).nickname("Doro")
                        .owns(new HashSet<>(Arrays.asList(c1104, c1105))).build();
    Person p3105 = AbstractFlensburgManager.personBuilder()
                        .age(105).nickname("Emil").build();
      
  } 
    
  // really handwritten
  public void inventDummiesTwo(int number) {
    for(int i = 1; i < number; i++) {

      Car o1 = AbstractFlensburgManager.carBuilder().build();
    
      Human o2 = AbstractFlensburgManager.humanBuilder().age(100+i).build();
    
      Person o3 = AbstractFlensburgManager.personBuilder()
                        .age(200+i)
                        .nickname("Nick"+i)
                        .owns(Collections.singleton(o1)).build();
      // todo: Ausbauoption: variable Menge von Cars
      // (das erfordert insgesamt Smartness beim generieren, um sinnvolle graphen zu bauen)
    }
  }

  // How a generated method could look like
  // alternative: numberOfObjects
  public void inventDummiesGenerated(
      int numberOfPersonInstances,
      int numberOfCarInstance,
      int numberOfHumanInstances // excluding Persons?
      // TODO: some way to influence associations?
  ){
    for (int i = 0; i < numberOfPersonInstances; i++) {
      FlensburgDummyManager.inventPerson();
    }

    for (int i = 0; i < numberOfCarInstance; i++) {
      FlensburgDummyManager.inventCar();
    }

    // Since every Car needs a Person, the parameter numberOfPersonOwnsCar can be omitted
    // Maybe this should be done in FlensburgDummyManager.inventCar?
    // - But: would be a special case, for "* <-> *" this would not be needed
    FlensburgDummyManager.inventPersonOwnsCar(numberOfCarInstance);

    // TODO: how do we guarantee that the generated object structure is an instance of the class diagram
  }
}
