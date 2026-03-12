package de.monticore.doc;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._ast.ASTCDAssocDir;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssocType;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for generating LaTeX documentation from Class Diagrams.
 * Extends PrintUtilForLaTex to provide CD-specific formatting capabilities.
 */
public class CDDocUtilForLaTeX extends PrintUtilForLaTex {

  /**
   * Creates a mapping of class names to their associations.
   * This helps organize which associations should be displayed for each class.
   *
   * @param cdAST The class diagram AST
   * @return Map where key is class name and value is list of associations involving that class
   */
  public Map<String, List<ASTCDAssociation>> getClassAssociationMap(ASTCDCompilationUnit cdAST) {
    Map<String, List<ASTCDAssociation>> classAssociationMap = new HashMap<>();

    // Initialize map with all classes
    for (ASTCDClass clazz : cdAST.getCDDefinition().getCDClassesList()) {
      classAssociationMap.put(clazz.getName(), new ArrayList<>());
    }

    // Add associations to relevant classes
    for (ASTCDAssociation assoc : cdAST.getCDDefinition().getCDAssociationsList()) {
      // Get class names from both sides of the association
      Optional<Map.Entry<ASTCDType, ASTCDType>> connectedTypes =
          getConnectedTypes(assoc, cdAST);
      if (connectedTypes.isEmpty()) {
        continue;
      }
      String leftClassName = connectedTypes.get().getKey().getSymbol().getFullName();
      String rightClassName = connectedTypes.get().getValue().getSymbol().getFullName();

      // Add association to both classes if they exist in our map
      if (classAssociationMap.containsKey(leftClassName)) {
        classAssociationMap.get(leftClassName).add(assoc);
      }
      if (classAssociationMap.containsKey(rightClassName)) {
        classAssociationMap.get(rightClassName).add(assoc);
      }
    }

    return classAssociationMap;
  }

  /**
   * Gets the cardinality string from an association side.
   */
  public String getCardinalityString(ASTCDAssocSide assocSide) {
    if (assocSide.isPresentCDCardinality()) {
      if (assocSide.getCDCardinality().isMult()) {
        return "[*]";
      }
      else if (assocSide.getCDCardinality().isOne()) {
        return "[1]";
      }
      else if (assocSide.getCDCardinality().isOpt()) {
        return "[0..1]";
      }
      else if (assocSide.getCDCardinality().isAtLeastOne()) {
        return "[1..*]";
      }
    }
    return "[*]"; // Default to many if no cardinality is specified
  }

  /**
   * Get the classes that are connected with the association. The function returns null if the
   * associated objects aren't classes.
   *
   * @param association     association.
   * @param compilationUnit diagram.
   * @return pair of classes that are connected with the association.
   */
  public static Optional<Map.Entry<ASTCDType, ASTCDType>> getConnectedTypes(
      ASTCDAssociation association, ASTCDCompilationUnit compilationUnit
  ) {
    Optional<CDTypeSymbol> typeLeft =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> typeRight =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    if (typeLeft.isPresent() && typeRight.isPresent()) {
      return Optional.of(Map.entry(typeLeft.get().getAstNode(), typeRight.get().getAstNode()));
    }
    Log.error("Could not resolve types of :" + CD4CodeMill.prettyPrint(association, false));
    return Optional.empty();
  }

  /**
   * Gets the role name from an association side.
   */
  public static String inferRole(ASTCDAssocSide assocSide) {
    if (assocSide.isPresentCDRole()) {
      return assocSide.getCDRole().getName();
    }
    char[] roleName =
        assocSide.getMCQualifiedType().getMCQualifiedName().getBaseName().toCharArray();
    roleName[0] = Character.toLowerCase(roleName[0]);
    return new String(roleName);
  }

  /**
   * Gets the target class name from an association side.
   */
  public String getTargetClassName(ASTCDAssocSide assocSide) {
    char[] roleName =
        assocSide.getMCQualifiedType().getMCQualifiedName().getBaseName().toCharArray();
    roleName[0] = Character.toLowerCase(roleName[0]);
    return new String(roleName);
  }

  /**
   * Determines the association type - composition or association.
   */
  public String getAssociationType(ASTCDAssociation assoc) {
    // Check for composition
    ASTCDAssocType assocType = assoc.getCDAssocType();
    if (assocType.isComposition()) {
      return "Composition";
    }
    return "Association";
  }

