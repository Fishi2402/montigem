/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.db.sql;

import com.google.common.base.CaseFormat;
import de.monticore.cd._symboltable.CDSymbolTables;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDBasisNode;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPFullPrettyPrinter;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.GenService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: cardinality constraints?
public class SQLSchemaGenerator {
  public static Map<String, String> fqnClassNameToType = Map.ofEntries(
      Map.entry("java.lang.String", "VARCHAR"),
      Map.entry("String", "VARCHAR"),
      Map.entry("java.lang.Byte", "TINYINT"),
      Map.entry("Byte", "TINYINT"),
      Map.entry("byte", "TINYINT"),
      Map.entry("java.lang.Short", "SMALLINT"),
      Map.entry("Short", "SMALLINT"),
      Map.entry("short", "SMALLINT"),
      Map.entry("java.lang.Integer", "INTEGER"),
      Map.entry("Integer", "INTEGER"),
      Map.entry("int", "INTEGER"),
      Map.entry("java.lang.Long", "BIGINT"),
      Map.entry("Long", "INTEGER"),
      Map.entry("long", "INTEGER"),
      Map.entry("java.lang.Float", "FLOAT"),
      Map.entry("Float", "FLOAT"),
      Map.entry("float", "FLOAT"),
      Map.entry("java.lang.Double", "DOUBLE"),
      Map.entry("Double", "DOUBLE"),
      Map.entry("double", "DOUBLE"),
      Map.entry("java.lang.Boolean", "BIT"),
      Map.entry("Boolean", "BIT"),
      Map.entry("boolean", "BIT"),
      Map.entry("java.lang.Char", "CHAR"),
      Map.entry("Char", "CHAR"),
      Map.entry("char", "CHAR"),
      Map.entry("java.time.LocalDate", "DATE"),
      // TODO: Erweiterungspunkt im Template anbieten?
      // Not supporting this type anymore Map.entry("java.util.Date", "DATE"),
      // TODO: as millis or nanos?
      Map.entry("java.time.Duration", "BIGINT"),
      Map.entry("java.time.Instant", "TIMESTAMP"),
      Map.entry("java.net.InetAddress", "VARCHAR"));

