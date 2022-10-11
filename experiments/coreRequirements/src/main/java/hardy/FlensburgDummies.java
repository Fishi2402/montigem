package hardy;

//+ GEN4T        : generated for test infrastuktur
//+ GEN-P        : generated for the product (z.B. eine Demoversion)

// auch speziell gut geeignet für TOP

/*+
  Aufgaben des Dummy-Erzeugers:
  * Setzt einige Dummy-Objekte
  * und setzt den Manager auf Simulation   
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FlensburgDummies {

  // really handwritten
  // could we generate such a half way intelligent dummy set?
  public void inventDummiesOne() {
  
    FlensburgMock.init();

    FlensburgMock.secretlyAdaptnextGemIds(101);
    Address a101 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a102 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a103 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a104 = FlensburgManager.addressBuilder().city("Flensburg").build().get();
    Address a105 = FlensburgManager.addressBuilder().city("Flensburg").build().get();

    FlensburgMock.secretlyAdaptnextGemIds(1101);
    
    CarBuilder c1101 = FlensburgManager.carBuilder();
    CarBuilder c1102 = FlensburgManager.carBuilder();
    CarBuilder c1103 = FlensburgManager.carBuilder();
    CarBuilder c1104 = FlensburgManager.carBuilder();
    CarBuilder c1105 = FlensburgManager.carBuilder();
    CarBuilder c1106 = FlensburgManager.carBuilder();
    
    FlensburgMock.secretlyAdaptnextGemIds(2101);
    
    Human h2101 = FlensburgManager.humanBuilder().age(21).build().get();
    
    FlensburgMock.secretlyAdaptnextGemIds(3101);




    PersonBuilder pb3101 = FlensburgManager.personBuilder()
        .mainResidence(a101)
        .age(25).nickname("Anna").owns(Collections.singleton(c1101));
    // Dies wird gebraucht, damit überprüfen kann, dass die owns assoziation korrekt ist
    // TODO: Es ist unschön, dass die Assoziation manuell in beide Richtungen gesetzt werden muss
    c1101.owns(pb3101).build();
    Person p3101 = pb3101.build().get();

    Person p3102 = FlensburgManager.personBuilder()
        .mainResidence(a102)
        .age(26).nickname("Bert").build().get();
    PersonBuilder pb3103 = FlensburgManager.personBuilder()
        .age(31).nickname("Charly")
        .mainResidence(a103)
        .ownsAdd(c1102)
        .ownsAdd(c1103);
    c1102.owns(pb3103).build();
    c1103.owns(pb3103).build();
    Person p3103 = pb3103.build().get();

    PersonBuilder pb3104 = FlensburgManager
        .personBuilder()
        .age(104)
        .mainResidence(a104)
        .nickname("Doro")
        .ownsAdd(c1104)
        .ownsAdd(c1105);
    c1104.owns(pb3104).build();
    c1105.owns(pb3104).build();
    Person p3104 = pb3104.build().get();

    PersonBuilder pb3105 = FlensburgManager.personBuilder()
        .mainResidence(a105)
        .age(105).ownsAdd(c1106).nickname("Emil");
    c1106.owns(pb3105).build();
    Person p3105 = pb3105.build().get();
  }
    
  // really handwritten
  public void inventDummiesTwo(int number) {
    FlensburgMock.init();
    for(int i = 0; i < number; i++) {
      CarBuilder cb = FlensburgManager.carBuilder();
    
      Human o2 = FlensburgManager.humanBuilder().age(100+i).build().get();

      PersonBuilder pb = FlensburgManager.personBuilder()
          .age(200 + i)
          .mainResidence(FlensburgManager.addressBuilder().city("City" + i).build().get())
          .nickname("Nick" + i)
          .owns(Collections.singleton(cb));

      cb.owns(pb).build();
      Person o3 = pb.build().get();
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
    Random r = new Random(1001);
    FlensburgDummyProvider provider = new FlensburgDummyProvider(r);

    List<Person> persons = new ArrayList<>();

    for (int i = 0; i < numberOfPersonInstances; i++) {
      persons.add(provider.inventPerson());
    }

    for (int i = 0; i < numberOfCarInstance; i++) {
      provider.inventCar(persons.get(r.nextInt(persons.size())));
    }

    for(int i = 0; i < numberOfHumanInstances; i++){
      provider.inventHuman();
    }
  }
}
