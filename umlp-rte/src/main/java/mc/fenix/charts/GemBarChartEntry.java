package mc.fenix.charts;

import java.util.List;
import java.util.Optional;

public class GemBarChartEntry {
  private String label;
  private String stack;
  private List<Integer> data;
  private String backgroundColor;
  private String hoverBackgroundColor;

  public GemBarChartEntry(String label,
                          String stack,
                          List<Integer> data,
                          String backgroundColor,
                          String hoverBackgroundColor) {
    this.label = label;
    this.stack = stack;
    this.data = data;
    this.backgroundColor = backgroundColor;
    this.hoverBackgroundColor = hoverBackgroundColor;
  }

  public List<Integer> getData() {
    return data;
  }

  public void setData(List<Integer> data) {
    this.data = data;
  }

  public Optional<String> getLabel() {
    return Optional.ofNullable(label);
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Optional<String> getStack() {
    return Optional.ofNullable(stack);
  }

  public void setStack(String stack) {
    this.stack = stack;
  }

  public Optional<String> getBackgroundColor() {
    return Optional.ofNullable(backgroundColor);
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public Optional<String> getHoverBackgroundColor() {
    return Optional.ofNullable(hoverBackgroundColor);
  }

  public void setHoverBackgroundColor(String hoverBackgroundColor) {
    this.hoverBackgroundColor = hoverBackgroundColor;
  }
}
