package umlp.backendrte.ocl;

import de.monticore.ocl.ocl.OCLMill;
import de.monticore.ocl.ocl._parser.OCLParser;
import de.monticore.ocl.ocl._visitor.OCLTraverser;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetExpressionsNode;
import de.monticore.ocl.setexpressions._cocos.SetExpressionsCoCoChecker;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Optional;

public class OCL2SQL implements SetExpressionsVisitor2 {

  protected static OCL2SQL instance;
  protected OCLTraverser traverser;

  private OCL2SQL() {
    traverser = OCLMill.traverser();
    traverser.add4SetExpressions(this);
  }

  public static void init() {
    OCLMill.init();
    instance = new OCL2SQL();
  }

  public static OCL2SQL getInstance() {
    return instance;
  }

  public String convert(String listComprehension) throws IOException, OCLException {
    Log.clearFindings();

    OCLParser parser = OCLMill.parser();
    Optional<ASTSetComprehension> ast = parser.parse_StringSetComprehension(listComprehension);

    if (ast.isPresent()) {
      SetExpressionsCoCoChecker checker = new SetExpressionsCoCoChecker();
      checker.addCoCo(new OnlySimpleComprehensions());
      checker.checkAll((ASTSetExpressionsNode) ast.get());
      if (Log.getFindingsCount() > 0) {
        throw new OCLException(getLogFindings());
      }

      return new OCL2SQLPrinter().getQuery(ast.get());
    }
    else {
      throw new OCLException("0xB0000"
          + " OCL parser failed to process the list comprehension:\n" + listComprehension
          + "\n" + getLogFindings()
      );
    }
  }

  protected String getLogFindings() {
    StringBuilder findings = new StringBuilder();
    for (Finding f : Log.getFindings()) {
      findings.append(f.getMsg()).append("\n");
    }
    return findings.toString();
  }

}