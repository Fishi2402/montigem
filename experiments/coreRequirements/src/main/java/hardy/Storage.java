/* (c) https://github.com/MontiCore/monticore */

package hardy;

import dex.persistence.CreateRequest;
import dex.persistence.ReadRequest;
import dex.persistence.Response;
import dex.persistence.UpdateRequest;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A Manager creates, retrieves, updates or deletes the instances of a specific
 * class T. Panels can register as Observers to get access to the model objects.
 * Part of the runtime for dex. (used by the generator, but independent of a
 * concrete Dex-Model)
 */
public interface Storage<T> {

  /**
   * Removes (deletes) a single object.
   *
   * @param o
   */
  Response<T> discard(T o);

  /**
   * Requests a new object to be created.
   *
   * @param obj
   */
  Response<T> add(CreateRequest c);

  /**
   * Checks if user is able to create given type
   */
  boolean canCreate();

  /**
   * Checks if user is able to read given type
   */
  boolean canRead();

  /**
   * Checks if user is able to update given type
   */
  boolean canUpdate();

  /**
   * Checks if user is able to delete given type
   */
  boolean canDelete();

  /**
   * Updates an already existing object.
   *
   * @param o
   */
  Response<T> update(UpdateRequest o);

  /**
   * Loads the complete object from the server. Initially
   * only proxy objects with some values (no associations)
   * are loaded.
   *
   * @param o
   */
  Response<T> get(T o);

  /**
   *
   */
  Response<T> get(Long id);

  /**
   * Returns an iterator of a Collection which contains all instances of the
   * specific class T in the actual state. This method should only be used by
   * Panels after notification. To trigger a synchronization with data storage
   * and a notification of other panels use {@link #synchronize()}
   *
   * @param includeSubtypes
   * @return
   */
  Iterator<T> iterator(ReadRequest r);

  /**
   * Returns the number of elements of type T (including subtypes)
   *
   * @return
   */
  Response<Integer> size();

  /**
   * removes all instances
   */
  void clear();

  /**
   * Search storage for a particular string
   * @param searchString
   * @return
   */
  Map<T, List<String>> search(String s);
}
