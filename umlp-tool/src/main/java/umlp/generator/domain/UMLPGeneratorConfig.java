/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain;

import org.apache.commons.cli.CommandLine;

import static umlp.UMLPToolOptions.GUI_GEN_JSWEET;
import static umlp.UMLPToolOptions.GUI_GEN_TS_FUNC;
import static umlp.UMLPToolOptions.USE_PLAYWRIGHT;
import static umlp.UMLPToolOptions.USE_SHELL;
import static umlp.UMLPToolOptions.USE_SPRING;

public class UMLPGeneratorConfig {

  public static final String GUI_GEN_TS_FUNC_DEFAULT = "true";

  private static final String GUI_GEN_JSWEET_DEFAULT = "false";

  private boolean useSpring;
  private boolean usePlaywright;
  private boolean useShell;

  private boolean generateTypescript = true;
  public boolean useJsweet;

  private UMLPGeneratorConfig() {
  }

  private static UMLPGeneratorConfig instance = null;
  public static UMLPGeneratorConfig getInstance() {
    if (instance == null) {
      instance = new UMLPGeneratorConfig();
    }
    return instance;
  }

  public boolean isUseSpring() {
    return useSpring;
  }

  public UMLPGeneratorConfig setUseSpring(boolean useSpring) {
    this.useSpring = useSpring;
    return this;
  }

  public boolean isUsePlaywright() {
    return usePlaywright;
  }

  public UMLPGeneratorConfig setUsePlaywright(boolean usePlaywright){
    this.usePlaywright = usePlaywright;
    return this;
  }

  public boolean isUseShell() {
    return useShell;
  }

  public UMLPGeneratorConfig setUseShell(boolean useShell) {
    this.useShell = useShell;
    return this;
  }

  public boolean isGenerateTypescript() {
    return generateTypescript;
  }

  public UMLPGeneratorConfig setGenerateTypescript(boolean generateTypescript) {
    this.generateTypescript = generateTypescript;
    return this;
  }

  public boolean isUseJsweet() {
    return useJsweet;
  }

  public UMLPGeneratorConfig setUseJsweet(boolean useJsweet) {
    this.useJsweet = useJsweet;
    this.generateTypescript = generateTypescript && !useJsweet;
    return this;
  }

  public UMLPGeneratorConfig initFromCmd(CommandLine cmd) {
    return this.setUseSpring(USE_SPRING.isSelected(cmd))
        .setUseShell(USE_SHELL.isSelected(cmd))
        .setGenerateTypescript(Boolean.parseBoolean(GUI_GEN_TS_FUNC.getValue(cmd, GUI_GEN_TS_FUNC_DEFAULT)))
        .setUseJsweet(Boolean.parseBoolean(GUI_GEN_JSWEET.getValue(cmd, GUI_GEN_JSWEET_DEFAULT)))
        .setUsePlaywright(USE_PLAYWRIGHT.isSelected(cmd));
  }
}
