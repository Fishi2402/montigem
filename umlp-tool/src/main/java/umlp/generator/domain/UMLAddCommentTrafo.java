// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.domain;

import com.google.common.collect.Sets;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.types.MCTypeFacade;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.Collection;

/*
 * This transformation adds comment attributes
 *
 **/
public class UMLAddCommentTrafo implements CDBasisVisitor2 {

  protected CD4CodeTraverser traverser;

  public static final String WITH_COMMENT = "withComment";

  public static final String NO_COMMENT = "noComment";

  public static final String COMMENT_ATTR = "comment";

  protected boolean withComments;

  protected Collection<ASTCDType> commentMap;

  public UMLAddCommentTrafo(CD4CodeTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4CDBasis(this);
    commentMap = Sets.newHashSet();
  }

  protected boolean hasStereotype(ASTModifier modifier, String value) {
    if (modifier.isPresentStereotype()) {
      return modifier.getStereotype().contains(value);
    }
    return false;
  }

  @Override
  public void visit(ASTCDDefinition ast) {
    withComments = hasStereotype(ast.getModifier(), WITH_COMMENT);
  }

  @Override
  public void visit(ASTCDClass ast) {
    if (withComments && !ast.getModifier().isDerived() &&
        !hasStereotype(ast.getModifier(), NO_COMMENT) &&
        ast.getCDAttributeList().stream().noneMatch(a -> COMMENT_ATTR.equals(a.getName()))) {
      commentMap.add(ast);
    }
  }

  public void transform(ASTCDCompilationUnit ast) {
    ast.accept(this.traverser);
    commentMap.forEach(c -> {
      ASTCDAttribute attr = CDAttributeFacade.getInstance().createAttribute(
          CDModifier.PROTECTED.build(), MCTypeFacade.getInstance().createOptionalTypeOf("String"), COMMENT_ATTR);
      c.addCDMember(attr);
    });
  }

}
