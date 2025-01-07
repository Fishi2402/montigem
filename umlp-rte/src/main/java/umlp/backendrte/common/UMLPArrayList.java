package umlp.backendrte.common;

import java.util.ArrayList;

/**
 * This Class is only relevant for Java to TypeScript transpilation.
 * In Java GUI code lists are transpiled to normal arrays, while list in the Java Domain (Backend)
 * code are transpiled to IUMLPLists. To use Backend transpiled lists in GUI transpiled code there
 * is a to GemListToArray method.
 * @param <E>
 */
public class UMLPArrayList<E> extends ArrayList<E> {

}
