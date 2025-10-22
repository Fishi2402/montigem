package de.monticore.doc;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.guidsl._prettyprint.GUIDSLExamplePrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuiFormaterTest {

  private String load(String fileName) throws IOException, URISyntaxException {
    return Files.readString(
        Paths.get(getClass().getClassLoader().getResource("examples/test_examples/" + fileName).toURI()),
        StandardCharsets.UTF_8
    );
  }

  private String normalize(String s) {
    return s.replace("\r\n", "\n").replaceAll("[ \t]+(?=\n)", "").trim();
  }

  @Test
  void testFormatGuiFile() throws IOException, URISyntaxException {
    String inputModel = this.load("GemExample_input.gui");
    String expectedModel = this.load("GemExample_expected.gui");

    GUIDSLParser parser = new GUIDSLParser();
    Optional<ASTGUICompilationUnit> optAst = parser.parse_String(inputModel);

    if (optAst.isPresent()) {
      GUIDSLExamplePrettyPrinter printer =
          new GUIDSLExamplePrettyPrinter(new IndentPrinter(), true);
      String printedModel = printer.prettyprint(optAst.get());

      assertEquals(this.normalize(expectedModel.trim()), this.normalize(printedModel.trim()));
    }
    else {
      throw new AssertionError("Parsing failed – AST absent");
    }
  }
}
