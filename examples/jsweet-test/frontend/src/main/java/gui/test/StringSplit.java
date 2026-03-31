package gui.test;

import def.dom.HTMLInputElement;
import def.dom.HTMLTextAreaElement;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

import static def.dom.Globals.document;

@Component
public class StringSplit extends StringSplitTOP {

  private void setResultFieldEntry(String[] values) {
    HTMLTextAreaElement el = (HTMLTextAreaElement) document.getElementById(getResultFieldId());
    if (el != null) {
      el.value = String.join("\n", values);
    } else {
      System.err.println("Could not find input element with ID: " + getResultFieldId() + "Input");
    }
  }

  private String[] splitInput() {
    // ID of the input field from the .gui file (inputTestFieldId) + "Input"
    HTMLInputElement inputEl = (HTMLInputElement) document.getElementById("inputTestFieldIdInput");
    HTMLInputElement regexEl = (HTMLInputElement) document.getElementById("regexTestFieldIdInput");

    if (hasValue(inputEl) && hasValue(regexEl)) {
      try {
        String input = inputEl.value;
        String regex = regexEl.value;
        return input.split(regex);
      } catch (Exception e) {
        System.err.println("Split error: " + e.getMessage());
      }
    }

    return new String[0];
  }

  private String[] limitSplitInput() {
    // ID of the input field from the .gui file (inputTestFieldId) + "Input"
    HTMLInputElement inputEl = (HTMLInputElement) document.getElementById("inputTestFieldIdInput");
    HTMLInputElement regexEl = (HTMLInputElement) document.getElementById("regexTestFieldIdInput");
    HTMLInputElement limitEl = (HTMLInputElement) document.getElementById("limitTestFieldIdInput");

    if (hasValue(inputEl) && hasValue(regexEl) && hasValue(limitEl)) {
      try {
        String input = inputEl.value;
        String regex = regexEl.value;
        int limit = Integer.parseInt(limitEl.value);
        return input.split(regex, limit);
      } catch (Exception e) {
        System.err.println("Split error: " + e.getMessage());
      }
    }

    return new String[0];
  }

  private boolean hasValue(HTMLInputElement el) {
    return el != null && el.value != null && !el.value.isEmpty();
  }

  @Override
  public Consumer<Void> testSplit() {
    return (Void) -> setResultFieldEntry(splitInput());
  }

  @Override
  public Consumer<Void> testLimitSplit() {
    return (Void) -> setResultFieldEntry(limitSplitInput());
  }
}
