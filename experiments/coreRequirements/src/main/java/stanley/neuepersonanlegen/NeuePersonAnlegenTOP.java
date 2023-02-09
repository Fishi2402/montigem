/* (c) https://github.com/MontiCore/monticore */
package stanley.neuepersonanlegen;

// GEN-T

/* This is the code that is generated for testing the GUI
   functionality within Java:
   It mimicks the GUI functionality as well as
   prepares an API where the test drive can mimick user inputs

The source for this class was (taken from models/NeuePersonAnlegen.gui):

Z1 page NeuePersonAnlegen(Person p) { // "leere" Person

  // show and edit the nickname
Z2  :TextInput(value = p.nickname)

  // show and edit the age as number
Z3  :NumberInput(value = p.age)

  // Button mit Text "Ok" und Navigation auf eine andere Seite
  // (und die Logik dafür ist generiert und verfügbar)
Z4  :Button(text = "Ok", leftClick = navigate(:EditierePersonCars(p))

  // Button mit Text "Discard", und ohne weitere Aktion
Z5  :Button(text = "Decrease", leftClick = decreaseAge(p))

}

*/

import hardy.FlensburgManager;
import hardy.PersonBuilder;
import stanley.personcarsedit.PersonCarsEdit;

/********************************************************/

public class NeuePersonAnlegenTOP {

  // Person does not exist in the beginning, use builder instead
  protected PersonBuilder pb;

  /* Z1
   * Constructor: uses the same parameters as the GUI-page,
   * The constructor call mimicks the start of the Window: it is opened,
   * its values are set etc.
   */
  public NeuePersonAnlegenTOP() {
    this.pb = FlensburgManager.personBuilder();
    this.build();
    // subcomponents, wie TextFeld werden in der Simulation nicht extra besetzt
    // weil der Wert direkt in Person gespeichert bleibt.
  }

  /* Z1
   * Builder method: should be called whenever person is potentially ready to be built.
   * Any change to the person object may trigger creation of a person, if the person is valid.
   * Any consecutive changes are considered as an update of the person object
   */
  protected void build() {
    try {
      if (!this.pb.isBuildFinished() && this.pb.isBuildable()) {
        this.pb.build();
      }
    } catch (IllegalStateException ex) {
      // TODO something goes completely wrong in client (should never happen) or server responds
      //  with an error, handle, but do not stop execution, GUI is never unresponsive
    }
  }

  // **************************** What is shown? ********************
  // Values in the compartments are simulated via getter's
  // If values are not present, GUI does not change or react
  
  public String getTextInput() { return pb.getNickname(); }

  public int getNumberInput() { return pb.getAge(); }



  // **************************** What can the User do? ********************
  // Actions that the User can make are simulated via setter's and related functions

  /*
   * Z2
   * When field is filled out, builder sets corresponding value and tries to build a person
   * If it fails due to invalid person state or if person was already built nothing happens,
   * otherwise error handling takes place
   */
  public void setTextInput(String nickname) {
    // TODO builder method has to call Person::setNickname or GUI has to track the state of the
    //  person object
    pb.nickname(nickname);
    this.build();
  }

  /*
   * Z2
   * see setTextInput
   */
  public void setNumberInput(int age) {
    pb.age(age);
    this.build();
  }

  /*
   * see EditierePersonGUITOP.java
   */
  public PersonCarsEdit pressButtonOk() {
    return this.navigate();
  }

  /*
   * navigate the same way as in EditierePersonGUITOP.java, but if a person wasn't built (invalid
   * state or server error), prevent navigation
   */
  protected PersonCarsEdit navigate() throws IllegalStateException {
    if (pb.isBuildFinished()) {
      return new PersonCarsEdit(pb);
    }
    // TODO use better strategy both for testing and GUI implementation
    throw new IllegalStateException("0xU1234: Person cannot be built.");
  }

  /*
   * see EditierePersonGUITOP.java
   */
  public void pressButtonDecrease() {
    decreaseAge(pb);
  }

  /*
   * see EditierePersonGUITOP.java
   */
  protected void decreaseAge(PersonBuilder pb) {
    // generated empty stub
  }

  // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
  // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)

}
