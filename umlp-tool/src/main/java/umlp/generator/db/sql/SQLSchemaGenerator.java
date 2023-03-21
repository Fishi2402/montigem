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
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPFullPrettyPrinter;
import umlp.UMLPMill;
import umlp.generator.domain.decorator.GenService;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: cardinality constraints?
public class SQLSchemaGenerator {
    protected GeneratorSetup generatorSetup;
    protected GeneratorEngine generatorEngine;
    protected Map<String, String> fqnClassNameToType;

    public SQLSchemaGenerator(GeneratorSetup generatorSetup) {
        this.generatorSetup = generatorSetup;
        this.generatorEngine = new GeneratorEngine(generatorSetup);
        initClassNameToType();
    }

    protected void initClassNameToType() {
        fqnClassNameToType = new HashMap<>();
        fqnClassNameToType.put("java.lang.String", "VARCHAR");
        fqnClassNameToType.put("String", "VARCHAR");
        fqnClassNameToType.put("java.lang.Byte", "TINYINT");
        fqnClassNameToType.put("Byte", "TINYINT");
        fqnClassNameToType.put("byte", "TINYINT");
        fqnClassNameToType.put("java.lang.Short", "SMALLINT");
        fqnClassNameToType.put("Short", "SMALLINT");
        fqnClassNameToType.put("short", "SMALLINT");
        fqnClassNameToType.put("java.lang.Integer", "INTEGER");
        fqnClassNameToType.put("Integer", "INTEGER");
        fqnClassNameToType.put("int", "INTEGER");
        fqnClassNameToType.put("java.lang.Long", "BIGINT");
        fqnClassNameToType.put("Long", "INTEGER");
        fqnClassNameToType.put("long", "INTEGER");
        fqnClassNameToType.put("java.lang.Float", "FLOAT");
        fqnClassNameToType.put("Float", "FLOAT");
        fqnClassNameToType.put("float", "FLOAT");
        fqnClassNameToType.put("java.lang.Double", "DOUBLE");
        fqnClassNameToType.put("Double", "DOUBLE");
        fqnClassNameToType.put("double", "DOUBLE");
        fqnClassNameToType.put("java.lang.Boolean", "BOOLEAN");
        fqnClassNameToType.put("Boolean", "BOOLEAN");
        fqnClassNameToType.put("boolean", "BOOLEAN");
        fqnClassNameToType.put("java.lang.Char", "CHAR");
        fqnClassNameToType.put("Char", "CHAR");
        fqnClassNameToType.put("char", "CHAR");
    }

    public void addClassToSQLTypeMapping(String fqnClassName, String sqlType) {
        fqnClassNameToType.put(fqnClassName, sqlType);
    }



    public static String convertAttributeName(String name, AttributeType type) {
        String attrName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        return type.format(attrName);
    }

    public static String convertClassName(String name, AttributeType type) {
        String clsName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        return type.format(clsName);
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

    public void generate(ASTCDCompilationUnit cdAST) {
        String templateName = "umlp.sql.Schema";
        generatorEngine.generate(templateName, Paths.get("schemata", cdAST.getCDDefinition().getName() + ".sql"), cdAST, generateContent(cdAST));
    }

    protected String generateContent(ASTCDCompilationUnit cd) {
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
        String tableName = convertClassName(node.getName(), AttributeType.QUOTED);
        p.print(tableName);
        p.println("(");
        p.indent();

        List<ASTCDAttribute> relevantAttributes = CDSymbolTables.getAttributesInHierarchy(node)
                .stream()
                .filter(a -> !a.getModifier().isStatic())
                .filter(a -> !a.getModifier().isDerived())
                .collect(Collectors.toList());

        generateIdField(convertClassName(node.getName(), AttributeType.PLAIN), p);
        for (ASTCDAttribute attribute : relevantAttributes) {
            Optional<? extends ISymbol> typeSym = attribute.getMCType().getDefiningSymbol();
            if (
                    !GenService.isList(attribute)
                            && !GenService.isOptional(attribute)
                            && (
                            typeSym.isEmpty()
                                    || !(typeSym.get() instanceof CDTypeSymbol)
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
            if (GenService.isList(attribute)) {
                generateListFieldTable(cd, node, attribute, p);
            }
            if (GenService.isOptional(attribute)) {
                generateListFieldTable(cd, node, attribute, p);
            }
            if (attribute.getMCType().getDefiningSymbol().isEmpty()) {
                generateListFieldTable(cd, node, attribute, p);
            }
        }
    }

    protected void generateListFieldTable(ASTCDCompilationUnit cd, ASTCDClass classNode, ASTCDAttribute attribute, IndentPrinter p) {
        p.print("CREATE TABLE ");
        String tableName = convertClassName(classNode.getName(), AttributeType.PLAIN) + "$" + convertAttributeName(attribute.getName(), AttributeType.PLAIN);
        p.print(tableName);
        p.println("(");
        p.indent();

        String r1 = "id";
        String r2 = "_value";

        p.print(r1);
        p.println(" BIGINT NOT NULL,");

        p.print(r2);
        p.print(" ");
        String type = GenService.getInnerType(attribute.getMCType());
        if (!fqnClassNameToType.containsKey(type)) {
            Log.error("Can not convert type to SQL: " + type + " used in " + cd.getCDDefinition().getName() + "." + classNode.getName() + "." + attribute.getName());
        }

        p.print(fqnClassNameToType.get(type));
        p.println(" NOT NULL,");

        p.println("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + r1 + "," + r2 + "),");
        p.println("CONSTRAINT fk_" + tableName + " FOREIGN KEY (" + r1 + ") REFERENCES " + convertClassName(classNode.getName(), AttributeType.QUOTED));

        p.unindent();
        p.println(");");
    }

    protected void generateIdField(String tableName, IndentPrinter p) {
        p.println("id BIGINT NOT NULL,");
        p.println("CONSTRAINT pk_" + tableName + " PRIMARY KEY (id)");
    }

    protected void generateSimpleField(ASTCDCompilationUnit cd, ASTCDClass classNode, ASTCDAttribute attribute, IndentPrinter p) {
        p.print(convertAttributeName(attribute.getName(), AttributeType.QUOTED));
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

        if (lSymbolOpt.get().isIsDerived()) {
            UMLPFullPrettyPrinter prettyPrinter = new UMLPFullPrettyPrinter();
            Log.info("Skipping generation of association " + prettyPrinter.prettyprint((ASTCDBasisNode) association) + " since the left class is derived", "SQLSchemaGenerator");
            return;
        }


        if (rSymbolOpt.get().isIsDerived()) {
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

        p.println("CONSTRAINT fk_" + r1 + "_" + tableName + " FOREIGN KEY (" + r1 + "_id) REFERENCES " + convertClassName(lSymbolOpt.get().getName(), AttributeType.QUOTED) + ",");

        p.print(r2 + "_id");
        p.println(" BIGINT NOT NULL,");

        p.println("CONSTRAINT fk_" + r2 + "_" + tableName + " FOREIGN KEY (" + r2 + "_id) REFERENCES " + convertClassName(rSymbolOpt.get().getName(), AttributeType.QUOTED) + ",");

        p.println("CONSTRAINT pk_" + tableName + " PRIMARY KEY (" + r1 + "_id, " + r2 + "_id)");

        p.unindent();
        p.println(");");
    }

    public enum AttributeType {
        PLAIN(""),
        QUOTED("\""),
        ESCAPED_QUOTED("\\\"");

        private final String modifier;

        AttributeType(String modifier) {
            this.modifier = modifier;
        }


        String format(String s) {
            return modifier + s + modifier;
        }
    }

}
