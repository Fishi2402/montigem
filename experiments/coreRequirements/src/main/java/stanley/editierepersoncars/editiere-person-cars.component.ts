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
@Component({
  "selector": "editiere-person-cars",
  "templateUrl": "./editiere-person.component.html",
  "styles": [
    ":host { display:flex; flex-direction:column; }"
  ],
})
export class EditierePersonCars implements OnInit {

  /* Internal state, also an input, i.e. the object is initialized by other
   * component (@Input annotation functionality)
   * The data value(s): here only Person, value is retrieved automatically if
   * not set explicitly by other component (@DB annotation functionality)
   */
  @Input()
  public p: Person;

  /*
   * Constructor: empty, person is set from another component upon navigation or
   * automatically
   */
  public constructor() {
  }
}
