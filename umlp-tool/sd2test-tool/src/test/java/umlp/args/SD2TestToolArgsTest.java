package umlp.args;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SD2TestToolArgsTest {

  @Test
  void testParseValid() {
    var parsed = sd2test.umlp.args.SD2TestToolArgs.parse(new String[]{
        "--domainModel",
        "examples/testing-with-sd/models/Webshop.umlp",
        "--model",
        "examples/testing-with-sd/test-models/TotalTest.sd",
        "examples/testing-with-sd/test-models/WebshopTest.sd",
        "--model",
        "examples/testing-with-sd/test-models/AnotherTest.sd",
        "--backendSymbolDirectory",
        "examples/testing-with-sd/backend/build/umlp/backend_symbols",
        "--testSymbolDirectory",
        "examples/testing-with-sd/backend/build/umlp/test_symbols",
        "--outputDirectory",
        "examples/testing-with-sd/backend/build/umlp/backend_test",
        "--hwcDirectory",
        "examples/testing-with-sd/backend/src/test/java/",
    });
    var expected = new sd2test.umlp.args.SD2TestToolArgs("examples/testing-with-sd/models/Webshop.umlp",
                                       List.of("examples/testing-with-sd/test-models/TotalTest.sd",
                                               "examples/testing-with-sd/test-models/WebshopTest" +
                                               ".sd",
                                               "examples/testing-with-sd/test-models/AnotherTest" +
                                               ".sd"),
                                       "examples/testing-with-sd/backend/build/umlp" +
                                       "/backend_symbols",
                                       "examples/testing-with-sd/backend/build/umlp/test_symbols",
                                       "examples/testing-with-sd/backend/build/umlp/backend_test",
                                       "examples/testing-with-sd/backend/src/test/java/");
    assertEquals(expected, parsed);
  }
}
