/* (c) https://github.com/MontiCore/monticore */
package stanley.personedit;

import hardy.Person;
import hardy.PersonErrorCollector;
import hardy.PersonErrorObserver;

// normal TOP rules apply
abstract public class PersonEditTOP implements PersonErrorObserver {

  protected final Person p;

  protected String gemNumberInputComp2ValueError = "";

  protected String gemTextInputComp1ValueError = "";

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
  public String getGemNumberInputComp2ValueError() {
    return this.gemNumberInputComp2ValueError;
  }

  public void notifyAgeInvalid(Person p, String msg) {
    gemNumberInputComp2ValueError = msg;
  }

  public int getGemNumberInputComp2Value() {
    return p.getAge();
  }

  public void setGemNumberInputComp2Value(int age) {
    p.setAge(age);
  }

  public String getGemTextInputComp1ValueError() {
    return this.gemTextInputComp1ValueError;
  }

  public void notifyNicknameInvalid(Person p, String msg) {
    gemTextInputComp1ValueError = msg;
  }

  public String getGemTextInputComp1Value() {
    return p.getNickname();
  }

  public void setGemTextInputComp1Value(String nickname) {
    p.setNickname(nickname);
  }

  // GUI component name + auto-assigned component id + parameter name
  // NumberInput + Comp2 + Value
  // GUI component name is optional, but helps to find the correct one
  public void gemButtonComp3LeftClick() {
    this.navigate();
  }

  public void gemButtonComp4LeftClick() {
    decreaseAge();
  }

  protected void navigate() {
  }

  public void decreaseAge() {
  }

}


