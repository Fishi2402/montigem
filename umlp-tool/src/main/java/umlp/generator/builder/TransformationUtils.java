/* (c) https://github.com/MontiCore/monticore */


package umlp.generator.builder;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import de.monticore.cdassociation._symboltable.CDAssociationSymbol;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TransformationUtils {

  private static final List<Integer> exceptionNumbers = Lists.newArrayList();

  public static final String CLASSES_PACKAGE = "cdmodelhwc.classes";

  /**
   * Returns a string in camel case notation
   *
   * @param stringList a list of strings
   * @return the string in camel case notation
   */
  public static String makeCamelCase(String... stringList) {
    List<String> lst = Lists.newArrayList(stringList);
    Iterator<String> namesIterator = lst.iterator();
    StringBuilder fullName = new StringBuilder();

    if (namesIterator.hasNext()) {
      fullName.append(namesIterator.next());
    }

    while (namesIterator.hasNext()) {
      fullName.append(capitalize(namesIterator.next()));
    }
    return fullName.toString();
  }

  /**
   * Capitalizes the first character of the string
   *
   * @param string the string
   * @return the capitalized string
   */
  public static String capitalize(String string) {
    checkNotNull(string);
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, string);
  }

  /**
   * Uncapitalizes the first character of the string
   *
   * @param string the string
   * @return the capitalized string
   */
  public static String uncapitalize(final String string) {
    checkNotNull(string, "Cannot uncapitalize an invalid string.");
    checkArgument(!string.isEmpty(), "Cannot uncapitalize an empty string.");

    return Character.toLowerCase(string.charAt(0)) + string.substring(1);
  }

  public static String getErrorCode() {
    StringBuilder str = new StringBuilder("0xD");
    int newExc = 0;

    do {
      newExc = getRandom(4);
    } while (exceptionNumbers.contains(newExc));

    exceptionNumbers.add(newExc);

    str.append(newExc);
    return str.toString();
  }

  public static Integer getRandom(Integer pSize) {

    if (pSize <= 0) {
      return null;
    }
    Double dMin = Math.pow(10, pSize.doubleValue() - 1D);
    Double dMax = (Math.pow(10, (pSize).doubleValue())) - 1D;
    int min = dMin.intValue();
    int max = dMax.intValue();
    Random ran = new Random();
    return ran.nextInt(max - min) + min;
  }

  public static boolean isBidirectional(CDRoleSymbol symbol) {
    return symbol.isIsDefinitiveNavigable() && symbol.getOtherSide().isIsDefinitiveNavigable();
  }
}
