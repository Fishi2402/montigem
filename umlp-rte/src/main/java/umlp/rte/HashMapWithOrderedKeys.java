/* (c) https://github.com/MontiCore/monticore */
package umlp.rte;

// RTE-P: Demo systems

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// A subclass of HashMap that manages a list of its keys.
// By using a list instead of a set, indexed access to the keys is possible
// This can be used to choose an element at random in O(1) instead of O(n)
// The FlensburgDummyProvider uses it to create random associations in the FlensburgMock
public class HashMapWithOrderedKeys<K, V> extends HashMap<K, V> {
  private List<K> keys = new ArrayList<>();

  public K getKeyAt(int index){
    return keys.get(index);
  }

  public K getRandomKey(Random r){
    return keys.get(r.nextInt(size()));
  }

  public V getRandomValue(Random r){
    return get(getRandomKey(r));
  }

  @Override
  public void clear() {
    super.clear();
    keys.clear();
  }

  @Override
  public V put(K key, V value) {
    if(!containsKey(key)){
      keys.add(key);
    }
    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    m.keySet().forEach(key -> {
      if(!containsKey(key)){
        keys.add(key);
      }
    });
    super.putAll(m);
  }

  @Override
  public V putIfAbsent(K key, V value) {
    if(!containsKey(key)){
      keys.add(key);
    }
    return super.putIfAbsent(key, value);
  }

  @Override
  public V remove(Object key) {
    keys.remove(key);
    return super.remove(key);
  }

  @Override
  public boolean remove(Object key, Object value) {
    boolean res = super.remove(key, value);
    if(res){
      keys.remove(key);
    }
    return res;
  }
}
