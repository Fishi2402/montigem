// (c) https://github.com/MontiCore/monticore
package umlp.rte;

import java.util.Optional;

public interface GemBuildable {
  //do to type erasure we cannot use GemBuildable<E extends GemBuildable<E>>
  //this would prevent A implements GemBuildable<B> with NOT A extends B

  // Pseudo build for all classes: normally nothing happens
  // due to type erasure we cannot be more specific than Object
  abstract Optional<? extends GemBuildable> build();

  // build being needed implies isBuildFinished being needed.
  // used to avoid calling build twice
  default boolean isBuildFinished() {
    return true;
  }

}
