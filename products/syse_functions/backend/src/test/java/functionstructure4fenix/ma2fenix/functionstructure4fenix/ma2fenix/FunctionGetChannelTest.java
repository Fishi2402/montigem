package functionstructure4fenix.ma2fenix.functionstructure4fenix.ma2fenix;

import functionstructure4fenix.Category;
import functionstructure4fenix.Channel;
import functionstructure4fenix.Function;
import functionstructure4fenix.FunctionStructure4FenixManager;
import functionstructure4fenix.Port;
import functionstructure4fenix.Type;
import functionstructure4fenix.test_infrastructure.FunctionStructure4FenixMockTestSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionGetChannelTest extends FunctionStructure4FenixMockTestSetup {

  public FunctionGetChannelTest() {
    super(0);
  }

  /**
   * component F {
   *   port in int inPort;
   * }
   */
  @Test
  public void testGetInChannelByName(){
    String inPortName = "inPort";

    Type intType = FunctionStructure4FenixManager.typeBuilder().mctype("int").build().get();
    Category intCategory = FunctionStructure4FenixManager.categoryBuilder().type(intType).build().get();

    Channel c1 = FunctionStructure4FenixManager.channelBuilder().category(intCategory).build().get();
    Port inPort = FunctionStructure4FenixManager.portBuilder().name(inPortName).channel(c1).build().get();

    Function funcF = FunctionStructure4FenixManager.functionBuilder()
        .name("F")
        .inAdd(inPort)
        .build().get();

    assertEquals(c1, funcF.getInChannel(inPortName).get());
  }
}
