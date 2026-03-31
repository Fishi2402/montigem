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
      el.value = String.join("#", values);
    } else {
      System.err.println("Could not find input element with ID: " + getResultFieldId() + "Input");
    }
  }

  private String[] splitInput() {
    // ID of the input field from the .gui file (inputTestFieldId) + "Input"
    HTMLTextAreaElement inputEl = (HTMLTextAreaElement) document.getElementById("inputTestFieldId");
    HTMLInputElement regexEl = (HTMLInputElement) document.getElementById("regexTestFieldIdInput");

    if (hasValue(inputEl) && hasValue2(regexEl)) {
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
    HTMLTextAreaElement inputEl = (HTMLTextAreaElement) document.getElementById("inputTestFieldId");
    HTMLInputElement regexEl = (HTMLInputElement) document.getElementById("regexTestFieldIdInput");
    HTMLInputElement limitEl = (HTMLInputElement) document.getElementById("limitTestFieldIdInput");

    if (hasValue(inputEl) && hasValue2(regexEl) && hasValue2(limitEl)) {
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

  private boolean hasValue(HTMLTextAreaElement el) {
    return el != null && el.value != null;
  }

  private boolean hasValue2(HTMLInputElement el) {
    return el != null && el.value != null;
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
