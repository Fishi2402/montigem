/* (c) https://github.com/MontiCore/monticore */
package stanley.personedit;

import hardy.Person;
import hardy.PersonErrorCollector;
import hardy.PersonErrorObserver;
import stanley.editierepersoncars.EditierePersonCars;

abstract public class PersonEditTOP implements PersonErrorObserver {

  protected Person p;

  protected String numberInputComp2Error = "";

  protected String textInputComp1Error = "";

  protected PersonEditTOP(Person p) {
    this.p = p;
    PersonErrorCollector.addErrorObserver(this);
    init();
  }

  public void init() {
  }

  public String getNumberInputComp2Error() {
    return this.numberInputComp2Error;
  }

  public void notifyAgeInvalid(Person p, String msg) {
    numberInputComp2Error = msg;
  }

  public int getNumberInputComp2() {
    return p.getAge();
  }

  public void setNumberInputComp2(int age) {
    p.setAge(age);
  }

  public String getTextInputComp1Error() {
    return this.textInputComp1Error;
  }

  public void notifyNicknameInvalid(Person p, String msg) {
    textInputComp1Error = msg;
  }

  public String getTextInputComp1() {
    return p.getNickname();
  }

  public void setTextInputComp1(String nickname) {
    p.setNickname(nickname);
  }

  public void leftClickButtonComp3() {
    this.navigate();
  }

  public void leftClickButtonComp4() {
    decreaseAge();
  }

  protected void navigate() {
  }

  public void decreaseAge() {
  }

}


