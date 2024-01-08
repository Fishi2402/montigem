package deser;

import baseartifactstest.BaseArtifactsTest4JavaProjectManagerImpl;
import baseartifactstest.BaseArtifactsTestManager;
import baseartifactstest.RootArtifact;
import baseartifactstest.commands.Artifact_get;
import database.GenAbstractNeo4jDBTest;
import javaproject.ExternalJavaSource;
import javaproject.JavaProjectManager;
import javaproject.JavaProjectManagerImpl;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.CommandManager;

import static org.junit.jupiter.api.Assertions.*;

public class CDCompositionDeSerTest extends GenAbstractNeo4jDBTest {

  @Test
  public void testCommandFromBaseCanBeExecuted(){
    JavaProjectManagerImpl.init();
    BaseArtifactsTest4JavaProjectManagerImpl.init();

    RootArtifact r = BaseArtifactsTestManager.rootArtifactBuilder()
        .fullName("someRoot")
        .isExternal(true)
        .name("someRoot")
        .build().get();

    ExternalJavaSource a = JavaProjectManager.externalJavaSourceBuilder()
        .fullName("test.java")
        .simpleName("test")
        .nameExtension("java")
        .modified("never")
        .name("Test")
        .root(r)
        .build().get();

    var res = CommandManager.executeCommand(new Artifact_get(a.getGemId()));
    assertNotNull(res);

    assertEquals(res.asSimpleResult().get(), a);
  }

}
