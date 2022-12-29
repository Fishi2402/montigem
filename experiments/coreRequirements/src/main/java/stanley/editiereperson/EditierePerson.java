/* (c) https://github.com/MontiCore/monticore */
package stanley.editiereperson;

// HWC-P

import hardy.Person;

public class EditierePerson extends EditierePersonTOP {
  public EditierePerson(Person p) {
    super(p);
  }

  @Override
  protected void decreaseAge(Person p) {
    // TODO AHe: Error handling? Permission etc
    p.setAge(p.getAge() - 1);
  }

  public void testAsynchronFunction(Person p) {
    Person child = p.getChildren().get(0);
    System.out.println(child.getAge());
  }

//  public async void testAsynchronFunctionJS(Person p) {
//    Person child = await (await p.getChildren()).get(0);
//    System.out.println(await child.getAge());
//  }
}
