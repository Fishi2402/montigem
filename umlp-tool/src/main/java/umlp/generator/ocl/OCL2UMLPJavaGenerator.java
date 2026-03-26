package umlp.generator.ocl;

import com.google.common.base.Preconditions;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import umlp.generator.ocl.javagen.OCL2UMLPJavaCodeGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class OCL2UMLPJavaGenerator {

  public static void generate(ASTOCLCompilationUnit ast, String outputFile) throws IOException {
    generate(ast, null, outputFile);
  }

  public static void generate(ASTOCLCompilationUnit ast, String fullPackage, String outputFile) throws IOException {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(ast.getEnclosingScope());
    Preconditions.checkNotNull(outputFile);
    Preconditions.checkArgument(!outputFile.isEmpty());
    File output = Paths.get(outputFile).toFile();
    output.getParentFile().mkdirs();

    FileOutputStream fos = new FileOutputStream(output, false);
    fos.write(generateCode(ast, fullPackage).getBytes());
    fos.close();
  }

  public static String generateCode(ASTOCLCompilationUnit ast) {
    return generateCode(ast, (String) null);
  }

  public static String generateCode(ASTOCLCompilationUnit ast, String fullPackage) {
    Preconditions.checkNotNull(ast);
    return generateCode(ast, fullPackage, new IndentPrinter());
  }

  protected static String generateCode(ASTOCLCompilationUnit ast, String fullPackage, IndentPrinter printer) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(printer);

    printer.println("/* (c) https://github.com/MontiCore/monticore */");
    ast.accept(new OCL2UMLPJavaCodeGenerator(printer, fullPackage).getTraverser());
    return printer.getContent();
  }
}
