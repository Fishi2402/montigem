// GEN-P (translated from EditierePersonGUI.java)

/* This is the code that is generated from hand-written Java file.
   It implements additional GUI page logic.

   implementation of following underspecified GUI part:

// Button mit Text "Discard", und ohne weitere Aktion
Z5  :Button(text = "Discard", leftClick = nichts-tun,aber andere Seite anzeigen)

*/

/*
 * Import RTE components and symbols from framework
 */
// import {...} from '...';

// Obligatory decorator for class with actual component implementation
@Component({
  "selector": "editiere-person",
  "templateUrl": "./editiere-person.component.html",
  "styles": [
    ":host { display:flex; flex-direction:column; }"
  ],
})
export class EditierePersonTOP {

  public constructor(
    public notify: NotificationManager,
    public epForm: EditierePersonForm
  ) {
    super(cmdManager, route, notify, epForm);
  }

  public decreaseAge(Person p): void {
    p.setAge(p.getAge() - 1);
  }

}
