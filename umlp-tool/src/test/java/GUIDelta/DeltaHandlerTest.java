package GUIDelta;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.gradle.internal.impldep.org.testng.Assert;
import org.junit.jupiter.api.Test;
import umlp.UMLPMill;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DeltaHandlerTest extends AbstractGUIDeltaTest{

    @Test
    public void importTest(){
        ASTGUICompilationUnit baseAST = parseGUI(new File("../resources/guidelta/BaseModel.gui"));
        ASTGUICompilationUnit variationAST = parseGUI(new File("../resources/guidelta/ImportTest.gui"));

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertWarnings("0x45c0100");

        Assert.assertEquals(productAST.getMCImportStatementList().size(), 2);
        Assert.assertEquals(productAST.getMCImportStatementList().stream().filter(
                i -> i.getQName().equals("guidelta.Text") || i.getQName().equals("guidelta.Button;")
        ).count(), 2);
    }
}
