package mc.fenix.charts.GemBarChartTypes;

import java.util.List;
import java.util.Optional;

public class GemBarChartEntry {

  //TODO use optionals in attributes and setters
  private Optional<String> label;
  private Optional<String> stack;
  private List<Integer> data;
  private Optional<String> backgroundColor;
  private Optional<String> hoverBackgroundColor;

  public GemBarChartEntry(Optional<String> label,
                          Optional<String> stack,
                          List<Integer> data,
                          Optional<String> backgroundColor,
                          Optional<String> hoverBackgroundColor) {
    this.label = label;
    this.stack = stack;
    this.data = data;
    this.backgroundColor = backgroundColor;
    this.hoverBackgroundColor = hoverBackgroundColor;
  }

  public Optional<String> getLabel() {
    return label;
  }

  public void setLabel(Optional<String> label) {
    this.label = label;
  }

  public Optional<String> getStack() {
    return stack;
  }

  public void setStack(Optional<String> stack) {
    this.stack = stack;
  }

  public List<Integer> getData() {
    return data;
  }

  public void setData(List<Integer> data) {
    this.data = data;
  }

  public Optional<String> getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Optional<String> backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public Optional<String> getHoverBackgroundColor() {
    return hoverBackgroundColor;
  }

  public void setHoverBackgroundColor(Optional<String> hoverBackgroundColor) {
    this.hoverBackgroundColor = hoverBackgroundColor;
  }

  @Override
  public String toString() {
    return "GemBarChartEntry{" + "label='" + label + '\'' + ", stack='" + stack + '\'' + ", data=" +
           data + ", backgroundColor='" + backgroundColor + '\'' + ", hoverBackgroundColor='" +
           hoverBackgroundColor + '\'' + '}';
  }
}
