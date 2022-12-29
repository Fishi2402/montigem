/* (c) https://github.com/MontiCore/monticore */
import baseartifactstest.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MinimalFailingExampleTest {

  @Disabled
  @Test
  public void testSimpleUsageOfDB() {
    BaseArtifactsTestManagerImpl.init();
    BaseArtifactsTestManager.rootArtifactBuilder().isExternal(false).fullName("Mein Fullname").build();

  }
}