  /**
   * Gets the opposite side of an association for a given class.
   * For directed associations, this returns the target side.
   * For bidirectional or undirected associations, returns the side not matching the current class.
   */
  public ASTCDAssocSide getOppositeSide(ASTCDAssociation assoc, String className) {
    ASTCDAssocDir direction = assoc.getCDAssocDir();
    if (!direction.isBidirectional()) {
      if (direction.isDefinitiveNavigableLeft()) {
        return assoc.getLeft();
      }
      else if (direction.isDefinitiveNavigableRight()) {
        return assoc.getRight();
      }
    }

    // For bidirectional or undirected: return the side not matching the className
    ASTCDAssocSide left = assoc.getLeft();
    ASTCDAssocSide right = assoc.getRight();
    String leftType = left.getMCQualifiedType().printType();
    String rightType = right.getMCQualifiedType().printType();

    if (className.equals(leftType)) {
      return right;
    }
    else if (className.equals(rightType)) {
      return left;
    }

    return null;
  }

  /**
   * Gets the current side of an association for a given class.
   * For directed associations, this returns the source side.
   * For bidirectional or undirected associations, returns the side matching the class.
   */
  public ASTCDAssocSide getCurrentSide(ASTCDAssociation assoc, String className) {
    ASTCDAssocDir direction = assoc.getCDAssocDir();
    if (!direction.isBidirectional()) {
      if (direction.isDefinitiveNavigableLeft()) {
        return assoc.getRight();
      }
      else if (direction.isDefinitiveNavigableRight()) {
        return assoc.getLeft();
      }
    }

    // For bidirectional or undirected: return the side matching the className
    ASTCDAssocSide left = assoc.getLeft();
    ASTCDAssocSide right = assoc.getRight();
    String leftType = left.getMCQualifiedType().printType();
    String rightType = right.getMCQualifiedType().printType();

    if (className.equals(leftType)) {
      return left;
    }
    else if (className.equals(rightType)) {
      return right;
    }

    return null;
  }


  /**
   * Formats attribute type with proper LaTeX escaping.
   */
  public String formatAttributeType(ASTCDAttribute attr) {
    if (attr.getMCType() != null) {
      return escapeForTexttt(attr.getMCType().printType());
    }
    return "";
  }

  /**
   * Formats attribute default value with proper LaTeX escaping.
   */
  public String formatAttributeDefault(ASTCDAttribute attr) {
    if (attr.isPresentInitial()) {
      return escapeForTexttt(attr.getInitial().toString());
    }
    return "";
  }

  /**
   * Gets visibility modifier for an attribute.
   */
  public String getAttributeVisibility(ASTCDAttribute attr) {
    if (attr.getModifier().isPrivate()) {
      return "private";
    }
    else if (attr.getModifier().isProtected()) {
      return "protected";
    }
    else if (attr.getModifier().isPublic()) {
      return "public";
    }
    return "package";
  }

  /**
   * Checks if an attribute is static.
   */
  public boolean isAttributeStatic(ASTCDAttribute attr) {
    return attr.getModifier().isStatic();
  }

  /**
   * Checks if an attribute is final.
   */
  public boolean isAttributeFinal(ASTCDAttribute attr) {
    return attr.getModifier().isFinal();
  }

  /**
   * Gets a formatted string of attribute modifiers.
   */
  public String getAttributeModifiers(ASTCDAttribute attr) {
    List<String> modifiers = new ArrayList<>();

    if (isAttributeStatic(attr)) {
      modifiers.add("static");
    }
    if (isAttributeFinal(attr)) {
      modifiers.add("final");
    }

    String visibility = getAttributeVisibility(attr);
    if (!visibility.equals("package")) {
      modifiers.add(0, visibility); // Add visibility at the beginning
    }

    return modifiers.isEmpty() ? "" : String.join(" ", modifiers);
  }

  /**
   * Checks if a class has any attributes.
   */
  public boolean hasAttributes(ASTCDClass clazz) {
    return clazz.getCDAttributeList() != null && !clazz.getCDAttributeList().isEmpty();
  }

  /**
   * Checks if a class has any associations.
   */
  public boolean hasAssociations(String className, Map<String, List<ASTCDAssociation>> classAssociationMap) {
    return !classAssociationMap.get(className).isEmpty();
  }
}