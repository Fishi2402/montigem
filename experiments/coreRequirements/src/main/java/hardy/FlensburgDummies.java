package hardy;

//+ GEN4T        : generated for test infrastuktur
//+ GEN-P        : generated for the product (z.B. eine Demoversion)

// auch speziell gut geeignet für TOP

/*+
  Aufgaben des Dummy-Erzeugers:
  * Setzt einige Dummy-Objekte
  * und setzt den Manager auf Simulation   
*/

public class FlensburgDummies {

  // really handwritten
  // could we generate such a half way intelligent dummy set?
  public void inventDummiesOne() {
  
    FlensburgMock.init();
    FlensburgMock.secretlyAdaptnextGemIds(1101);
    
    Car c1101 = FlensburgManager.car().build();
    Car c1102 = FlensburgManager.car().build();
    Car c1103 = FlensburgManager.car().build();
    Car c1104 = FlensburgManager.car().build();
    Car c1105 = FlensburgManager.car().build();
    Car c1106 = FlensburgManager.car().build();
    
    FlensburgMock.secretlyAdaptnextGemIds(2101);
    
    Human h2101 = FlensburgManager.human().age(21).build();
    
    FlensburgMock.secretlyAdaptnextGemIds(3101);
    
    Person p3101 = FlensburgManager.human()
                        .age(25).nickname("Anna").addCar(c1101).build();
    Person p3102 = FlensburgManager.human()
                        .age(26).nickname("Bert").build();
    Person p3103 = FlensburgManager.human()
                        .age(3).nickname("Charly")
                        .addCar(c1102).addCar(c1103).build();
    Person p3104 = FlensburgManager.human()
                        .age(104).nickname("Doro")
                        .addCar(c1104).addCar(c1105).build();
    Person p3105 = FlensburgManager.human()
                        .age(105).nickname("Emil").build();
      
  } 
    
  // really handwritten
  public void inventDummiesTwo(int number) {
    FlensburgMock.init();
    for(int i = 1; i < number; i++) {

      Car o1 = FlensburgManager.car().build();
    
      Human o2 = FlensburgManager.human().age(100+i).build();
    
      Person o3 = FlensburgManager.human()
                        .age(200+i)
                        .nickname("Nick"+i)
                        .addCar(o1).build();
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
