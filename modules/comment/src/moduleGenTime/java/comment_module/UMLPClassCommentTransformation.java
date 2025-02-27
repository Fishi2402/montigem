package comment_module;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.types.MCTypeFacade;
import umlp.cli.UMLPFilePipelineSetup;
import umlp.cli.common.IOperation;
import umlp.generator.Filter;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UMLPClassCommentTransformation implements IOperation<UMLPFilePipelineSetup.Data> {
  
  public UMLPClassCommentTransformation() {
    // TODO JRa: Remove
    System.out.println("[WOW] Comment module initialized in pipeline!");
  }
  
  @Override
  public boolean doWork(UMLPFilePipelineSetup.Data data) {
    // TODO JRa: Remove
    System.out.println("[WOW] Comment module did some work...");
    
    Predicate<Filter.Wrapper> filter = Filter.DERIVED.or(Filter.ABSTRACT).negate();
    List<ASTCDClass> domainClasses = data.getAST().getCDDefinition()
        .getCDClassesList()
        .stream()
        .filter(c -> filter.test(Filter.Wrapper.wrap(c)))
        .collect(Collectors.toList());
    
    domainClasses.forEach(this::createAttribute);
    return true;
  }
  
  protected void createAttribute(ASTCDClass cdclass) {
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(CDModifier.PUBLIC.build(), MCTypeFacade.getInstance().createListTypeOf("CommentModule.CommentModuleNote"),"comments");
    cdclass.addCDMember(attribute);
  }
  
}
