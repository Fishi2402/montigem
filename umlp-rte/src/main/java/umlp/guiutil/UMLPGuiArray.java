/* (c) https://github.com/MontiCore/monticore */
package umlp.guiutil;


import java.util.ArrayList;
import java.util.Collection;

public class UMLPGuiArray<E> extends ArrayList<E> {
  public UMLPGuiArray(int initialCapacity) {
    super(initialCapacity);
  }

  public UMLPGuiArray() {
    super();
  }

  public UMLPGuiArray(Collection<? extends E> c) {
    super(c);
  }
}
