/* (c) https://github.com/MontiCore/monticore */

package gradle.common;

import org.gradle.api.invocation.Gradle;
import org.gradle.api.provider.Provider;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

/**
 * We tend to run into timeout errors with playwright.
 * This service ensures, that each task using service, MAY NOT be run in parallel
 */
public abstract class NonParallelPlaywrightService implements BuildService<BuildServiceParameters.None>, AutoCloseable {

  public static final String SERVICE_NAME = "NonParallelPlaywrightService";
  @Override
  public void close() throws Exception {
    // Deliberately empty
  }

  public static Provider<NonParallelPlaywrightService> get(Gradle gradle) {
    return gradle.getSharedServices().registerIfAbsent(SERVICE_NAME, NonParallelPlaywrightService.class, spec -> {
      // no spec, but make gradle happy
    });
  }
}
