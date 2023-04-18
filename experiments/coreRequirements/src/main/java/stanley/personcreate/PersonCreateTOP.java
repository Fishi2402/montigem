/* (c) https://github.com/MontiCore/monticore */
package stanley.personcreate;

// GEN-T

/* This is the code that is generated for testing the GUI
   functionality within Java:
   It mimicks the GUI functionality as well as
   prepares an API where the test drive can mimick user inputs
*/

import hardy.FlensburgManager;
import hardy.Person;
import hardy.PersonBuilder;
import hardy.PersonErrorObserver;
import umlp.jsweet.extension.annotation.Directive;

/********************************************************/

@Directive
public class PersonCreateTOP implements PersonErrorObserver {

  // Person does not exist in the beginning, use builder instead
  protected PersonBuilder pb;

  protected String gemTextInputComp1ValueError = "";

  protected String gemNumberInputComp2ValueError = "";

  /* Z1
   * Constructor: uses the same parameters as the GUI-page,
   * The constructor call mimicks the start of the Window: it is opened,
   * its values are set etc.
   */
  public PersonCreateTOP() {
    this.pb = FlensburgManager.personBuilder();
    // subcomponents, wie TextFeld werden in der Simulation nicht extra besetzt
    // weil der Wert direkt in Person gespeichert bleibt.
  }

  public String getGemTextInputComp1ValueError() {
    return this.gemTextInputComp1ValueError;
  }

  public void notifyNicknameInvalid(Person p, String msg) {
    gemTextInputComp1ValueError = msg;
  }

  // **************************** What is shown? ********************
  // Values in the compartments are simulated via getter's
  // If values are not present, GUI does not change or react

  public String getGemTextInputComp1Value() { return pb.getNickname(); }

  public String getGemNumberInputComp2ValueError() {
    return this.gemNumberInputComp2ValueError;
  }

  public void notifyAgeInvalid(Person p, String msg) {
    gemNumberInputComp2ValueError = msg;
  }

  public int getGemNumberInputComp2Value() { return pb.getAge(); }

  // **************************** What can the User do? ********************
  // Actions that the User can make are simulated via setter's and related functions

  /*
   * Z2
   * When field is filled out, builder sets corresponding value and tries to build a person
   * If it fails due to invalid person state or if person was already built nothing happens,
   * otherwise error handling takes place
   */
  public void setGemTextInputComp1Value(String nickname) {
    pb.nickname(nickname);
  }

  public void setGemNumberInputComp2Value(int age) {
    pb.age(age);
  }

  public void gemButtonComp4LeftClick() {
    create();
  }

  public void create() {
  }

}
