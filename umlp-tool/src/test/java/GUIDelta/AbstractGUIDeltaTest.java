package GUIDelta;

import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPTool;
import umlp.generator.gui.guidelta.GUIDeltaHandler;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

abstract public class AbstractGUIDeltaTest {

    public static ASTGUICompilationUnit parseGUI(File model){
        return UMLPTool.parseGUI(model.getPath());
    }

    public static ASTGUICompilationUnit handleGUIDelta(ASTGUICompilationUnit deltaAST, ASTGUICompilationUnit adaptedAST){
        GUIDeltaHandler handler = new GUIDeltaHandler(adaptedAST);
        deltaAST.accept(handler.getTraverser());
        return adaptedAST;
    }

    public static void assertNoErrors() {
        if (Log.getErrorCount() > 0) {
            System.err.println("Expected no errors, but got: ");
            for (Finding f : Log.getFindings()) {
                if (f.isError()) {
                    System.err.println(f);
                }
            }
            fail();
        }
    }

    public static void assertErrors(String... errorCodes) {
        assertEquals(errorCodes.length, Log.getErrorCount(),
                "Expected and actual number of errors differ!");

        for (String expectedErrorCode : errorCodes) {
            boolean foundError = false;
            for (Finding f : Log.getFindings()) {
                if (f.isError() && f.getMsg().startsWith(expectedErrorCode)) {
                    foundError = true;
                    break;
                }
            }
            if (!foundError) {
                fail("Expected to find an error with the error code '" +
                        expectedErrorCode + "', but the error did not occur!");
            }
        }
    }

    public static void assertWarnings(String... warnCodes) {
        assertEquals(warnCodes.length, Log.getFindings().stream().filter(Finding::isWarning).count(),
                "Expected and actual number of warnings differ!");

        for (String expectedWarnCode : warnCodes) {
            boolean foundError = false;
            for (Finding f : Log.getFindings()) {
                if (f.isWarning() && f.getMsg().startsWith(expectedWarnCode)) {
                    foundError = true;
                    break;
                }
            }
            if (!foundError) {
                fail("Expected to find an warning with the warning code '" +
                        expectedWarnCode + "', but the warning did not occur!");
            }
        }
    }
}
