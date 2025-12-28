/* (c) https://github.com/MontiCore/monticore */
package common;

import jsweet.lang.Replace;
import umlp.jsweet.extension.annotation.Component;
import java.util.function.Consumer;

@Component
public class CodeViewer extends CodeViewerTOP {

  // --- Expansion Logic ---

  public Consumer<Void> toggleExpanded() {
    return v -> {
        boolean newState = !isIsExpanded();
        setIsExpanded(newState);
    };
  }

  // --- Toggle Logic ---

  public Consumer<Void> toggleGui() {
    return v -> {
        boolean newState = !isIsGuiVisible();
        setIsGuiVisible(newState);
        if (newState && getGuiCode().isEmpty()) {
            loadCode("gui");
        }
    };
  }

  public Consumer<Void> toggleJava() {
    return v -> {
        boolean newState = !isIsJavaVisible();
        setIsJavaVisible(newState);
        if (newState && getJavaCode().isEmpty()) {
            loadCode("java");
        }
    };
  }

  public Consumer<Void> toggleScss() {
    return v -> {
        boolean newState = !isIsScssVisible();
        setIsScssVisible(newState);
        if (newState && getScssCode().isEmpty()) {
            loadCode("scss");
        }
    };
  }

  // --- Data Loading ---

  private void loadCode(String extension) {
    String path = "assets/source-code/" + getBasePath() + "/" + getComponentName() + "." + extension;
    String type = extension; 
    fetchCode(path, type);
  }
  
  @Replace("fetch(path).then(res => { " +
           "  if (!res.ok) { " +
           "    if (res.status === 404) { " +
           "      if (type === 'scss') return '/* No custom SCSS file for this component. */'; " +
           "      if (type === 'java') return '/* No custom Java logic for this component. */'; " +
           "    } " +
           "    throw new Error('Status ' + res.status + ' for ' + path); " +
           "  } " +
           "  return res.text(); " +
           "}).then(text => { " +
           "  if (text.trim().startsWith('<!DOCTYPE html') || text.trim().startsWith('<html') || text.includes('<app-root>')) { " +
           "    if (type === 'scss') { text = '/* No custom SCSS file for this component. */'; } " +
           "    else if (type === 'java') { text = '/* No custom Java logic for this component. */'; } " +
           "    else { text = 'Error: Source file not found (Server returned HTML fallback) for ' + path; } " +
           "  } " +
           "  if (type == 'gui') this.setGuiCode(text); " +
           "  else if (type == 'java') this.setJavaCode(text); " +
           "  else if (type == 'scss') this.setScssCode(text); " +
           "}).catch(err => { " +
           "  console.error('CodeViewer error:', err); " +
           "  var msg = 'Error loading code: ' + err.message; " +
           "  if (type == 'gui') this.setGuiCode(msg); " +
           "  else if (type == 'java') this.setJavaCode(msg); " +
           "  else if (type == 'scss') this.setScssCode(msg); " +
           "})")
  private void fetchCode(String path, String type) {
      // Java dummy implementation
      String msg = "Loading " + path + "...";
      if (type.equals("gui")) setGuiCode(msg);
      else if (type.equals("java")) setJavaCode(msg);
      else if (type.equals("scss")) setScssCode(msg);
  }
}
