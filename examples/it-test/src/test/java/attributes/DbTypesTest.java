package attributes;

import attributes.test_infrastructure.AttributesDBTestSetup;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbTypesTest extends AttributesDBTestSetup {
  public DbTypesTest() {
    super(0);
  }

  @Test
  public void testListOfPrimitivesIsWrittenToDb(){
    // Regression: Double values caused a primary key exception
    List<Integer> values = List.of(0, 0, 1, 2, 3, 4);
    var sut = AttributesManager.listOfPrimitivesBuilder().ints(values).build().get();

    var dao = new ListOfPrimitivesDAO();

    var clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(values, clone.getIntsList());
  }
}
