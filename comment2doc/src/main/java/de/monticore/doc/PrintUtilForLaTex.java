package de.monticore.doc;

import de.monticore.ast.Comment;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._parser.GUIDSLParser;
import de.monticore.guidsl._prettyprint.GUIDSLExamplePrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.commonmark.node.Code;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.Renderer;

import java.io.IOException;
import java.util.List;

/**
 * Enhanced version of PrintUtil with LaTeX formatting capabilities using CommonMark.
 * Handles both inline and block comments, special characters, hyperlinks,
 * bullet points, and other formatting elements.
 */
public class PrintUtilForLaTex extends PrintUtil {

  private final Parser parser;
  private final LaTeXRenderer renderer;

  public PrintUtilForLaTex() {
    this.parser = Parser.builder().build();
    this.renderer = new LaTeXRenderer();
  }

  /**
   * Converts comments to properly formatted LaTeX,
   * handling special characters, hyperlinks, bullet points, and other formatting elements.
   *
   * @param commentList List of comments to format
   * @return LaTeX-formatted comment string
   */
  @Override
  public String printComment(List<Comment> commentList) {
    if (commentList == null || commentList.isEmpty()) {
      return "";
    }

    StringBuilder comment = new StringBuilder();
    for (Comment c : commentList) {
      String text = c.getText();
      String processedText = processCommentText(text);
      comment.append(processedText);
    }

    String result = comment.toString().trim();
    return formatCommentForLatex(result);
  }

  /**
   * Processes raw comment text to handle different comment types and preserve links.
   */
  private String processCommentText(String text) {
    if (text == null || text.trim().isEmpty()) {
      return "";
    }

    if (text.trim().startsWith("//")) {
      return processInlineComment(text);
    }
    else if (text.contains("/**") || text.contains("/*")) {
      return processBlockComment(text);
    }
    else {
      // Already processed text, just clean up
      return cleanupCommentText(text);
    }
  }

  /**
   * Processes inline comments (starting with //).
   */
  private String processInlineComment(String text) {
    String[] lines = text.split("\n");
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i].trim();

      if (line.startsWith("//")) {
        // Remove the // and any immediately following whitespace
        line = line.replaceFirst("^//\\s?", "");

        result.append(line);
      }
      else {
        // Non-comment line or already processed
        result.append(line);
      }

