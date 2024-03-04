package umlp.generator.gui.coco;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._symboltable.IGUIDSLScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._cocos.MCCollectionTypesASTMCListTypeCoCo;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import umlp.generator.util.TagLookUp;

import java.util.Optional;

/**
 * Prohibits lists of singleton types
 */
public class GuiNoSingletonList implements MCCollectionTypesASTMCListTypeCoCo {
  @Override
  public void check(ASTMCListType astmcListType) {
    Optional<ASTMCType> typeOpt = astmcListType.getMCTypeArgument().getMCTypeOpt();
    if (typeOpt.isEmpty() || !(typeOpt.get() instanceof ASTMCQualifiedType)) return;
    ASTMCQualifiedType astmcQualifiedType = (ASTMCQualifiedType) typeOpt.get();
    IGUIDSLScope guiScope = (IGUIDSLScope) astmcListType.getEnclosingScope();
    Optional<TypeSymbol> ts = guiScope.resolveType(Joiners.DOT.join(astmcQualifiedType.getNameList()));
    if (ts.isPresent()) {
      if (TagLookUp.isSingleton(ts.get())) {
        Log.error("0xTODO: " + GUIDSLMill.prettyPrint(astmcListType, false) + " is a list of a singleton type", astmcListType.get_SourcePositionStart());
      }
    }
  }
}
