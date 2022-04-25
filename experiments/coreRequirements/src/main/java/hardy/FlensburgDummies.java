package hardy;

// oder:  (kann man evtl. verwenden, um stand alone Demos zu machen, ohne DB)
//+ GEN-T        : generated for tests

// auch speziell gut geeignet für TOP

/*+
  Aufgaben des Dummy-Erzeugers:
  * Setzt einige Dummy-Objekte
  * und setzt den Manager auf Simulation   
*/

public class FlensburgDummies {

  // really handwritten
  // could we generate such a half way intelligent dummy set?
  public inventDummiesOne() {
  
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
  public inventDummiesTwo(int number) {
    FlensburgMock.init();
    for(i = 1; i < number; i++) {

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
}
