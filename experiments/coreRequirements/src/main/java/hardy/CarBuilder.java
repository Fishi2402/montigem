package hardy;

//+ GEN-P        : generated for the product

import java.util.Set;

// TODO: ähnlich wie PersonBuilder aufbauen
public class CarBuilder extends Car {

    protected boolean buildFinished = false;

    CarValidator validator;

    public CarBuilder(long gemId, CarValidator validator) {
       this.gemId = gemId;
       this.validator = validator;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    // todo: Build-Methode komplettieren ähnlich zu unten beschrieben

    public Car build() {
      // todo: incomplete! (checks, and build dependent objects)
      if(!buildFinished) {
        if(validator.buildingCheck(this)) {
            buildFinished = true;
            // todo: add object to the Manager (for Persistence)
            notifyObservers();      //+ an initial notification
            return this;
        } else {
            throw new DataConsistencyViolationException("0xU1234: Person cannot be built.");
          // todo reject the build
        }
      }
      return null;
    }

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


