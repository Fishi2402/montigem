package hardy;

import java.util.*;

/*+ PersonBuilder extends Person: it thus can be used as person even if 
    not yet completed: Helpful during creation, when entered values come in 
    as typed by the user
    
    furthermore, geter (and also setters can be reused)
    
*/
public class PersonBuilder extends Person {

    // flag: if Build has finished
    protected boolean buildFinished = false;

    // a flag for each mandatory attribute: has the attribute been set?   
    protected boolean agePresent = false;
    protected boolean nicknamePresent = false;
    protected boolean ownsPresent = false;

    public PersonBuilder() {
       init();
    }

    public PersonBuilder nickname(String nickname) {
        super.setNickname(nickname);
        this.nicknamePresent = true;
        return this;
    }

    public PersonBuilder age(int age) {
        super.setAge(age);
        this.agePresent = true;
        return this;
    }

    public PersonBuilder owns(Set<Car> o) {
        super.clearOwnss();
        super.addAllOwnss(o);
        this.ownsPresent = true;
        return this;
    }

    // 'addOwns' clashes with addOwns:Person in super class
    public PersonBuilder ownsAdd(Car o) {
        this.owns.add(o);
        return this;
    }

    // Potentiell interessant ... ?
//    @Override
    public boolean isNicknamePresent() {
        return this.nicknamePresent;
    }


// Nein: Laut Konvention geben wir keine Listen raus
// sondern nutzen die genrierten Ftn. um darauf zu operieren
//    public PersonBuilder owns(List<Car> o) {



/*- Das regeln wir anders:, denn dieser Sonderfall erlaubt ja nur eine
    2er Konstruktion:
      public Person build(boolean addToBidirectionalAssociation) {

Wir führen stattdessen die Möglichkeit ein andere ebenfalls noch in Konstruktion 
befindliche Objekte ebenfalls anzumelden und dann später alle gleichzeitig zu 
bilden (wg. Subklassenbildung)

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


