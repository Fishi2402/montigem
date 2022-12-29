/* (c) https://github.com/MontiCore/monticore */
// GEN-P (generated from EditierePersonCars.gui or
// translated from EditierePrsonCarsGUI.java)

/* This is the code that is generated from model (or translated from
   generated Java). It implements GUI page logic using TypeScript.
*/

/*
 * Import RTE components and symbols from framework
 */
// import {...} from '...';

// Obligatory decorator for components
@Directive()
export class NeuePersonAnlegenTOP {

  /* Internal state, also an input, i.e. the object is initialized by other
   * component (@Input annotation functionality)
   * The data value(s): here only Person, value is retrieved automatically if
   * not set explicitly by other component (@DB annotation functionality)
   */
  @Input()
  public pb: PersonBuilder = FlensburgManager.personBuilder();

  protected numberInputError: string = "";

  /*
   * Constructor: empty, person is set from another component upon navigation or
   * automatically
   */
  public constructor() {
  }

  protected build(): void {
    try {
      if (!this.pb.isBuildFinished() && this.pb.isBuildable()) {
        this.pb.build();
      }
    } catch (IllegalStateException ex) {
      // TODO something goes completely wrong in client (should never happen) or server responds
      //  with an error, handle, but do not stop execution, GUI is never unresponsive
    }
  }

  public setTextInput(String nickname): void {
    // TODO builder method has to call Person::setNickname or GUI has to track the state of the
    //  person object
    this.pb.nickname(nickname);
    this.build();
  }

  public String getTextInput() { return this.pb.getNickname(); }

  public setNumberInput(int age): void {
    this.pb.age(age);
    this.build();
  }

  public getNumberInput(): number {
    return this.p.getAge();
  }

  public getNumberInputError(): string { return this.numberInputError; }

  public EditierePersonCarsGUI pressButtonOk() {
    this.pb.build();
    return this.navigate();
  }

  protected navigate(): void {
    if (this.pb.isBuildFinished()) {
      // navigate to page
      // return new EditierePersonCarsGUI(pb);
    }
  }

  public pressButtonDecrease(): void {
    this.decreaseAge(pb);
  }

  /*
   * see EditierePersonGUITOP.java
   */
  protected decreaseAge(PersonBuilder pb): void {
    // generated empty stub
  }

}