      if (i < lines.length - 1) {
        result.append("\n");
      }
    }

    return result.toString();
  }

  /**
   * Processes block comments and remove asterisks and white spaces.
   */
  private String processBlockComment(String text) {
    // Remove comment delimiters
    String cleaned = text
        .replace("/**", "").replace("*/", "")
        .replace("/*", "");

    String[] lines = cleaned.split("\n");
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      if (line.trim().startsWith("*") && !line.trim().startsWith("**")) {
        // Asterisk removal
        line = line.replaceFirst("^\\s*\\*\\s?", "");
      }
      else {
        // Just remove leading whitespace for lines that don't start with *
        line = line.replaceFirst("^\\s*", "");
      }

      result.append(line);
      if (i < lines.length - 1) {
        result.append("\n");
      }
    }

    return result.toString();
  }

  /**
   * Basic cleanup for already processed comment text.
   */
  private String cleanupCommentText(String text) {
    return text.replaceAll("(?m)^\\s*/\\*\\s*(\\(c\\).*)\\*/?", "// $1");
  }

  /**
   * Formats a comment string for LaTeX output using CommonMark parser.
   */
  private String formatCommentForLatex(String comment) {
    if (comment == null || comment.trim().isEmpty()) {
      return "";
    }

    // Step 1: Use preprocessor to handle custom formats
    String preprocessed = preprocessText(comment);

    // Step 2: Parse the preprocessed text using CommonMark
    // Parse the markdown content
    Node document = parser.parse(preprocessed);

    // Step 3: Render the CommonMark AST to LaTeX
    // Render the document to LaTeX format
    return renderer.render(document).trim();
  }

  /**
   * Custom LaTeX renderer for CommonMark AST nodes.
   */
  private class LaTeXRenderer implements Renderer {

    @Override
    public void render(Node node, Appendable output) {
      try {
        StringBuilder sb = new StringBuilder();
        renderNode(node, sb);
        output.append(sb.toString());
      } catch (java.io.IOException e) {
        throw new RuntimeException("Error rendering to LaTeX", e);
      }
    }

    public String render(Node node) {
      StringBuilder sb = new StringBuilder();
      renderNode(node, sb);
      return sb.toString();
    }

    private void renderNode(Node node, StringBuilder sb) {
      switch (node.getClass().getSimpleName()) {
        case "Document":
          renderChildren(node, sb);
          break;

        case "Paragraph":
          renderChildren(node, sb);
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "Text":
          String text = ((Text) node).getLiteral();
          sb.append(escapeForTexttt(text));
          break;

        case "Emphasis":
          sb.append("\\textit{");
          renderChildren(node, sb);
          sb.append("}");
          break;

        case "StrongEmphasis":
          sb.append("\\textbf{");
          renderChildren(node, sb);
          sb.append("}");
          break;

        case "Code":
          String code = ((Code) node).getLiteral();
          sb.append("\\texttt{").append(escapeForTexttt(code)).append("}");
          break;

        case "FencedCodeBlock":
          FencedCodeBlock fencedCode = (FencedCodeBlock) node;
          String language = fencedCode.getInfo() != null ? fencedCode.getInfo() : "Java";
          sb.append("\\begin{lstlisting}[language=").append(language).append("]\n");
          sb.append(fencedCode.getLiteral());
          sb.append("\\end{lstlisting}");
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "IndentedCodeBlock":
          IndentedCodeBlock indentedCode = (IndentedCodeBlock) node;
          sb.append("\\begin{lstlisting}[language=Java]\n");
          sb.append(indentedCode.getLiteral());
          sb.append("\\end{lstlisting}");
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "Link":
          Link link = (Link) node;
          sb.append("\\url{").append(escapeForTexttt(link.getDestination())).append("}{");
          renderChildren(node, sb);
          sb.append("}");
          break;

        case "BulletList":
          sb.append("\\begin{itemize}\n");
          renderChildren(node, sb);
          sb.append("\\end{itemize}");
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "OrderedList":
          sb.append("\\begin{enumerate}\n");
          renderChildren(node, sb);
          sb.append("\\end{enumerate}");
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "ListItem":
          sb.append("\\item ");
          renderChildren(node, sb);
          sb.append("\n");
          break;

        case "HtmlBlock":
        case "HtmlInline":
          // No HTML support, skip these nodes
          break;

        case "BlockQuote":
          sb.append("\\begin{quote}\n");
          renderChildren(node, sb);
          sb.append("\\end{quote}");
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "Heading":
          Heading heading = (Heading) node;
          String headingCommand = getHeadingCommand(heading.getLevel());
          sb.append(headingCommand).append("{");
          renderChildren(node, sb);
          sb.append("}");
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "ThematicBreak":
          sb.append("\\noindent\\rule{\\textwidth}{0.4pt}");
          if (node.getNext() != null) {
            sb.append("\n\n");
          }
          break;

        case "LineBreak":
          sb.append("\\\\");
          break;

        case "SoftLineBreak":
          sb.append(" ");
          break;

        default:
          // For any unhandled node types, render children
          renderChildren(node, sb);
          break;
      }
    }

    private void renderChildren(Node parent, StringBuilder sb) {
      Node child = parent.getFirstChild();
      while (child != null) {
        renderNode(child, sb);
        child = child.getNext();
      }
    }

    private String getHeadingCommand(int level) {
      switch (level) {
        case 1:
          return "\\section";
        case 2:
          return "\\subsection";
        case 3:
          return "\\subsubsection";
        case 4:
          return "\\paragraph";
        case 5:
          return "\\subparagraph";
        default:
          return "\\textbf"; // Fallback
      }
    }
  }

  /**
   * Pre-process text to handle custom formats and tables before CommonMark parsing.
   * Enhanced preprocessing that better handles different comment types as CommonMark does not recognize them.
   */
  private String preprocessText(String text) {
    // First handle colon-based lists
    text = convertColonListsToMarkdown(text);

    // Convert bare URLs to proper Markdown autolinks
    text = convertBareUrlsToAutolinks(text);

    // Then handle any remaining inline comment patterns that might interfere with markdown
    text = normalizeInlineCommentPatterns(text);

    return text;
  }

  /**
   * Converts bare URLs to Markdown autolink format - CommonMark cannot recognize them otherwise.
   */
  private String convertBareUrlsToAutolinks(String text) {
    // Pattern to match URLs that aren't already in Markdown link format
    // Looks for URLs that aren't preceded by ]( or wrapped in <>
    String urlPattern = "(?<!]\\()(?<!<)(https?://[^\\s)\\]>]+)(?!>)";

    return text.replaceAll(urlPattern, "<$1>");
  }

  /**
   * Normalizes remaining inline comment patterns
   */
  private String normalizeInlineCommentPatterns(String text) {
    String[] lines = text.split("\n");
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      result.append(line);
      if (i < lines.length - 1) {
        result.append("\n");
      }
    }

    return result.toString();
  }

  /**
   * Converts colon-based lists to proper Markdown bullet lists.
   * Handles patterns like: `"text"`: Accept any input string.
   */
  private String convertColonListsToMarkdown(String text) {
    String[] lines = text.split("\n");
    StringBuilder result = new StringBuilder();
    boolean inColonList = false;

    for (String line : lines) {
      String trimmedLine = line.trim();

      // Check if we're starting a colon-based list section
      if (trimmedLine.endsWith(":") && !trimmedLine.startsWith("-") && !trimmedLine.startsWith("*")) {
        result.append(line).append("\n");
        inColonList = true;
        continue;
      }

      // Check if line matches the pattern `"something"`: description
      if (trimmedLine.matches("^`\"[^\"]+\"`:\\s+.*")) {
        // Convert to bullet list format
        result.append("- ").append(trimmedLine).append("\n");
        inColonList = true;
      }
      else if (trimmedLine.matches("^\"[^\"]+\":\\s+.*")) {
        // Handle unquoted version: "something": description -> - `"something"`: description
        result.append("- `").append(trimmedLine).append("`\n");
        inColonList = true;
      }
      else if (inColonList && trimmedLine.isEmpty()) {
        // Empty line ends the list
        result.append(line).append("\n");
        inColonList = false;
      }
      else if (inColonList &&
          !trimmedLine.matches("^[-*+]\\s+.*") &&
          !trimmedLine.matches("^\\d+\\.\\s+.*")) {
        // If we're in a colon list and this doesn't look like a standard list item,
        // it is a continuation or a new paragraph
        result.append(line).append("\n");
        inColonList = false;
      }
      else {
        result.append(line).append("\n");
        if (!trimmedLine.isEmpty() && !trimmedLine.matches("^[-*+]\\s+.*") &&
            !trimmedLine.matches("^\\d+\\.\\s+.*")) {
          inColonList = false;
        }
      }
    }

    return result.toString();
  }

  /**
   * Escapes text for use in LaTeX \texttt{} commands.
   * This should be used for code, default values, types.
   */
  public String escapeForTexttt(String text) {
    if (text == null) {
      return "";
    }
    // Use a unique placeholder for backslashes to avoid conflicts
    String placeholder = "<<<BACKSLASH-PLACEHOLDER>>>";

    return text
        .replace("\\", placeholder)  // Replace backslashes with placeholder first
        .replace("{", "\\{")
        .replace("}", "\\}")
        .replace("%", "\\%")
        .replace("^", "\\textasciicircum{}")
        .replace("~", "\\textasciitilde{}")
        .replace("#", "\\#")
        .replace("&", "\\&")
        .replace("$", "\\$")
        .replace("_", "\\_")
        .replace(placeholder, "\\textbackslash{}");  // Replace placeholder with LaTeX command last
  }

  /**
   * Wrap text in \texttt{} with proper escaping.
   */
  public String printAsTexttt(String text) {
    if (text == null || text.trim().isEmpty()) {
      return "";
    }
    return "\\texttt{" + escapeForTexttt(text) + "}";
  }


  /**
   * Removes comments from an example model using the GUIDSLExamplePrettyPrinter and remove any empty lines/spaces.
   */
  public static String removeAllComments(String content) throws IOException {
    GUIDSLParser parser = new GUIDSLParser();
    ASTGUICompilationUnit ast = parser.parse_String(content).orElseThrow();

    GUIDSLExamplePrettyPrinter printer = new GUIDSLExamplePrettyPrinter(new IndentPrinter(), false);
    List<Comment> allComments = printer.collectAllComments(ast);

    String result = content;

    for (Comment s : allComments) {
      result = result.replace(s.getText(), "");
    }

    // Remove trailing whitespace and empty lines
    result = result.replaceFirst("^\\s*\\n+", "");
    result = result.replaceAll("(?m)^\\s+$", "");
    result = result.replaceAll("\\n{3,}", "\n\n");

    return result;
  }
}
