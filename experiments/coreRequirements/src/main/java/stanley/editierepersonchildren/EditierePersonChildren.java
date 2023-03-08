/* (c) https://github.com/MontiCore/monticore */
package stanley.editierepersonchildren;

import hardy.Person;

// page enables editing of a person and their children, used to test validations of dependent fields
// in this case, age of parent has to be older than a child
public class EditierePersonChildren extends EditierePersonChildrenTOP {

  public EditierePersonChildren(Person p) {
    super(p);
  }

}
