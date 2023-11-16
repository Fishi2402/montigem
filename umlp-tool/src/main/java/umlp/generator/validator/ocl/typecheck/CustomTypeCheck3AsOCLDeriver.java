package umlp.generator.validator.ocl.typecheck;

import de.monticore.ocl.types.check.types3wrapper.TypeCheck3AsOCLDeriver;

import java.util.List;
import java.util.Map;

public class CustomTypeCheck3AsOCLDeriver extends TypeCheck3AsOCLDeriver {
  public CustomTypeCheck3AsOCLDeriver(Map<String, List<String>> enumMap) {
    super();
    this.typeTraverser = new CustomOCLTypeTraverserFactory(enumMap).createTraverser(type4Ast);
  }
}
