package umlp_intellij_plugin;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BasicIntelliJFeatureTest extends AbstractIntelliJPluginTest {

  @Test
  public void testUMLPHighlightingWork() {
    openFile("models/Mydomain.umlp");
    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty(), "Highlights can not be loaded");
  }

  @Test
  public void testGUIHighlightingWork() {
    openFile("frontend/src/main/gui/gui/Start.gui");
    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty(), "Highlights can not be loaded");
  }

  @Test
  public void testUMLPSymtabErrorWorks() throws IOException {
    String umlpModelPath = "models/Mydomain.umlp";
    copyExternalFileToProject("src/test/resources/symtab_errors/Mydomain.umlp", umlpModelPath);
    openFile(umlpModelPath);

    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty());
    List<HighlightInfo> errors = highlights.stream().filter(it -> it.type.equals(HighlightInfoType.ERROR)).toList();
    assertFalse(errors.isEmpty(), "No symtab errors found");
  }

  @Test
  public void testGUISymtabErrorWorks() throws IOException {
    String guiModelPath = "frontend/src/main/gui/gui/Start.gui";
    copyExternalFileToProject("src/test/resources/symtab_errors/Start.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty());
    List<HighlightInfo> errors = highlights.stream().filter(it -> it.type.equals(HighlightInfoType.ERROR)).toList();
    assertFalse(errors.isEmpty(), "No symtab errors found");
  }

  @Test
  public void testUMLPCoCoErrorWorks() throws IOException {
    String umlpModelPath = "models/Mydomain.umlp";
    copyExternalFileToProject("src/test/resources/coco_errors/Mydomain.umlp", umlpModelPath);
    openFile(umlpModelPath);

    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty());
    List<HighlightInfo> errors = highlights.stream().filter(it -> it.type.equals(HighlightInfoType.ERROR)).toList();
    assertFalse(errors.isEmpty(), "No coco errors found");
  }

  @Test
  public void testGUICoCoErrorWorks() throws IOException {
    String guiModelPath = "frontend/src/main/gui/gui/Start.gui";
    copyExternalFileToProject("src/test/resources/coco_errors/Start.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty());
    List<HighlightInfo> errors = highlights.stream().filter(it -> it.type.equals(HighlightInfoType.ERROR)).toList();
    assertFalse(errors.isEmpty(), "No coco errors found");
  }

  @Test
  public void testUMLPCompletion() throws IOException {
    String umlpModelPath = "models/Mydomain.umlp";
    copyExternalFileToProject("src/test/resources/completion/Mydomain.umlp", umlpModelPath);
    openFile(umlpModelPath);

    waitForLspIndexing();

    LookupElement[] result = getCompletion();
    System.out.println(Arrays.toString(result));
    assertNotNull(result);
    assertNotEquals(0, result.length);
  }

  @Test
  public void testGUICompletion() throws IOException {
    String baseBath = "frontend/src/main/gui/gui/";
    String guiModelPath = baseBath + "Start.gui";
    copyExternalFileToProject("src/test/resources/completion/Another.gui", baseBath + "Another.gui");
    copyExternalFileToProject("src/test/resources/completion/Start.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    LookupElement[] result = getCompletion();
    System.out.println(Arrays.toString(result));
    assertNotNull(result);
    assertNotEquals(0, result.length);

    assertTrue(Arrays.stream(result).anyMatch(it -> it.getLookupString().equals("@Another")));
    assertTrue(Arrays.stream(result).anyMatch(it -> it.getLookupString().equals("@GemText")));

    assertFalse(Arrays.stream(result).anyMatch(it -> it.getLookupString().equals("@Start")));
  }

  @Test
  public void testGUICompletionInParameters() throws IOException {
    String baseBath = "frontend/src/main/gui/gui/";
    String guiModelPath = baseBath + "Start.gui";
    copyExternalFileToProject("src/test/resources/completion/CompletionInParameters.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    LookupElement[] result = getCompletion();
    System.out.println(Arrays.toString(result));
    assertNotNull(result);
    assertNotEquals(0, result.length);

    assertTrue(Arrays.stream(result).anyMatch(it -> it.getLookupString().equals("SomeClass")));
  }

  @Test
  public void testUMLPNavToDefinition() throws IOException {
    String umlpModelPath = "models/Mydomain.umlp";
    copyExternalFileToProject("src/test/resources/navigation/Mydomain.umlp", umlpModelPath);
    openFile(umlpModelPath);

    waitForLspIndexing();

    PsiElement[] result = getDeclarations();

    assertNotNull(result);
    assertEquals(1, result.length);

    PsiElement elementAtCaret = getElementReferencedAtCaret();
    PsiElement definition = result[0];
    assertEquals("SomeClass", ReadAction.compute(definition::getText));
    assertNotEquals(elementAtCaret, definition);
  }

  @Test
  public void testGUINavToDefinition() throws IOException {
    String guiModelPath = "frontend/src/main/gui/gui/Another.gui";
    copyExternalFileToProject("src/test/resources/navigation/Another.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    PsiElement[] result = getDeclarations();
    assertNotNull(result);
    assertEquals(1, result.length);

    PsiElement elementAtCaret = getElementReferencedAtCaret();
    PsiElement definition = result[0];
    assertEquals("Start", ReadAction.compute(definition::getText));
    assertNotEquals(elementAtCaret, definition);
  }

  @Test
  public void testGUIToUmlpNavToDefinition() throws IOException {
    String guiModelPath = "frontend/src/main/gui/gui/Start.gui";
    copyExternalFileToProject("src/test/resources/navigation/Start.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    PsiElement[] result = getDeclarations();
    assertNotNull(result);
    assertEquals(1, result.length);

    PsiElement elementAtCaret = getElementReferencedAtCaret();
    PsiElement definition = result[0];
    assertTrue(definition.getContainingFile().getName().endsWith("Mydomain.umlp"));
    assertEquals("SomeClass", ReadAction.compute(definition::getText));
    assertNotEquals(elementAtCaret, definition);
  }

  @Test
  public void testUMLPNavToDefinitionExtends() throws IOException {
    String umlpModelPath = "models/Mydomain.umlp";
    copyExternalFileToProject("src/test/resources/navigation/ExtendsNavigation.umlp", umlpModelPath);
    openFile(umlpModelPath);

    waitForLspIndexing();

    PsiElement[] result = getDeclarations();

    assertNotNull(result);
    assertEquals(1, result.length);

    PsiElement elementAtCaret = getElementReferencedAtCaret();
    PsiElement definition = result[0];
    assertEquals("B", ReadAction.compute(definition::getText));
    assertNotEquals(elementAtCaret, definition);
  }

  @Test
  public void testGUIIterateTypecheckIsCorrect() throws IOException {
    String guiModelPath = "frontend/src/main/gui/gui/Start.gui";
    copyExternalFileToProject("src/test/resources/typecheck/IterateStart.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty());
    List<HighlightInfo> errors = highlights.stream().filter(it -> it.type.equals(HighlightInfoType.ERROR)).toList();
    assertTrue(errors.isEmpty(), "Error(s) found for correct model:\n" + errors.stream().map(Objects::toString).collect(Collectors.joining("\n")));
  }

  @Test
  public void testGUIEventTypecheckIsCorrect() throws IOException {
    String guiModelPath = "frontend/src/main/gui/gui/Start.gui";
    copyExternalFileToProject("src/test/resources/typecheck/EventStart.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty());
    List<HighlightInfo> errors = highlights.stream().filter(it -> it.type.equals(HighlightInfoType.ERROR)).toList();
    assertTrue(errors.isEmpty(), "Error(s) found for correct model:\n" + errors.stream().map(Objects::toString).collect(Collectors.joining("\n")));
  }

  @Test
  public void testGUIGuardTypecheckIsCorrect() throws IOException {
    String guiModelPath = "frontend/src/main/gui/gui/Start.gui";
    copyExternalFileToProject("src/test/resources/typecheck/GuardStart.gui", guiModelPath);
    openFile(guiModelPath);

    waitForLspIndexing();

    List<HighlightInfo> highlights = getHighlights();
    assertFalse(highlights.isEmpty());
    List<HighlightInfo> errors = highlights.stream().filter(it -> it.type.equals(HighlightInfoType.ERROR)).toList();
    assertTrue(errors.isEmpty(), "Error(s) found for correct model:\n" + errors.stream().map(Objects::toString).collect(Collectors.joining("\n")));
  }
}
