package umlp.backendrte.common;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A read-only adapter for GemLists
 * throw {@link UnsupportedOperationException} on unsupported modifications
 * @param <E>
 */
public class ReadOnlyGemList<E extends GemIdentifiable> extends GemList<E> {
  protected final GemList<E> realList;

  public ReadOnlyGemList(GemList<E> realList) {
    this.realList = realList;
  }

  @Override
  protected List<LoadResult<E>> loadObjects(List<Long> gemIDs) {
    return realList.loadObjects(gemIDs);
  }

  @Override
  public Optional<? extends GemList<E>> build() {
    return realList.build();
  }

  @Override
  public int size() {
    return realList.size();
  }

  @Override
  public E get(int idx) {
    return realList.get(idx);
  }

  @Override
  public boolean contains(Long gemID) {
    return realList.contains(gemID);
  }

  @Override
  public E set(int idx, E elem) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public void _internal_set(int idx, long gemID) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public void add(int idx, E elem) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public void _internal_add(int idx, List<Long> gemIDs) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public E remove(int idx) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public boolean add(E e) {
    return realList.add(e);
  }

  @Override
  public int indexOf(Object o) {
    return realList.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return realList.lastIndexOf(o);
  }

  @Override
  public void clear() {
    realList.clear();
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    return realList.addAll(index, c);
  }

  @Override
  public ListIterator<E> listIterator() {
    return realList.listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return realList.listIterator(index);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return realList.subList(fromIndex, toIndex);
  }

  @Override
  public boolean isEmpty() {
    return realList.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return realList.contains(o);
  }

  @Override
  public Object[] toArray() {
    return realList.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return realList.toArray(a);
  }

  @Override
  public boolean remove(Object o) {
    return realList.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return realList.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    return realList.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return realList.retainAll(c);
  }

  @Override
  public String toString() {
    return realList.toString();
  }

  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    return realList.removeIf(filter);
  }

  @Override
  public Stream<E> stream() {
    return realList.stream();
  }

  @Override
  public Stream<E> parallelStream() {
    return realList.parallelStream();
  }

  @Override
  public void forEach(Consumer<? super E> action) {
    realList.forEach(action);
  }

  @Override
  public void _internal_remove(Set<Integer> idxs) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public boolean isPresent(int idx) {
    return realList.isPresent(idx);
  }

  @Override
  public void setIDs(Map<Integer, Long> changes) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public Long getID(int idx) {
    return realList.getID(idx);
  }

  @Override
  public List<Long> getIDs(int startIdx, int endIdx) {
    return realList.getIDs(startIdx, endIdx);
  }

  @Override
  public List<Long> getIDs() {
    return realList.getIDs();
  }

  @Override
  public void removeIdSure(long gemId) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public int indexOfId(long gemId) {
    return realList.indexOfId(gemId);
  }

  @Override
  public boolean TESTING_deepEquals(List<E> o) {
    return realList.TESTING_deepEquals(o);
  }

  @Override
  public boolean isBuildFinished() {
    return realList.isBuildFinished();
  }

  @Override
  public <T> T[] toArray(IntFunction<T[]> generator) {
    return realList.toArray(generator);
  }

  @Override
  public Spliterator<E> spliterator() {
    return realList.spliterator();
  }

  @Override
  public Iterator<E> iterator() {
    return realList.iterator();
  }

  @Override
  public boolean equals(Object o) {
    return realList.equals(o);
  }

  @Override
  public int hashCode() {
    return realList.hashCode();
  }

  @Override
  public void sort(Comparator<? super E> c) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }

  @Override
  public void replaceAll(UnaryOperator<E> operator) {
    throw new UnsupportedOperationException("The global GemList is read only");
  }


}
