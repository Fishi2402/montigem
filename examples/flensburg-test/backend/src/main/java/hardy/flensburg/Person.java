/* (c) https://github.com/MontiCore/monticore */

package hardy.flensburg;


public  class Person extends PersonTOP {

  @Override
  public String getClubname() {
    return "";
  }

  @Override
  public boolean checkAge() {
    // & anstelle von && damit immer alle Fehlermeldungen reported werden
    return super.checkAge() & checkAgeOver18();
  }

  public boolean checkAgeOver18() {
    if(_age == null || _age < 18){
      PersonErrorCollector.reportAgeInvalid(this);
      return false;
    }
    return true;
  }
  public void decreaseAge() {
    setAge(getAge()-1);
  }
}


