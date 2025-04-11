package de.monticore.dsl2cd;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * the methodBody might contain a few " too many, resulting in unparseable cds
 * <p>
 * We just replace any <<methodBody="fix_and_remove">>
 */
public class BrokenStereoFixReader extends Reader {
  final String regex = "<<methodBody=\"([\\s\\S]*?)\">>";

  private String replacedContent;
  private int currentPosition = 0;

  public BrokenStereoFixReader(Reader reader) throws IOException {
    StringBuilder content = new StringBuilder();
    try (BufferedReader bufferedReader = new BufferedReader(reader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line).append("\n");
      }
    }

    // Apply the regex replacement
    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL); // DOTALL allows matching across lines
    Matcher matcher = pattern.matcher(content);
    this.replacedContent = matcher.replaceAll("<<methodBody=\"fix_and_remove\">>");

  }

  @Override
  public int read(@Nonnull char[] cbuf, int off, int len) throws IOException {
    if (currentPosition >= replacedContent.length()) {
      return -1; // End of content
    }

    int charsToRead = Math.min(len, replacedContent.length() - currentPosition);
    replacedContent.getChars(currentPosition, currentPosition + charsToRead, cbuf, off);
    currentPosition += charsToRead;
    return charsToRead;
  }

  @Override
  public void close() {
    this.replacedContent = null;

  }

}
