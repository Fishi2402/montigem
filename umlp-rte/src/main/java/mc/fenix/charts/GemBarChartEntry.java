package mc.fenix.charts;

import java.util.List;

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

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getStack() {
    return stack;
  }

  public void setStack(String stack) {
    this.stack = stack;
  }

  public List<Integer> getData() {
    return data;
  }

  public void setData(List<Integer> data) {
    this.data = data;
  }

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public String getHoverBackgroundColor() {
    return hoverBackgroundColor;
  }

  public void setHoverBackgroundColor(String hoverBackgroundColor) {
    this.hoverBackgroundColor = hoverBackgroundColor;
  }
}
