package umlp.generator.ocl;

import com.google.common.base.Preconditions;
import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import umlp.generator.ocl.javagen.OCL2JavaCodeGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class AdaptedOCL2JavaGenerator {

  public static void generate(ASTOCLCompilationUnit ast, String outputFile) throws IOException {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(ast.getEnclosingScope());
    Preconditions.checkNotNull(outputFile);
    Preconditions.checkArgument(!outputFile.isEmpty());
    File output = Paths.get(outputFile).toFile();
    output.getParentFile().mkdirs();

    FileOutputStream fos = new FileOutputStream(output, false);
    fos.write(generate(ast).getBytes());
    fos.close();
  }

  public static String generate(ASTOCLCompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    return generate(ast, new IndentPrinter());
  }

  protected static String generate(ASTOCLCompilationUnit ast, IndentPrinter printer) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(printer);

    printer.println("/* (c) https://github.com/MontiCore/monticore */");
    ast.accept(new OCL2JavaCodeGenerator(printer).getTraverser());
    return printer.getContent();
  }
}