  public static String convertAttributeName(String name) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
  }

  public static String convertClassName(String name) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
  }

  public static String convertAssociationRollName(String name) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
  }

  public static String convertAssocName(String name) {
    String res = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    res = res.replaceAll("\\s", "_");
    res = res.replaceAll("_+", "_");
    res = res.replaceAll("_$", "");
    return "assoc_" + res;
  }

  public String generateContent(ASTCDCompilationUnit cd) {
    IndentPrinter p = new IndentPrinter();

    generateTypeSchemas(cd, p);
    for (ASTCDAssociation association : cd.getCDDefinition().getCDAssociationsList()) {
      if (!association.getModifier().isDerived()) {
        generateAssociationSchema(cd, association, p);
      }
    }

    return p.getContent();
  }

  protected void generateTypeSchemas(ASTCDCompilationUnit cd, IndentPrinter p) {
    var v = new CDBasisVisitor2() {
      @Override
      public void visit(ASTCDClass node) {
        if (!node.getModifier().isDerived() && !node.getModifier().isAbstract()) {
          generateClassSchema(cd, node, p);
        }
      }
    };
    var t = UMLPMill.traverser();
    t.add4CDBasis(v);
    cd.accept(t);
  }

  public void generateClassSchema(ASTCDCompilationUnit cd, ASTCDClass node, IndentPrinter p) {
    p.print("CREATE TABLE ");
    // TODO: is this mapping always unique and possible?
    String tableName = convertClassName(node.getName());
    p.print(tableName);
    p.println("(");
    p.indent();

    List<ASTCDAttribute> relevantAttributes = CDSymbolTables.getAttributesInHierarchy(node)
        .stream()
        .filter(a -> !a.getModifier().isStatic())
        .filter(a -> !a.getModifier().isDerived())
        .collect(Collectors.toList());

    generateIdField(tableName, p);
    for (ASTCDAttribute attribute : relevantAttributes) {
      Optional<? extends ISymbol> typeSym = attribute.getMCType().getDefiningSymbol();
      if (
          !GenService.isList(attribute)
          && !GenService.isOptional(attribute)
          && (
              typeSym.isEmpty()
              || !(typeSym.get() instanceof  CDTypeSymbol)
          )
      ) {
        p.print(",");
        generateSimpleField(cd, node, attribute, p);
      }
    }

    p.unindent();
    p.println(");");

    // TODO: associations in hierarchie?
    for (ASTCDAttribute attribute : relevantAttributes) {
      if(GenService.isList(attribute)){
        generateListFieldTable(cd, node, attribute, p);
      }
      if(GenService.isOptional(attribute)){
        generateListFieldTable(cd, node, attribute, p);
      }
      if(attribute.getMCType().getDefiningSymbol().isEmpty()){
        generateListFieldTable(cd, node, attribute, p);
      }
    }
  }

  protected void generateListFieldTable(ASTCDCompilationUnit cd, ASTCDClass classNode, ASTCDAttribute attribute, IndentPrinter p) {
    p.print("CREATE TABLE ");
    String tableName = convertClassName(classNode.getName()) + "$" + convertAttributeName(attribute.getName());
    p.print(tableName);
    p.println("(");
    p.indent();

    String r1 = "id";
    String r2 = "value";

    p.print(r1);
    p.println(" BIGINT NOT NULL,");

    p.print(r2);
    p.print(" ");
    String type = GenService.getInnerType(attribute.getMCType());
    if(!fqnClassNameToType.containsKey(type)){
      Log.error("Can not convert type to SQL: " + type + " used in " + cd.getCDDefinition().getName() + "." + classNode.getName() + "." + attribute.getName());
    }

    p.print(fqnClassNameToType.get(type));
    p.println(" NOT NULL,");

    p.println("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + r1 + "," + r2 + "),");
    p.println("CONSTRAINT fk_" + tableName + " FOREIGN KEY (" + r1 + ") REFERENCES " + convertClassName(classNode.getName()));

    p.unindent();
    p.println(");");
  }

  protected void generateIdField(String tableName, IndentPrinter p) {
    p.println("id BIGINT NOT NULL,");
    p.println("CONSTRAINT pk_" + tableName + " PRIMARY KEY (id)");
  }

  protected void generateSimpleField(ASTCDCompilationUnit cd, ASTCDClass classNode, ASTCDAttribute attribute, IndentPrinter p) {
    p.print(convertAttributeName(attribute.getName()));
    p.print(" ");
    Optional<? extends ISymbol> typeSymbolOpt = attribute.getMCType().getDefiningSymbol();
    if (typeSymbolOpt.isEmpty()) {
      Log.error("Can not find symbol for type of attribute " + cd.getCDDefinition().getName() + "." + classNode.getName() + "." + attribute.getName());
      return;
    }

    String fullName = typeSymbolOpt.get().getFullName();
    if (!fqnClassNameToType.containsKey(fullName)) {
      Log.error("Can not transform type to SQL: " + fullName + " used in " + cd.getCDDefinition().getName() + "." + classNode.getName() + "." + attribute.getName());
    }
    p.print(fqnClassNameToType.get(fullName));

    p.println(" NOT NULL");
  }

  protected void generateAssociationSchema(ASTCDCompilationUnit cd, ASTCDAssociation association, IndentPrinter p) {
    Optional<CDTypeSymbol> lSymbolOpt = cd.getCDDefinition().getEnclosingScope().resolveCDType(association.getLeft().getMCQualifiedType().getMCQualifiedName().getQName());
    Optional<CDTypeSymbol> rSymbolOpt = cd.getCDDefinition().getEnclosingScope().resolveCDType(association.getRight().getMCQualifiedType().getMCQualifiedName().getQName());
    if (lSymbolOpt.isEmpty()) {
      UMLPFullPrettyPrinter prettyPrinter = new UMLPFullPrettyPrinter();
      Log.error("Can not find type " +
          prettyPrinter.prettyprint(association.getLeft().getMCQualifiedType())
          + " for association "
          + prettyPrinter.prettyprint((ASTCDBasisNode) association)
      );
      return;
    }

    if (rSymbolOpt.isEmpty()) {
      UMLPFullPrettyPrinter prettyPrinter = new UMLPFullPrettyPrinter();
      Log.error("Can not find type " +
          prettyPrinter.prettyprint(association.getRight().getMCQualifiedType())
          + " for association "
          + prettyPrinter.prettyprint((ASTCDBasisNode) association)
      );
      return;
    }

    if(lSymbolOpt.get().isIsDerived()){
      UMLPFullPrettyPrinter prettyPrinter = new UMLPFullPrettyPrinter();
      Log.info("Skipping generation of association " + prettyPrinter.prettyprint((ASTCDBasisNode) association) + " since the left class is derived", "SQLSchemaGenerator");
      return;
    }


    if(rSymbolOpt.get().isIsDerived()){
      UMLPFullPrettyPrinter prettyPrinter = new UMLPFullPrettyPrinter();
      Log.info("Skipping generation of association" + prettyPrinter.prettyprint((ASTCDBasisNode) association) + " since the right class is derived", "SQLSchemaGenerator");
      return;
    }


    p.print("CREATE TABLE ");
    String tableName = convertAssocName(association.getName());
    p.print(tableName);
    p.println("(");
    p.indent();

    // TODO: use roll names: Person -> Person
    String r1 = convertAssociationRollName(association.getLeft().getName());
    String r2 = convertAssociationRollName(association.getRight().getName());

    p.print(r1 + "_id");
    p.println(" BIGINT NOT NULL,");
    // TODO: different foreign key for each non-abstract, non-derived subclass
    // TODO: Convert fqn?

    p.println("CONSTRAINT fk_" + r1 + "_" + tableName + " FOREIGN KEY (" + r1 + "_id) REFERENCES " + convertClassName(lSymbolOpt.get().getName()) + ",");

    p.print(r2 + "_id");
    p.println(" BIGINT NOT NULL,");

    p.println("CONSTRAINT fk_" + r2 + "_" + tableName + " FOREIGN KEY (" + r2 + "_id) REFERENCES " + convertClassName(rSymbolOpt.get().getName()) + ",");

    p.println("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + r1 + "_id, " + r2 + "_id)");

    p.unindent();
    p.println(");");
  }

}
