// GEN (generated from EditierePerson.gui or
// translated from EditierePrsonGUITOP.java)

/* This is the code that is generated from model (or translated from
   generated Java). It implements GUI page logic using TypeScript.

The source for this class is (taken from models/EditierePerson.gui):

Z1 page EditierePerson(Person p) {

  // show and edit the nickname
Z2  :TextInput(value = p.nickname)

  // show and edit the age as number
Z3  :NumberInput(value = p.age)

  // Button mit Text "Ok" und Aufruf der Speicherung der Änderungen
  // (und die Logik dafür ist generiert und verfügbar)
Z4  :Button(text = "Ok", leftClick = submitAndNavigate(:EditierePersonCars(p)))
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

  /* Internal state, also an input if the page is used as a reusable component.
   * The data value(s): here only Person
   */
  @Input()
  public p: Person;

  // Z1: Page initialization consists of constructor and ngOnInit method

  /*
   * Constructor: services (singletons) are injected and are usable as class
   * fields.
   * CommandManager - run commands, which are sent to the server
   * ActivatedRoute - holds meta-information on current page, such as URL
   * and provides navigation
   * NotificationService - used to show pop-up windows
   * EditierePersonForm - a proxy-like object, controlling user input (see
   * editiere-person.form.ts)
   */
  public constructor(
    public cmdManager: CommandManager,
    public route: ActivatedRoute,
    public notify: NotificationService,
    public epForm: EditierePersonForm
  ) {
  }

  /*
   * Method, which is called when GUI is ready for rendering. Constructor is
   * used purely for class initialization, which is why most of the
   * initialization logic is located here.
   */
  public ngOnInit(): void {
    // Z2, Z3 initialize form
    this.form.init();
    // page initialization is tied to URL changes, since change of a single
    // parameter (e.g. person id) should only reinitialize data, not the
    // rendering of the page
    this.route.paramMap.subscribe(params => this.initPerson(params));
  }

  /*
   * Request data from server
   * ParamMap is used to retrieve person id from URL
   * (e.g. montigem.com/person/$id)
   */
  protected initPerson(params: ParamMap): void {
    if (params.personId > 0) {
      this.cmdManager.runCommand(
        new GetPersonCommand(params.personId),
        // execute function when command is done and data is received
        this.setPageData
      );
    }
  }

  /*
   * Initialize input fields via form
   */
  protected setPageData(p: Person) {
    this.form.setValue(p);
  }

  // AGe: unnötig, die submitAndNavigate Methode kann direkt aufgerufen werden
  public pressButtonOk(): void {
    this.submitAndNavigate();
  }

  // Z4
  /*
   * Update person and navigate to another page
   */
  public submitAndNavigate(): void {
    const p: Person = this.form.getValue();
    this.cmdManager.runCommand(
      new SavePersonCommand(p),
      // callback for successful result
      () => this.onSuccess(p),
      // callback for error
      this.onError
    );
  }

  /*
   * navigate on success
   */
  protected onSuccess(Person p) {
    this.route.navigate("editiere-person-cars", p.id);
  }

  /*
   * show notification on error
   */
  protected onError(error: GemError) {
    this.notify.error(error)
  }

}
