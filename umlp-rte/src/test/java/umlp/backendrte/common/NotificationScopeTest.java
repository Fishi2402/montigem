package umlp.backendrte.common;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotificationScopeTest {

  @Test
  public void testSimpleHierarchy(){
    List<NotificationScope> simpleCases = List.of(NotificationScope.MINIMAL, NotificationScope.LOCAL, NotificationScope.GLOBAL, NotificationScope.ALL);

    for (int i = 0; i < simpleCases.size(); i++) {
      NotificationScope first = simpleCases.get(i);
      for (int j = 0; j < simpleCases.size(); j++) {
        NotificationScope second = simpleCases.get(j);
        if(i <= j) {
          System.out.println(second.name() + " >= " + first.name());
          assertTrue(second.contains(first), second.name() + " does not contain " + first.name());
        }else{
          System.out.println(second.name() + " < " + first.name());
          assertFalse(second.contains(first), second.name() + " does contain " + first.name());
        }
      }
    }
  }
}
