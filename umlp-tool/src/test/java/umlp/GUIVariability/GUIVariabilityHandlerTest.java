package umlp.GUIVariability;

import de.monticore.guidsl._ast.ASTGUIBlockContent;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GUIVariabilityHandlerTest extends AbstractGUIVariabilityTest {

    @BeforeEach
    public void resetLog(){
        Log.clearFindings();
    }

    public void checkGUIBlock(List<AbstractMap.SimpleEntry<ContentType, String>> expectedContent, List<ASTGUIBlockContent> actualContent){
        assertEquals(expectedContent.size(), actualContent.size());

        for (int i = 0; i < expectedContent.size(); i++) {
            switch (expectedContent.get(i).getKey()){
                case GUARD:
                    assertTrue(actualContent.get(i).isPresentGUIGuard());
                    assertEquals(actualContent.get(i).getGUIGuard()
                            .getId().getName(), expectedContent.get(i).getValue());
                    break;
                case ITERATE:
                    assertTrue(actualContent.get(i).isPresentGUIIterate());
                    assertEquals(actualContent.get(i).getGUIIterate()
                            .getId().getName(), expectedContent.get(i).getValue());
                    break;
            }
        }
    }

    @Test
    public void importTest(){
        ASTGUICompilationUnit baseAST = parseGUI("guivariability/BaseModel.gui");
        ASTGUICompilationUnit variationAST = parseGUI("guivariability/ImportTest.gui");

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertWarnings("0x45c0100");

        assertEquals(productAST.getMCPackageDeclaration().getMCQualifiedName().toString(), "guivariability");
        assertEquals(productAST.getComponentName(),"BaseModel");
        assertEquals(productAST.getMCImportStatementList().size(), 2);
        assertEquals(productAST.getMCImportStatementList().stream().filter(
                i -> i.getQName().equals("guivariability.Text") || i.getQName().equals("guivariability.Button")
        ).count(), 2);
    }

    @Test
    public void removeTest(){
        ASTGUICompilationUnit baseAST = parseGUI("guivariability/BaseModel.gui");
        ASTGUICompilationUnit variationAST = parseGUI("guivariability/RemoveTest.gui");

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertNoWarnings();

        assertEquals(productAST.getGUIComponentDeclaration().getGUIBlock()
                .getGUIBlockContentList().size(), 0);
    }

    @Test
    public void replaceTest(){
        List<AbstractMap.SimpleEntry<ContentType, String>> expectedValues = new ArrayList<>();

        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.GUARD, "newGuard"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.ITERATE, "newIterate"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent1"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent2"));

        ASTGUICompilationUnit baseAST = parseGUI("guivariability/BaseModel.gui");
        ASTGUICompilationUnit variationAST = parseGUI("guivariability/ReplaceTest.gui");

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertNoWarnings();

        List<ASTGUIBlockContent> pageContent = productAST.getGUIComponentDeclaration().getGUIBlock().getGUIBlockContentList();

        checkGUIBlock(expectedValues, pageContent);
    }

    @Test
    public void afterTest(){
        List<AbstractMap.SimpleEntry<ContentType, String>> expectedValues = new ArrayList<>();

        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "exampleText"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.GUARD, "newGuard"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.GUARD, "exampleGuard"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.ITERATE, "newIterate"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.ITERATE, "exampleIterate"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent1"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent2"));

        ASTGUICompilationUnit baseAST = parseGUI("guivariability/BaseModel.gui");
        ASTGUICompilationUnit variationAST = parseGUI("guivariability/AfterTest.gui");

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertNoWarnings();

        List<ASTGUIBlockContent> pageContent = productAST.getGUIComponentDeclaration().getGUIBlock().getGUIBlockContentList();

        checkGUIBlock(expectedValues, pageContent);
    }

    @Test
    public void beforeTest(){
        List<AbstractMap.SimpleEntry<ContentType, String>> expectedValues = new ArrayList<>();

        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.GUARD, "newGuard"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "exampleText"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.ITERATE, "newIterate"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.GUARD, "exampleGuard"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent1"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent2"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.ITERATE, "exampleIterate"));

        ASTGUICompilationUnit baseAST = parseGUI("guivariability/BaseModel.gui");
        ASTGUICompilationUnit variationAST = parseGUI("guivariability/BeforeTest.gui");

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertNoWarnings();

        List<ASTGUIBlockContent> pageContent = productAST.getGUIComponentDeclaration().getGUIBlock().getGUIBlockContentList();

        checkGUIBlock(expectedValues, pageContent);
    }
}
