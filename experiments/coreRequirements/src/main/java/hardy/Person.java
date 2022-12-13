package hardy;

//+ HWC-P        : adds implementation for derived attribute

import javax.persistence.Entity;
import javax.persistence.Table;

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
    if(age == null || age < 18){
      PersonErrorCollector.reportAgeInvalid(this);
      return false;
    }
    return true;
  }

  /**
     * Derived attribute becomes a handcoded Method for calculation
     *  / String clubname;  // wird als nickname +"@"+ mainResidence.city  realisiert
     */
    public String getClubname() {
      // Bem: we demonstrate how to deal with missing data (which is actually not necessary here)
      if(availableMainResidence()) {
        return nickname + "@" + mainResidence.get().getCity();
      } else {
        return nickname +"@"+ "obdachlos"; // should not happen
      }
    }
}
