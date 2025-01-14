package de.monticore.dsl2cd;

import de.monticore.ast.Comment;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.ForgivingParser;
import de.monticore.cd4codebasis._ast.ASTCDInterface;
import de.monticore.cdassociation._ast.ASTCDCardMult;
import de.monticore.cdassociation._ast.ASTCDCardOpt;
import de.monticore.cdassociation._ast.ASTCDCardinality;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This entire class is (as of now) just a messy write-down
 */
public class DSL2CD {

  static Stack<ASTCDType> requestedClasses = new Stack<>();
  static Map<String, String> shortNameMap = new HashMap<>();

  public static void main(String[] args) throws Exception {
    for (String x : args)
      System.err.println(x);

    String domainName = args[0];

    if (!"domain".equals(domainName))
      throw new IllegalStateException("As of now, only domain is supported for the domain name: " + domainName);


    File reporting = new File(args[1]);

    if (!reporting.exists()) {
      throw new IllegalStateException("Could not find input file: " + reporting.getAbsolutePath() + " - ?!?");
    }

    File domainCDFile = new File(args[2]);
    File javaOut = new File(args[3]);
    File additional = new File(args[4]);

    CD4CodeMill.init();
    Log.initWARN();

    GeneratorSetup genSetup = new GeneratorSetup();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    genSetup.setGlex(glex);
    CD4C.getInstance().setEmptyBodyTemplate(CD2JavaTemplates.EMPTY_BODY);
    genSetup.setOutputDirectory(javaOut);
    CD4C.init(genSetup);

    var parser = new ForgivingParser();

    var astOpt = parser.parse(reporting.getAbsolutePath());
    if (parser.hasErrors() || astOpt.isEmpty()) {
      throw new IllegalStateException("Could not parse file: " + reporting.getAbsolutePath());
    }
    var mainAS = CD4CodeMill.scopesGenitorDelegator().createFromAST(astOpt.get());
    List<String> dslsToLoad = ClassSearcher.lookForPackagesOfMills(astOpt.get());

    // Load additional CDs
    List<ASTCDCompilationUnit> cds = loadAdditionalCDs(additional, parser, dslsToLoad);
    System.err.println("Loaded " + cds.size() + " other CDs in a weird-implicit way (via adapted symbol resolvers)");

    var umlpDef = CD4CodeMill.cDDefinitionBuilder().setName(astOpt.get().getCDDefinition().getName()).setModifier(astOpt.get().getCDDefinition().getModifier()).build();

    var traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDBasis(new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (node.isPresentCDExtendUsage() && !node.getCDExtendUsage().getSuperclassList().isEmpty() && node.getCDExtendUsage().getSuperclass(0).printType().equals("ASTCNode")) {
          requestedClasses.add(node);
        }
      }
    });
    astOpt.get().accept(traverser);

    cds.add(astOpt.get());

    var dsl2domainClass = CD4CodeMill.cDClassBuilder().setName("DSL2Domain").setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();

    // Prepare dsl2domainClass
    CD4C.getInstance().addImport(dsl2domainClass, ITraverser.class.getName());
    CD4C.getInstance().addAttribute(dsl2domainClass, "protected ITraverser traverser");
    CD4C.getInstance().addAttribute(dsl2domainClass, "public Stack<Object> stack");

    CD4C.getInstance().addConstructor(dsl2domainClass, "templates.dsl2domain.Constructor");

    // Use the stack of classes to create, such that we only create the required tree
    Set<String> createdClasses = new HashSet<>();
    Set<String> grammarsUsed = new HashSet<>();
    while (!requestedClasses.isEmpty()) {
      var rc = requestedClasses.pop();
      String clazzname = sanitizeTypeName(rc.getSymbol().getFullName());

      grammarsUsed.add(rc.getSymbol().getPackageName());

      clazzname = StringTransformations.capitalize(clazzname);

      if (createdClasses.contains(clazzname)) continue;
      createdClasses.add(clazzname);

      if (rc instanceof ASTCDClass) {
        umlpDef.addCDElement(handleClass((ASTCDClass) rc, mainAS, dsl2domainClass, glex));
      } else if (rc instanceof ASTCDInterface) {
        umlpDef.addCDElement(handleInterface((ASTCDInterface) rc, mainAS));
        // Look for all implementing
        lookForImplementing((ASTCDInterface) rc, cds);
      } else {
        Log.warn("Unhandled type " + rc + " " + rc.getName());
      }

    }

    // Next, we add the Handler
    var handlers = ClassSearcher.lookForHandlers(cds.stream().filter(c -> grammarsUsed.contains(c.getCDDefinition().getSymbol().getFullName())).collect(Collectors.toList()));
    var traversers = ClassSearcher.lookForTraversers(cds.stream().filter(c -> grammarsUsed.contains(c.getCDDefinition().getSymbol().getFullName())).collect(Collectors.toList()));

    dsl2domainClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().uncheckedBuild());
    for (var handler : handlers) {
      dsl2domainClass.getCDInterfaceUsage().addInterface(CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(Splitters.DOT.splitToList(simplifyName(handler.getSymbol()))).build()).build());
    }
    for (var t : traversers) {
      CD4C.getInstance().addMethod(dsl2domainClass, "templates.dsl2domain.setTraverser", simplifyName(t.getSymbol()));
    }
    CD4C.getInstance().addMethod(dsl2domainClass, "templates.dsl2domain.getTraverser", simplifyName(ClassSearcher.lookForTraversers(List.of(astOpt.get())).get(0).getSymbol()));

    var umlp = CD4CodeMill.cDCompilationUnitBuilder().setCDDefinition(umlpDef).build();


    if (domainCDFile.exists()) {
      domainCDFile.delete();
    }
    Files.write(domainCDFile.toPath(), CD4CodeMill.prettyPrint(umlp, true).getBytes(), StandardOpenOption.CREATE);

    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    GeneratorEngine generatorEngine = new GeneratorEngine(genSetup);
    generatorEngine.generate(CD2JavaTemplates.CLASS, new File("dsl2cd/DSL2Domain.java").toPath(), dsl2domainClass, CD4CodeMill.cDPackageBuilder().setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(Arrays.asList("dsl2cd")).build()).build());
  }

  private static List<ASTCDCompilationUnit> loadAdditionalCDs(File additional, ForgivingParser parser, List<String> dslsToLoad) throws IOException {
    List<ASTCDCompilationUnit> cds = new ArrayList<>();
    Files.walkFileTree(additional.toPath(), new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().endsWith(".cd")) {


          var astOpt = parser.parse(new BrokenStereoFixReader(Files.newBufferedReader(file.toAbsolutePath())));
          if (parser.hasErrors() || astOpt.isEmpty()) {
            throw new IllegalStateException("Could not parse file: " + file);
          }
          String x = astOpt.get().getCDPackageList().isEmpty() ? astOpt.get().getCDDefinition().getName() : Joiners.DOT.join(astOpt.get().getCDPackageList()) + "." + astOpt.get().getCDDefinition().getName();

          if (!dslsToLoad.contains(x.toLowerCase())) {
            return FileVisitResult.CONTINUE;
          }

          cds.add(astOpt.get());
          var as = CD4CodeMill.scopesGenitorDelegator().createFromAST(astOpt.get());

          // Somewhat cursed "imports" to other CDs
          CD4CodeMill.globalScope().addAdaptedCDTypeSymbolResolver(new AdaptedICDTypeSymbolResolver(as));

        }
        return FileVisitResult.CONTINUE;
      }
    });
    return cds;
  }

  static List<String> primitives = Arrays.asList("String", "boolean", "int", "long", "short", "double", "float");

  protected static String getUniqueShortName(String name) {
    if (shortNameMap.containsKey(name.toLowerCase())) {
      return shortNameMap.get(name.toLowerCase());
    } else {
      String shortname = name.substring(name.lastIndexOf('.') + 1);
      if (!shortNameMap.containsValue(shortname)) {
        shortNameMap.put(name.toLowerCase(), shortname);
        return shortname;
      }
      shortname = shortname + "X";
      if (!shortNameMap.containsValue(shortname)) {
        shortNameMap.put(name.toLowerCase(), shortname);
        return shortname;
      }
    }
    throw new IllegalStateException("unable to create shortname");
  }

  protected static ASTCDClass handleClass(ASTCDClass astClass, ICD4AnalysisArtifactScope as, ASTCDClass dsl2domainClass, GlobalExtensionManagement glex) {
    String name = sanitizeTypeName(astClass.getSymbol().getFullName());
    name = getUniqueShortName(name);

    var clazz = CD4CodeMill.cDClassBuilder().setName(name).setModifier(astClass.getModifier()).build();

    if (astClass.isPresentCDInterfaceUsage()) {

      clazz.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().uncheckedBuild());
      for (var interf : astClass.getCDInterfaceUsage().getInterfaceList()) {
        String iname = interf.printType();
        if (iname.endsWith("Node")) continue; // TODO: Better Exclude _ast.AST${dsl}Node interface
        if (iname.contains("._ast.AST")) {
          System.err.println("interface: " + clazz.getName());
          System.err.println(interf.printType());

          var sym = as.resolveCDTypeMany(iname);
          if (!sym.isEmpty()) {
            var type = sym.get(0);
            String typeName = getUniqueShortName(sanitizeTypeName(type.getFullName()));
            requestedClasses.add(type.getAstNode());

            clazz.getCDInterfaceUsage().addInterface(typeFromString(typeName));
          } else {
            Log.warn("Unable to find type " + iname);
          }

        } else {
          Log.warn("Unhandled implements type " + iname + " of " + clazz.getName());
        }
      }
    }

    // astnode", "nodeDomainName", "domain
    // TODO: CD4C.getInstance().addMethod does not work with replaceTemplate due to early toString

    StringBuilder dsl2domainBody = new StringBuilder();

    var traverser = CD4CodeMill.inheritanceTraverser();
    traverser.add4CDBasis(new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDAttribute node) {
        if (node.getMCType() instanceof ASTMCOptionalType) {
          var typeString = ((ASTMCOptionalType) node.getMCType()).getMCTypeArgument().printType();
          String attributeName = getAttributeName(node.getName());
          int i = handleAttribute(node, typeString, astClass, clazz, DSL2CD::opt, as, false);

          if (i > 0) {
            dsl2domainBody.append("if (node.isPresent").append(StringTransformations.capitalize(node.getName())).append("()){\n");
            if (i == 1) {
              dsl2domainBody.append("node.get").append(StringTransformations.capitalize(node.getName())).append("().accept(getTraverser());\n");
              dsl2domainBody.append("nodeBuilder.").append(StringTransformations.uncapitalize(attributeName)).append("( (domain.").append(getUniqueShortName(typeString)).append(") stack.pop());\n}\n\n");
            } else {
              dsl2domainBody.append("nodeBuilder.").append(StringTransformations.uncapitalize(attributeName)).append("(Optional.of(").append("node.get").append(StringTransformations.capitalize(node.getName())).append("() ) );\n}\n\n");
            }
          }
        } else if (node.getMCType() instanceof ASTMCBasicGenericType && ((ASTMCBasicGenericType) node.getMCType()).printWithoutTypeArguments().equals("java.util.List")) {
          String attributeName = getAttributeName(node.getName());
          var typeString = ((ASTMCBasicGenericType) node.getMCType()).getMCTypeArgument(0).printType();
          int i = handleAttribute(node, typeString, astClass, clazz, DSL2CD::mult, as, true);

          if (i > 0) {
            String n = node.getName();

            if (node.getModifier().isPresentStereotype() && node.getModifier().getStereotype().contains("derived_attribute_name"))
              n = n.substring(0, n.length() - 1);
            if (i == ASSOC_ADDED) {
              dsl2domainBody.append("for (var elem : node.get").append(StringTransformations.capitalize(n)).append("List()){\n");
              dsl2domainBody.append("elem.accept(getTraverser());\n");
              dsl2domainBody.append("nodeBuilder.").append(StringTransformations.uncapitalize(attributeName)).append("Add( (domain.").append(getUniqueShortName(typeString)).append(") stack.pop());\n}\n\n");
            } else if (i == PRIMITIVE_ADDED) {
              dsl2domainBody.append("nodeBuilder.").append(StringTransformations.uncapitalize(attributeName)).append("(").append("node.get").append(StringTransformations.capitalize(node.getName())).append("List() );\n\n");
            } else if (i == STRING_LIST_WORKAROUND) {
              dsl2domainBody.append("/* string workaround */ nodeBuilder.").append(StringTransformations.uncapitalize(attributeName)).append("( String.join(\"<sep>\", ").append("node.get").append(StringTransformations.capitalize(node.getName())).append("List() ) );\n\n");
            }
          }
        } else if (node.getMCType() instanceof ASTMCQualifiedType) {
          var sym = as.resolveCDTypeMany(node.getMCType().printType());
          String attributeName = getAttributeName(node.getName());
          if (sym.size() == 1) {
            var type = sym.get(0);
            if (!type.getName().startsWith("AST")) return;

            String typeName = getUniqueShortName(sanitizeTypeName(type.getFullName()));
            clazz.addCDMember(CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(typeFromString(typeName)).build());
            requestedClasses.add(type.getAstNode());

            /*
                node.getMyDefinition().accept(getTraverser());
                nodeBuilder.myDefinition(pop());
            */

            dsl2domainBody.append("node.get").append(StringTransformations.capitalize(node.getName())).append("().accept(getTraverser());\n");
            dsl2domainBody.append("nodeBuilder.").append(StringTransformations.uncapitalize(attributeName)).append("( (domain.").append(typeName).append(") stack.pop());\n\n");


          } else if (primitives.contains(node.getMCType().printType())) {
            clazz.addCDMember(CD4CodeMill.cDAttributeBuilder().setName(node.getName()).setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(node.getMCType()).build());

            dsl2domainBody.append("nodeBuilder.").append(StringTransformations.uncapitalize(node.getName())).append("(node.get").append(StringTransformations.capitalize(node.getName().replaceFirst("r__", ""))).append("());\n\n");

          } else {
            clazz.add_PostComment(new Comment("/* TODO: unknown attribute " + node.getMCType().printType() + " */"));
            Log.warn("Unable to find type " + node.getMCType().printType(), node.getMCType().get_SourcePositionStart());
          }
        } else if (node.getMCType() instanceof ASTMCPrimitiveType) {
          String attributeName = getAttributeName(node.getName());
          clazz.addCDMember(CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(node.getMCType()).build());

          if (((ASTMCPrimitiveType) node.getMCType()).isBoolean()) {
            // nodeBuilder.r__private(node.isPrivate());
            dsl2domainBody.append("nodeBuilder.").append(StringTransformations.uncapitalize(attributeName)).append("(node.is").append(StringTransformations.capitalize(node.getName().replaceFirst("r__", ""))).append("());\n\n");
          } else {
            Log.warn("TODO: Unhandled sync primitive type " + node.getMCType().printType());
          }
        } else {
          clazz.add_PostComment(new Comment("/* TODO: unknown attribute-type " + node.getMCType().printType() + " */"));
          Log.warn("Unable to handle type " + node.getMCType().getClass(), node.getMCType().get_SourcePositionStart());
        }
      }
    });
    astClass.accept(traverser);

    CD4C.getInstance().addMethod(dsl2domainClass, "templates.dsl2domain.handle", astClass.getSymbol().getFullName().substring(astClass.getSymbol().getPackageName().length() + 1), clazz.getName(), "domain.Domain", dsl2domainBody.toString());


    return clazz;
  }

  protected static String getAttributeName(String name) {
    // TS/JS does not like argument-names, such as "arguments" in strict mode
    if (name.equals("arguments")) return "r__arguments";
    return name;
  }

  protected static ASTCDInterface handleInterface(ASTCDInterface astClass, ICD4AnalysisArtifactScope as) {
    String name = sanitizeTypeName(astClass.getSymbol().getFullName());
    name = getUniqueShortName(name);
    var clazz = CD4CodeMill.cDInterfaceBuilder().setName(name).setModifier(astClass.getModifier()).build();

    if (!clazz.getModifier().isPresentStereotype())
      clazz.getModifier().setStereotype(CD4CodeMill.stereotypeBuilder().uncheckedBuild());

    // TODO: inner members?

    return clazz;
  }

  static final int SKIP=-1, ASSOC_ADDED=1, STRING_LIST_WORKAROUND=3, PRIMITIVE_ADDED=2;
  private static int handleAttribute(ASTCDAttribute node, String typeString, ASTCDClass astClass, de.monticore.cd4codebasis._ast.ASTCDClass clazz, Supplier<ASTCDCardinality> cardinalitySupplier, ICD4AnalysisArtifactScope as, boolean useStringWorkaround) {
    var sym = as.resolveCDTypeMany(typeString);
    String attributeName = getAttributeName(node.getName());
    if (sym.size() == 1) {
      var type = sym.get(0);
      if (!type.getName().startsWith("AST")) return SKIP;

      clazz.addCDMember(CD4CodeMill.cDDirectCompositionBuilder().setCDAssocRightSide(CD4CodeMill.cDAssocRightSideBuilder().setCDRole(CD4CodeMill.cDRoleBuilder().setName(attributeName).build()).setMCQualifiedType(typeFromString(getUniqueShortName(sanitizeTypeName(type.getFullName())))).setCDCardinality(cardinalitySupplier.get()).setModifier(CD4CodeMill.modifierBuilder().build())

          .build()

      ).build());
      requestedClasses.add(type.getAstNode());
      return ASSOC_ADDED;
    } else if ("String".equals(typeString) && useStringWorkaround) {
      clazz.addCDMember(CD4CodeMill.cDAttributeBuilder().setName(attributeName).setMCType(typeFromString(typeString)).setModifier(CD4CodeMill.modifierBuilder().build()).build());
      return STRING_LIST_WORKAROUND;
    } else if (primitives.contains(typeString)) {
      clazz.addCDMember(CD4CodeMill.cDDirectCompositionBuilder().setCDAssocRightSide(CD4CodeMill.cDAssocRightSideBuilder().setCDRole(CD4CodeMill.cDRoleBuilder().setName(attributeName).build()).setMCQualifiedType(typeFromString(typeString)).setCDCardinality(cardinalitySupplier.get()).setModifier(CD4CodeMill.modifierBuilder().build())

          .build()

      ).build());
      return PRIMITIVE_ADDED;
    } else {
      clazz.add_PostComment(new Comment("/* TODO: unknown attribute " + typeString + " */"));
      Log.warn("Unable to find type for opt/list " + typeString, node.getMCType().get_SourcePositionStart());
    }
    return SKIP;
  }

  static String sanitizeTypeName(String type) {
    // De_monticore_UMLStereotype_de_monticore_umlstereotype__ast_ASTStereotype
//    type = type.replace(".", "_");

    int index = type.lastIndexOf("._ast.");

    // Shorten de package.package__ast_name (and remove a single package)
    if (index > 0) {
      type = type.substring(index / 2 + 1);
    }

    return StringTransformations.capitalize(type);
  }

  static ASTMCQualifiedType typeFromString(String fqn) {
    return CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(Splitters.DOT.splitToList(fqn)).build()).build();
  }

  static ASTCDCardOpt opt() {
    return CD4CodeMill.cDCardOptBuilder().setDigitssList(Arrays.asList("0", "1")).build();
  }

  static ASTCDCardMult mult() {
    return CD4CodeMill.cDCardMultBuilder().build();
  }

  static String simplifyName(CDTypeSymbol symbol) {
    return symbol.getFullName().substring(symbol.getPackageName().length() + 1);
  }

  // Look for all classes implementing a given interface
  static void lookForImplementing(ASTCDInterface cdInterface, List<ASTCDCompilationUnit> allCDs) {
    // [package:]de.monticore.expressions.ExpressionsBasis].de.monticore.expressions.expressionsbasis._ast.name:[ASTExpression]
    String name = cdInterface.getSymbol().getFullName().substring(cdInterface.getSymbol().getPackageName().length() + 1);

    var t = CD4CodeMill.inheritanceTraverser();
    t.add4CDBasis(new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (!node.isPresentCDInterfaceUsage()) return;
        for (var x : node.getCDInterfaceUsage().getInterfaceList()) {
          if (x.printType().equals(name)) {
            requestedClasses.add(node);
          }
        }
      }
    });
    for (var c : allCDs)
      c.accept(t);
  }
}
