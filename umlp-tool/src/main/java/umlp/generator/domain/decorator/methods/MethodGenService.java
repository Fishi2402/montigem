package umlp.generator.domain.decorator.methods;

import de.monticore.cd.codegen.CDGenService;
import umlp.generator.domain.decorator.GenService;

public class MethodGenService extends CDGenService {
  @Override
  public String getNativeAttributeName(String attributeName) {
    return GenService.getNativeName(attributeName);
  }
}
