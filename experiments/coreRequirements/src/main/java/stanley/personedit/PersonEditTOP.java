/* (c) https://github.com/MontiCore/monticore */
package stanley.personedit;

import hardy.Person;
import hardy.PersonErrorCollector;
import hardy.PersonErrorObserver;

abstract public class PersonEditTOP implements PersonErrorObserver {

  protected final Person p;

  protected String numberInputComp2ValueError = "";

  protected String textInputComp1ValueError = "";

  protected PersonEditTOP(Person p) {
    this.p = p;
    PersonErrorCollector.addErrorObserver(this);
    init();
  }

  public void init() {
  }

  // method + GUI component name + auto-assigned component id + parameter name + Error
  // get + NumberInput + Comp2 + Value + Error
  // GUI component name is optional, but helps to find the correct one
  public String getNumberInputComp2ValueError() {
    return this.numberInputComp2ValueError;
  }

  public void notifyAgeInvalid(Person p, String msg) {
    numberInputComp2ValueError = msg;
  }

  public int getNumberInputComp2Value() {
    return p.getAge();
  }

  public void setNumberInputComp2Value(int age) {
    p.setAge(age);
  }

  public String getTextInputComp1ValueError() {
    return this.textInputComp1ValueError;
  }

  public void notifyNicknameInvalid(Person p, String msg) {
    textInputComp1ValueError = msg;
  }

  public String getTextInputComp1Value() {
    return p.getNickname();
  }

  public void setTextInputComp1Value(String nickname) {
    p.setNickname(nickname);
  }

  public void buttonComp3LeftClick() {
    this.navigate();
  }

  public void buttonComp4LeftClick() {
    decreaseAge();
  }

  protected void navigate() {
  }

  public void decreaseAge() {
  }

}


