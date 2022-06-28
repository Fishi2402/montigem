package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.Gem;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

/*+ PersonBuilder extends Person: it thus can be used as person even if 
    not yet completed: Helpful during creation, when entered values come in 
    as typed by the user
    
    furthermore, getter (and also setters can be reused)
    
*/
public class PersonBuilder extends Person {

    //+ Note: PersonBuilder does not extend HumanBuilder 

    // -----------------------------------------------------
    // flag: if Build has finished: Object behaves like an 
    // ordinary Person Object
    protected boolean buildFinished = false;

    // -----------------------------------------------------
    // a flag for each mandatory attribute: has the attribute been set?   
    // This is needed e.g. for booleans, and numbers (not neccessarily for Objects)
    protected boolean agePresent = false;
    protected boolean nicknamePresent = false;
    protected boolean ownsPresent = false;
    protected boolean mainResidencePresent = false;

    // the Builder:
    //+ gemId wird bereits beim initialisieren des Builders besetzt
    // und ein Validator wird auch mitgeliefert (und erstmal gespeichert)
    public PersonBuilder(long gemId) {
        this.gemId = gemId;
        // TODO AGe initial value null or not?
        this.owns = new ArrayList<>();
    }

    // Standard set-Function:
    // does NOT use the set funtion of the superclass (which may be overriden
    //      and may be assuming the object is already complete)
    // it does mark the attribute as being set
    public PersonBuilder nickname(String nickname) {
        this.nickname = nickname;
        this.nicknamePresent = true;
        return this;
    }

    public PersonBuilder age(int age) {
        this.age = age;
        this.agePresent = true;
        return this;
    }

    public PersonBuilder mainResidence(Address mainResidence) {
        this.mainResidence = new Gem<Address>(mainResidence);
        this.mainResidencePresent = true;
        return this;
    }

    // --------------------------------------------------------------
    // The association says Set, but the implementation will be a lazylist:
    public PersonBuilder owns(Set<Car> o) {
        super.clearOwnss();
        super.addAllOwnss(o);
        this.ownsPresent = true;
        return this;
    }

    // todo: because 'addOwns' clashes with addOwns:Person in super class: find systematic new name
    public PersonBuilder ownsAdd(Car o) {
        this.owns.add(o);
        return this;
    }

    // + Understand condition of builder -------------------------------------
    public boolean isBuildFinished() {
        return buildFinished;
    }

    public boolean isNicknamePresent() {
        return this.nicknamePresent;
    }

    public boolean isAgePresent() {
        return agePresent;
    }

    public boolean isOwnsPresent() {
        return ownsPresent;
    }

    public boolean isMainResidencePresent() {
        return mainResidencePresent;
    }

    //+ Build ---------------------------------------------------------------
    /*+
     *  Build checks all mandatory requirements (i.e. using a validator)
     * then sets the buildFinished to true and the
     * object behaves like a normal object
     * Main differences:
     *  - from now on all changes are persisted
     *  - observers are now notified
     */

    public Optional<Person> build() {
        if (!buildFinished) {
            // todo: discuss: sollte es nicht einen else Fall geben,
            //  der präzisere Fehler wirft (siehe Log)?
            if (isBuildable()) {
                for (Car own : owns) {
                    if(!own.isBuildFinished()) {
                        own.build();
                    }
                }
                FlensburgManager.addPerson(this);
                personObservers.forEach(obs -> obs.notifyBuild(this)); //+ an initial notification
                buildFinished = true;
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    public boolean isBuildable(){
        // & anstelle von && damit immer alle Fehlermeldungen reported werden
        // TODO AGe zu viele checks? Wie wird definiert, was unbedingt gesetzt werden muss
        //  (Optional, List)?
        return checkAgePresent()
                & checkNicknamePresent()
                & checkMainResidencePresent()
                & checkAge()
                & checkNickname()
                & checkMainResidence()
                & checkOwns();
    }

    public boolean checkAgePresent() {
        if(!agePresent){
            PersonErrorCollector.reportAgeInvalid(this);
        }
        return agePresent;
    }

    public boolean checkNicknamePresent(){
        if(!nicknamePresent){
            PersonErrorCollector.reportNicknameInvalid(this);
        }
        return nicknamePresent;
    }

    public boolean checkMainResidencePresent() {
        if(!mainResidencePresent){
            PersonErrorCollector.reportMainResidenceInvalid(this);
        }
        return mainResidencePresent;
    }

    /*- Das regeln wir anders:, denn dieser Sonderfall erlaubt ja nur eine
    2Objekte-Parallel instantiieren Konstruktion:
      public Person build(boolean addToBidirectionalAssociation) {

Wir führen stattdessen die Möglichkeit ein andere ebenfalls noch in Konstruktion 
befindliche Objekte ebenfalls anzumelden und dann später alle gleichzeitig zu 
bilden (wg. Subklassenbildung)

    // todo: Bitte realisieren
*/

//  public Person build() {
//    if(!buildFinished) {
//      if( ... all mandatory attributes are set)
//      {
//        if( .. an OCL constraint attached to the class says ok)
//        {
//          buildFinished = true;
//          // also run Build on all contained objects
//            for (Person o : child) {
//                o.build();
//            }
//            for (Person o : parent) {
//                o.build();
//            }
//        return this;
//    }
//    throw an Exception here -- this should not happen
//  }
//
//  public boolean isBuildable() {
//      if( ... all mandatory attributes are Present)
//      {
//        if( .. all OCL constraints attached to the class says ok)
//        {
//            return true;
//            (oder Meldungen/Warnungen auch noch sammeln)
//        }
//      }
//      return false;
//  }
  
  /*- das könnten wir auch machen, das erfordert aber noch etwas infrastruktur:
    public boolean allObjectsBuildable() {
      return isLocallyBuildable() && 
        (dasselbe fuer alle eingehängten Objekte)
    }
    ggf. ausklammern
  */

}


