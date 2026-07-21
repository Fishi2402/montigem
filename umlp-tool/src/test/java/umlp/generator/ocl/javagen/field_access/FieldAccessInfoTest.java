package umlp.generator.ocl.javagen.field_access;

import de.monticore.ocl.ocl.OCLMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.generator.ocl.NoRegexOCLTypeCheck3;
import umlp.generator.ocl.TypeUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FieldAccessInfoTest {

  @BeforeEach
  void initTypeCheck() {
    LogStub.init();
    Log.enableFailQuick(false);
    OCLMill.reset();
    OCLMill.init();
    NoRegexOCLTypeCheck3.init();
    TypeUtil.getInstance().init(List.of());
  }

  @AfterEach
  void resetTypeCheck() {
    NoRegexOCLTypeCheck3.reset();
  }

  @Test
  void usesProvidedExpressionTypeWithoutTypeCheckingQualifier() {
    TypeSymbol typeSymbol = new TypeSymbol("Eintragstyp");
    OCLMill.globalScope().add(typeSymbol);

    var qualifier = OCLMill.nameExpressionBuilder().setName("Eintragstyp").build();
    qualifier.setEnclosingScope(OCLMill.globalScope());
    var fieldAccess = OCLMill.fieldAccessExpressionBuilder()
        .setExpression(qualifier)
        .setName("PAUSE")
        .build();
    var qualifierType = SymTypeExpressionFactory.createTypeObject(typeSymbol);
    var expressionType = SymTypeExpressionFactory.createTypeObject(new TypeSymbol("FieldType"));

    FieldAccessInfo info = FieldAccessInfo.from(fieldAccess, qualifierType, expressionType);

    assertEquals(FieldAccessKind.SIMPLE_VALUE, info.getKind());
    assertSame(expressionType, info.getFieldType());
    assertEquals(0, Log.getErrorCount());
  }
}
