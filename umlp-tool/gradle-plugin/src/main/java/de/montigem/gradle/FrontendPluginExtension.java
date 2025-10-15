package de.montigem.gradle;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class FrontendPluginExtension {
  private final Property<Boolean> withCD2GUI;

  @Inject
  public FrontendPluginExtension(ObjectFactory objects) {
    this.withCD2GUI = objects.property(Boolean.class);
    this.withCD2GUI.convention(false);
  }

  public Property<Boolean> getWithCD2GUI() {
    return withCD2GUI;
  }
}
