// GEN-P (generated from EditierePerson.gui or
// translated from EditierePrsonGUITOP.java)

/* This is the code that is generated from model (or translated from
   generated Java). It implements GUI page logic using TypeScript.

The source for this class is (taken from models/EditierePerson.gui):

Z1 page EditierePerson(Person p) {

  // show and edit the nickname
Z2  :TextInput(value = p.nickname)

  // show and edit the age as number
Z3  :NumberInput(value = p.age)

  // Button mit Text "Ok" und Navigation auf eine andere Seite
  // (und die Logik dafür ist generiert und verfügbar)
Z4  :Button(text = "Ok", leftClick = navigate(:EditierePersonCars(p)))
  // todo: Wo steht, wie es danach weitergeht (welche Seite wird gezeigt?)

  // Button mit Text "Discard", und ohne weitere Aktion
Z5  :Button(text = "Discard", leftClick = nichts-tun,aber andere Seite anzeigen)

}
*/

/*
 * Import RTE components and symbols from framework
 */
// import {...} from '...';

// Obligatory decorator for components
@Directive()
// implements ngOnInit method of interface OnInit, which is used for
// initialization (see further below)
export class EditierePersonTOP implements OnInit {

  // Z1: Input parameter

  /* Internal state, also an input, i.e. the object is initialized by other
   * component (@Input annotation functionality)
   * The data value(s): here only Person, value is retrieved automatically if
   * not set explicitly by other component (@DB annotation functionality)
   */
  @Input()
  @DB()
  public p: Person;

  // Z1: Page initialization consists of constructor and ngOnInit method

  /*
   * Constructor: empty, person is set from another component upon navigation or
   * automatically
   */
  public constructor() {
  }

  // Z2, Z3: getter for current values of the input fields

  public getTextInput(): string {
    return p.getNickname();
  }
  public getNumberInput(): number {
    return p.getAge();
  }

  // Z2, Z3: setter method, used to programmatically set the value of input fields

  public setTextInput(nickname: string): void {
    p.setNickname(nickname);
  }

  public setNumberInput(age: number): void {
    p.setAge(age);
  }

  // Z4: function to call on button press
  public pressButtonOk(): void {
    this.navigate();
  }

  // Z4
  /*
   * Update person and navigate to another page
   */
  public navigate(): void {
    NavigationManager.navigate("editiere-person-cars", this.p.id);
  }

  // Z5: function to call on button press
  public pressButtonDecrease(): void {
    this.decreaseAge(this.p);
  }

  public decreaseAge(p: Person): void {
  }

}
