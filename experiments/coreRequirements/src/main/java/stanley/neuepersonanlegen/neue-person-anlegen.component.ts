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
@Component({
  "selector": "neue-person-anlegen",
  "templateUrl": "./neue-person-anlegen.component.html",
  "styles": [
    ":host { display:flex; flex-direction:column; }"
  ],
})
export class NeuePersonAnlegen extends NeuePersonAnlegenTOP implements OnInit {

  public constructor(
  ) {
    super();
  }

  public decreaseAge(Person p): void {
    p.setAge(p.getAge() - 1);
  }

}
