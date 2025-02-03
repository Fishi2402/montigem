package umlp._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import umlp.AbstractUMLPTest;
import umlp._ast.ASTUMLPNode;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractCoCoTest<T extends CoCoTestable> extends AbstractUMLPTest {

  private static final String UMLP_ENDING = ".umlp";
  private static final String CD_ENDING = ".cd";

  private final String cocoValidFile;
  private final String cocoInvalidDirPath;
  private final T coco;
  private final BiConsumer<UMLPCoCoChecker, T> cocoAdder;

  protected UMLPCoCoChecker checker;
  protected Optional<String> umlpFile;

  public AbstractCoCoTest(T coco, BiConsumer<UMLPCoCoChecker, T> cocoAdder) {
    this(coco, cocoAdder, "cocovalid/Valid.umlp", "cocoinvalid/");
  }

  public AbstractCoCoTest(T coco, BiConsumer<UMLPCoCoChecker, T> cocoAdder, String cocoValidFile, String cocoInvalidDirPath) {
    this.coco = coco;
    this.cocoAdder = cocoAdder;
    this.cocoValidFile = cocoValidFile;
    this.cocoInvalidDirPath = cocoInvalidDirPath;
  }

  @Override
  public void _setup() {
    super._setup();
    checker = new UMLPCoCoChecker();
    umlpFile = Optional.empty();
    cocoAdder.accept(checker, coco);
  }

  @Test
  void testValid() {
    ASTCDCompilationUnit ast = parse(cocoValidFile);

    createSymTab(ast);
    checker.checkAll(ast);

    assertNoErrors();
  }

  /**
   * Parses an invalid umlp diagram, calls {@link UMLPCoCoChecker#checkAll(ASTUMLPNode)} on the
   * result and stores the file name in {@link #umlpFile}.
   *
   * @param fileName The file name of the umlp diagramm to parse. When the ending is not .umlp or .cd,
   *                 .umlp will be assumed as default file type
   * @return The parsed ast
   */
  protected ASTCDCompilationUnit parseInvalid(String fileName) {
    String diagrammFile = fileName.endsWith(UMLP_ENDING) || fileName.endsWith(CD_ENDING) ? fileName : fileName + UMLP_ENDING;
    this.umlpFile = Optional.of(diagrammFile);

    ASTCDCompilationUnit ast = parse(cocoInvalidDirPath + diagrammFile);

    createSymTab(ast);
    checker.checkAll(ast);

    return ast;
  }

  /**
   * Assert the number of warnings in the {@link Log}
   *
   * @param expectedFindingsCount The expected number of findings
   */
  protected void assertFindingsCount(long expectedFindingsCount) {
    assertEquals(expectedFindingsCount, Log.getFindingsCount(), "findings count differed");
  }

  protected void assertFindingAtLine(int findingIndex, int atLine, Object... errorMessageArgs) {
    String expectedMessage = getExpectedErrorMessage(coco.getErrorCode(),
                                                     coco.getErrorMessageFormat(),
                                                     errorMessageArgs);
    assertFindingAtLine(findingIndex, expectedMessage, atLine);
  }

  protected String getExpectedErrorMessage(String errorCode, String errorMessageFormat, Object... errorMessageArgs) {
    String message;
    try {
      message = String.format(errorMessageFormat, errorMessageArgs);
    } catch (IllegalFormatException e) {
      message = errorMessageFormat;
    }
    return errorCode + " " + message;
  }

  protected void assertFinding(int findingIndex, String message) {
    assertFindingAtLine(findingIndex, message, Optional.empty());
  }

  protected void assertFindingAtLine(int findingIndex, String message, int atLine) {
    assertFindingAtLine(findingIndex, message, Optional.of(atLine));
  }

  protected void assertFindingAtLine(int findingIndex, String message, Optional<Integer> atLine) {
    List<Finding> findings = Log.getFindings();
    int findingsSize = findings.size();
    if (findingIndex >= findingsSize) {
      fail(String.format("There is no finding with the index %d (there are only %d findings)",
                         findingIndex,
                         findingsSize));
    }
    Finding finding = findings.get(findingIndex);
    atLine.ifPresentOrElse(line -> {
      String file = umlpFile.orElseThrow(() -> new IllegalStateException(
          "no invalid file has been parsed. Call #parseInvalid(String) first!"));
      Optional<SourcePosition> optSourcePosition = finding.getSourcePosition();
      if (optSourcePosition.isEmpty()) fail("finding is missing a source position");
      SourcePosition sourcePosition = optSourcePosition.get();
      assertEquals(file + ":<" + line + "," + sourcePosition.getColumn() + ">: " + message,
                   finding.buildMsg());
    }, () -> assertEquals(message, finding.getMsg()));
  }
}
