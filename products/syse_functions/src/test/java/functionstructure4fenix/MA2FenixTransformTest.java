package functionstructure4fenix;

import functionstructure4fenix.ma2fenix.MA2FenixTool;
import functionstructure4fenix.test_infrastructure.FunctionStructure4FenixDBTestSetup;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MA2FenixTransformTest extends FunctionStructure4FenixDBTestSetup {
  public MA2FenixTransformTest(){
    super(0);
  }

  Path input = Paths.get("src/test/resources/ma2fenix");

  @Test
  public void testSomething(){
    String[] args = new String[] {
        "-mp", input.toAbsolutePath().toString(),
        "-main", "L"
    };
    System.out.println(input.toAbsolutePath().toString());
    MA2FenixTool.main(args);
  }
}
