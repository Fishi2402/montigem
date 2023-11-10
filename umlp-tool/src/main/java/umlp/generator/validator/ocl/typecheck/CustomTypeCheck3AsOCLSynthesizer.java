package umlp.generator.validator.ocl.typecheck;

import de.monticore.ocl.types.check.types3wrapper.TypeCheck3AsOCLSynthesizer;

import java.util.List;
import java.util.Map;

public class CustomTypeCheck3AsOCLSynthesizer extends TypeCheck3AsOCLSynthesizer {
  public CustomTypeCheck3AsOCLSynthesizer(Map<String, List<String>> enumMap) {
    super();
    this.typeTraverser = new CustomOCLTypeTraverserFactory(enumMap).createTraverser(type4Ast);
  }
}
