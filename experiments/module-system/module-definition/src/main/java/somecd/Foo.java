/* (c) https://github.com/MontiCore/monticore */

package somecd;

public class Foo extends FooTOP {
  @Override
  public int getHwField() {
    return getAField() * 2;
  }
}
