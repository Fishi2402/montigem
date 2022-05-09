package stanley;

// GEN-T

/* This is the code that is generated for testing the GUI
   functionality within Java:
   It mimicks the GUI functionality as well as
   prepares an API where the test drive can mimick user inputs

The source for this class was (taken from models/EditierePerson.gui):

Z1 page EditierePerson(Person p) {

  // show and edit the nickname
Z2  :TextInput(value = p.nickname)

  // show and edit the age as number
Z3  :NumberInput(value = p.age)

  // Button mit Text "Ok" und Aufruf der Speicherung der Änderungen
  // (und die Logik dafür ist generiert und verfügbar)
Z4  :Button(text = "Ok", leftClick = submit())
  // todo: Wo steht, wie es danach weitergeht (welche Seite wird gezeigt?)

  // Button mit Text "Discard", und ohne weitere Aktion
Z5  :Button(text = "Discard", leftClick = nichts-tun,aber andere Seite anzeigen)

}

*/

import hardy.Person;

/********************************************************/

public class EditierePersonGUITOP {

  // there is internal state: 
  // Part 1: The data value(s): here only Person
  Person p;
  
  // Part 2: The subcomponents
  // gibt es hier nicht unebdingt, die einfachen eingebundenen Elemente
  // sind m besten direkt zu expandieren (das kann evtl. bei kompleren GUIs
  // anders sein


  /* 
   * Constructor: uses the same parameters as the GUI-page), Z1
   * The constructor call mimicks the start of the Window: it is opened,
   * its values are set
   * etc.
   */
  public EditierePersonGUITOP(Person p) {
    this.p = p;
    // subcomponents, wie TextFeld werden in der Simulation nicht extra besetzt
    // weil der Wert direkt in Person gespeichert bleibt.
  }

  // **************************** What is shown? ********************
  // Values in the compartments are simulated via getter's
  
  String getTextInput() { return p.getNickname(); }
  
  int getNumberInput() { return p.getAge(); }

  // das ist alles was auf der Seite gezeigt wird und variabel ist ...



  // **************************** What can the User do? ********************
  // Actions that the User can make are simulated via setter's and related functions

  // todo
  // Bemerkung: Die Propagation der Änderung zB zum Server
  // übernimmt das Objekt selbst! (weshalb hier nichts weiter zu tun ist)
  // indem es ein entsprechendes Kommando absetzt
  void setTextInput(String nickname) { 
    p.setNickname(nickname);
  }
  
  // todo
  // Bemerkung: ein Validator, zB (age >= 0) müsste hier vorgeschaltet werden
  // und statt einer Änderung dann eine Fehler-Anzeige in der GUi induzieren.
  // Zusätzlich: Wenn der Validator hier erfolgreich ist, kann die 
  // Änderung im Server dennoch fehlschlagen: Auch hier wäre eine entsprechende
  // Fehlermeldung relevant
  void setNumberInput(int age) { 
    p.setAge(age);
  }

  // Bemerkung: hier wurde der Text des Buttons in die Methode übernommen,
  // ist das sinnvoll, oder gibt es andere Mechanismen die Unterscheidung 
  // sicherzustellen? (selbes Problem ja auch bei obigen Methoden)
  void pressButtonOk() {
    // todo: was kommt hier her ...: Wechsel zur neuen Seite
  }
  
  void pressButtonDecrease() {
    // Aufruf handgeschriebener Funktionalität in Form einer vorgegebenen Signatur
    // die handgeschriebene Funktion ist angenommen in XXX welcher Klasse?
    // Antwort AHe:
    //   In MontiGem wird aktuell der generierten Seite ein leerer Stub der Method hinzugefügt, dieser muss dann per TOP-mechanismus überschrieben werden.
    //   Daher wird in MontiGem aktuell auch diese Methode nicht generiert und decreaseAge würde direkt vom Button aufgerufen, was für Tests und Expressions allerdings schlechter ist
    
    // aber eigentlich will ich hier eine Expression schreiben!
    decreaseAge(p); // reicht wahrscheinlich nicht, es braucht auch die GUI Seite:
    // AHe: Falls der decreaseAge stub in diese Klasse generiert wird, ist die GUI Seite über *this* zugreifbar
  }

  void decreaseAge(Person p){
    // generated empty stub
  }

  // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
  // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)

// todo: Testklasse kann nun geschrieben werden: siwhe ersten Rohling

}
