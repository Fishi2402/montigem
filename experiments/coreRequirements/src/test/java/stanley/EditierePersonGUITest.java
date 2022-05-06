package stanley;

// HW-T

/* This is sime code that tests the GUI (or later also E2E)
   functionality within Java:
*/


public class EditierePersonGUITest {

runATest() {
  // eine Ausbaustufe wäre: einen Validator hinzufügen und wenn der failed,
  // zusaetlich eine Fehlermeldung anzeigen (die analog angezeigt wird)

// todo: Testklasse kann nun geschrieben werden: tests wären zB:

    Person p = get... PersonDummy... // "theo", 24
    EditierePersonGUI e = EditierePersonGUI(p);
    assert "theo", e.getNickname();
    assert 24, e.getAge();
    e.setAge(33);
    assert 33, e.getAge();
    e.pressButtonDecrease();
    e.pressButtonDecrease();
    assert 31, e.getAge();

// und später kombinieren wir das mit Server/Client kommunikation und prüefen dann 
// nicht nur am lokalen Objekt, sondern simuliert im Gesamten System die Konsistente Änderung

  }
}
