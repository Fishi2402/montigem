/* (c) https://github.com/MontiCore/monticore */
package gradle.common;

import org.gradle.api.provider.ListProperty;
import org.gradle.workers.WorkParameters;


public interface ArgActionParameter extends WorkParameters {
    ListProperty<String> getArgs();
}
