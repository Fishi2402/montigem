package GUIDelta;

import de.monticore.guidsl._ast.ASTGUIBlockContent;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.gradle.internal.impldep.org.testng.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class DeltaHandlerTest extends AbstractGUIDeltaTest{

    @BeforeEach
    public void resetLog(){
        Log.clearFindings();
    }

    public void checkGUIBlock(List<AbstractMap.SimpleEntry<ContentType, String>> expectedContent, List<ASTGUIBlockContent> actualContent){
        Assert.assertEquals(expectedContent.size(), actualContent.size());

        for (int i = 0; i < expectedContent.size(); i++) {
            switch (expectedContent.get(i).getKey()){
                case GUARD:
                    Assert.assertTrue(actualContent.get(i).isPresentGUIGuard());
                    Assert.assertEquals(actualContent.get(i).getGUIGuard()
                            .getId().getName(), expectedContent.get(i).getValue());
                    break;
                case ITERATE:
                    Assert.assertTrue(actualContent.get(i).isPresentGUIIterate());
                    Assert.assertEquals(actualContent.get(i).getGUIIterate()
                            .getId().getName(), expectedContent.get(i).getValue());
                    break;
                case COMPONENT:
                    Assert.assertTrue(actualContent.get(i).isPresentGUIComponent());
                    Assert.assertEquals(actualContent.get(i).getGUIComponent()
                            .getId().getName(), expectedContent.get(i).getValue());
                    break;
            }
        }
    }

    @Test
    public void importTest(){
        ASTGUICompilationUnit baseAST = parseGUI(new File("src/test/resources/guidelta/BaseModel.gui"));
        ASTGUICompilationUnit variationAST = parseGUI(new File("src/test/resources/guidelta/ImportTest.gui"));

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertWarnings("0x45c0100");

        Assert.assertEquals(productAST.getMCPackageDeclaration().getMCQualifiedName().toString(), "guidelta");
        Assert.assertEquals(productAST.getComponentName(),"BaseModel");
        Assert.assertEquals(productAST.getMCImportStatementList().size(), 2);
        Assert.assertEquals(productAST.getMCImportStatementList().stream().filter(
                i -> i.getQName().equals("guidelta.Text") || i.getQName().equals("guidelta.Button")
        ).count(), 2);
    }

    @Test
    public void removeTest(){
        ASTGUICompilationUnit baseAST = parseGUI(new File("src/test/resources/guidelta/BaseModel.gui"));
        ASTGUICompilationUnit variationAST = parseGUI(new File("src/test/resources/guidelta/RemoveTest.gui"));

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertNoWarnings();

        Assert.assertEquals(productAST.getGUIComponentDeclaration().getGUIBlock()
                .getGUIBlockContentList().size(), 0);
    }

    @Test
    public void replaceTest(){
        List<AbstractMap.SimpleEntry<ContentType, String>> expectedValues = new ArrayList<>();

        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.GUARD, "newGuard"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.ITERATE, "newIterate"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent1"));
        expectedValues.add(new AbstractMap.SimpleEntry<>(ContentType.COMPONENT, "newComponent2"));

        ASTGUICompilationUnit baseAST = parseGUI(new File("src/test/resources/guidelta/BaseModel.gui"));
        ASTGUICompilationUnit variationAST = parseGUI(new File("src/test/resources/guidelta/ReplaceTest.gui"));

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

        ASTGUICompilationUnit baseAST = parseGUI(new File("src/test/resources/guidelta/BaseModel.gui"));
        ASTGUICompilationUnit variationAST = parseGUI(new File("src/test/resources/guidelta/AfterTest.gui"));

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

        ASTGUICompilationUnit baseAST = parseGUI(new File("src/test/resources/guidelta/BaseModel.gui"));
        ASTGUICompilationUnit variationAST = parseGUI(new File("src/test/resources/guidelta/BeforeTest.gui"));

        ASTGUICompilationUnit productAST = handleGUIDelta(variationAST, baseAST);

        assertNoErrors();
        assertNoWarnings();

        List<ASTGUIBlockContent> pageContent = productAST.getGUIComponentDeclaration().getGUIBlock().getGUIBlockContentList();

        checkGUIBlock(expectedValues, pageContent);
    }
}
