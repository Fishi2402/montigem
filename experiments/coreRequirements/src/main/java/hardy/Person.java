/* (c) https://github.com/MontiCore/monticore */
package hardy;

//+ HWC-P        : adds implementation for derived attribute

public class Person extends PersonTOP {

  // --------------------------------------------------------------------
  // Constructor (gibt es nur ohne Argumente, weil
  // Rest in init-Methoden ausgelagert ist
    
  protected Person() {
    super();
    // this includes initHuman(); initPerson();
  }

  // -----------------------------------------------------------------


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

  /**
     * Derived attribute becomes a handcoded Method for calculation
     *  / String clubname;  // wird als _nickname +"@"+ mainResidence.city  realisiert
  */
  public String getClubname() {
    // Bem: we demonstrate how to deal with missing data (which is actually not necessary here)
    if(availableMainResidence()) {
      return _nickname + "@" + _mainResidence.get().getCity();
    } else {
      return _nickname +"@"+ "obdachlos"; // should not happen
    }
  }
}
