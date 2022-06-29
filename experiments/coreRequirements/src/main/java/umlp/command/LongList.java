package umlp.command;

// RTE-P

import java.util.ArrayList;

// Deserialisierung von generischen Typen, wie List<Long>, ist mit Gson kompliziert, insbesondere wenn der Typ als
// string geladen werden soll.
// Dies ist z.B. der Fall bei der Deserialisierung eines SimpleResult<List<Long>> als Antwort auf den Request
// Human_ids.
// Daher wird hier eine Klasse erstellt, welche die geschachtelten Generics versteckt und eine Nutzung mit gson
// vereinfacht.
public class LongList extends ArrayList<Long> {
}
