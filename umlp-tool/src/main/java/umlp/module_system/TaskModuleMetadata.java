package umlp.module_system;

import java.util.ArrayList;
import java.util.List;

public class TaskModuleMetadata {
  private String type;
  private List<String> additionalConfigs = new ArrayList<>();
  private List<String> additionalParameters = new ArrayList<>();

  public TaskModuleMetadata(){

  }

  public TaskModuleMetadata(String type, List<String> additionalConfigs, List<String> additionalParameters) {
    this.type = type;
    this.additionalConfigs = additionalConfigs;
    this.additionalParameters = additionalParameters;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getAdditionalConfigs() {
    return additionalConfigs;
  }

  public void setAdditionalConfigs(List<String> additionalConfigs) {
    this.additionalConfigs = additionalConfigs;
  }

  public List<String> getAdditionalParameters() {
    return additionalParameters;
  }

  public void setAdditionalParameters(List<String> additionalParameters) {
    this.additionalParameters = additionalParameters;
  }
}
