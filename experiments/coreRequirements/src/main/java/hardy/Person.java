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
