/* (c) https://github.com/MontiCore/monticore */
package hardy;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 *
 *          $Date$
 *
 */
public abstract class AbstractObservable implements DexObservable {
  
  protected Set<DexObserver> observers = new HashSet<DexObserver>();

  public void addObserver(DexObserver observer) {
    this.observers.add(observer);
  }

  public void deleteObserver(DexObserver observer) {
    this.observers.remove(observer);
  }

  public void deleteObservers() {
    this.observers.clear();
  }

  public void notifyObservers() {
    notifyObservers(null);
  }

  public void notifyObservers(Object argument) {
    for (DexObserver o : observers) {
      o.update(this, argument);
    }
  }
}
