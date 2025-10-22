import de.monticore.doc.PrintUtilForLaTex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.monticore.ast.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrintUtilForLaTexTest {

  private PrintUtilForLaTex printUtil;

  @BeforeEach
  void setUp() {
    printUtil = new PrintUtilForLaTex();
  }

  @Test
  void testPrintComment_NullInput_ReturnsEmptyString() {
    String result = printUtil.printComment(null);
    assertEquals("", result);
  }

  @Test
  void testPrintComment_EmptyList_ReturnsEmptyString() {
    String result = printUtil.printComment(List.of());
    assertEquals("", result);
  }

  @Test
  void testPrintComment_RemovesCommentSyntax() {
    Comment comment = new Comment("/** This is a test comment */");
    String result = printUtil.printComment(List.of(comment));
    assertTrue(result.contains("This is a test comment"));
    assertFalse(result.contains("/**"));
    assertFalse(result.contains("*/"));
  }

  @Test
  void testPrintComment_FormatsMarkdownEmphasis() {
    Comment comment = new Comment("This is *italic* and this is **bold**");
    String result = printUtil.printComment(List.of(comment));
    assertTrue(result.contains("\\textit{italic}"));
    assertTrue(result.contains("\\textbf{bold}"));
  }

  @Test
  void testPrintComment_FormatsInlineCode() {
    Comment comment = new Comment("Use `System.out.println()` to print.");
    String result = printUtil.printComment(List.of(comment));
    assertTrue(result.contains("\\texttt{System.out.println()}"));
  }

  @Test
  void testPrintComment_FormatsCodeBlock() {
    Comment comment = new Comment("Example:\n```java\nSystem.out.println(\"Hello\");\n```");
    String result = printUtil.printComment(List.of(comment));
    assertTrue(result.contains("\\begin{lstlisting}"));
    assertTrue(result.contains("System.out.println"));
    assertTrue(result.contains("\\end{lstlisting}"));
  }

  @Test
  void testPrintComment_FormatsHyperlink() {
    Comment comment = new Comment("Visit [Google](https://www.google.com)");
    String result = printUtil.printComment(List.of(comment));
    assertTrue(result.contains("\\url{https://www.google.com}{Google}"));
  }

  @Test
  void testPrintComment_FormatsBulletPoints() {
    Comment comment = new Comment("- Item one\n- Item two");
    String result = printUtil.printComment(List.of(comment));
    assertTrue(result.contains("\\begin{itemize}"));
    assertTrue(result.contains("\\item Item one"));
    assertTrue(result.contains("\\item Item two"));
    assertTrue(result.contains("\\end{itemize}"));
  }

  @Test
  void testPrintComment_EscapesSpecialLatexChars() {
    Comment comment = new Comment("Escape these: % $ & _ { } # ~ ^ \\");
    String result = printUtil.printComment(List.of(comment));
    assertTrue(result.contains("\\%"));
    assertTrue(result.contains("\\$"));
    assertTrue(result.contains("\\&"));
    assertTrue(result.contains("\\_"));
    assertTrue(result.contains("\\{"));
    assertTrue(result.contains("\\}"));
    assertTrue(result.contains("\\#"));
    assertTrue(result.contains("\\textasciitilde{}"));
    assertTrue(result.contains("\\textasciicircum{}"));
    assertTrue(result.contains("\\textbackslash{}"));
  }

  @Test
  void testEscapeForTexttt_EscapesAllSpecialChars() {
    String input = "% $ & _ { } # ~ ^ \\";
    String escaped = printUtil.escapeForTexttt(input);
    assertEquals("\\% \\$ \\& \\_ \\{ \\} \\# \\textasciitilde{} \\textasciicircum{} \\textbackslash{}", escaped);
  }
}

