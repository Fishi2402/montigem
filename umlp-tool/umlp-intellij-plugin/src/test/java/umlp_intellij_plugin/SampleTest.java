package umlp_intellij_plugin;

import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {
  private CodeInsightTestFixture myFixture;
  private IdeaProjectTestFixture projectFixture;

  @BeforeEach
  void setUp() throws Exception {
    // 1. Create a project fixture builder
    TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder =
        IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder("MyTestProject");
    projectFixture = fixtureBuilder.getFixture();

    // 2. Create a Code-Insight fixture (with Java support)
    myFixture = IdeaTestFixtureFactory.getFixtureFactory()
        .createCodeInsightFixture(projectFixture);

    // 3. setUp the fixture — this initializes in-memory project, module, PSI, VFS etc.
    myFixture.setUp();
  }

  @AfterEach
  void tearDown() throws Exception {
    if (myFixture != null) {
      myFixture.tearDown();
    }
  }

  @Test
  void testSomethingInEditor() {
    // Example: create a file in the in-memory project
    String fileText = "classdiagram TestDiagram { class A; }";
    myFixture.configureByText("TestDiagram.umlp", fileText);

    // Do something with PSI / plugin logic
    // e.g. find classes / methods
    var psiFile = myFixture.getFile();
    assertNotNull(psiFile);
    System.out.println("Hello World!");
    // ... your plugin invocation / assertions ...
  }
}
