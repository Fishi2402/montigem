// (c) https://github.com/MontiCore/monticore
package umlp.rte;

import java.util.List;

/**
 * List with a gem ID
 *
 * required to load lists in the Manager
 * without giving direct access to LazyList
 * @param <E> type of elements to be stored
 */
public interface GemIdentifiableList<E> extends List<E>, GemIdentifiable {
}
