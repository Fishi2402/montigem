package de.monticore.doc;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.guidsl.GUIDSLMill;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.util.List;

public class PrintUtil {

  public String print(ASTNode node) {
    return GUIDSLMill.prettyPrint(node, false);
  }

  public String printComment(List<Comment> commentList) {
    StringBuilder comment = new StringBuilder();
    for (Comment c : commentList) {
      String formatted = c.getText()
              .replace("/**", "").replace("*/", "")
              .replaceAll("\\s*\\n\\s*", " ");
      comment.append(formatted);
    }
    return comment.toString();
  }

  public String printPath(String qName) {
    return Names.getPathFromPackage(qName).replace("\\", "/");
  }

  public String printRelativePath(Path from, Path to) {
    return "./" + from.getParent().relativize(to).toString().replace("\\", "/");
  }

}
