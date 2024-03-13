package umlp.generator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.umlmodifier._ast.ASTModifier;
import umlp.generator.domain.decorator.GenService;

import java.util.function.Predicate;

/**
 * Filters which can be used with {@link AbstractDecorator#getDomainClasses(ASTCDCompilationUnit, Predicate)}
 */
public interface Filter {
  Predicate<Wrapper> DERIVED = c -> c.getSymbol().isIsDerived();
  Predicate<Wrapper> ABSTRACT = c -> c.getModifier().isAbstract();
  Predicate<Wrapper> SINGLETON = c -> GenService.hasStereotype(c.getModifier(), "singleton");

  //bundles the properties of
  interface Wrapper {
    CDTypeSymbol getSymbol();
    ASTModifier getModifier();

    static Wrapper wrap(ASTCDClass type) {
      return new Wrapper() {
        @Override
        public CDTypeSymbol getSymbol() {
          return type.getSymbol();
        }

        @Override
        public ASTModifier getModifier() {
          return type.getModifier();
        }
      };
    }

    static Wrapper wrap(ASTCDInterface type) {
      return new Wrapper() {
        @Override
        public CDTypeSymbol getSymbol() {
          return type.getSymbol();
        }

        @Override
        public ASTModifier getModifier() {
          return type.getModifier();
        }
      };
    }
  }
}
