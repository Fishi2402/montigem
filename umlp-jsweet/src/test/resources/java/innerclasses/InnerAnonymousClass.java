package innerclasses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class InnerAnonymousClass {

  public int x = 10;

  public void doSomething() {

    ArrayList<HelperClass> myList = new ArrayList<>();
    ArrayList<HelperClass2> myList2 = new ArrayList<>();

    myList.add(new HelperClass($ -> {}) {
      @Override
      public void myNotification() {
        this.whatever.accept(null);
      }
    });

    myList2.add(new HelperClass2() {
      @Override
      public void myNotification() {
        this.whatever.accept(null);
      }
    });

    NastyInnerClass myNastInnerClass = new NastyInnerClass();
    myNastInnerClass.doNothing();
  }

  public class NastyInnerClass{

    ArrayList<HelperClass> innerMyList = new ArrayList<>();

    public void doNothing() {
      System.out.println(x);
    }
  }

  public class NastyInnerClassWithConstructor{

    ArrayList<HelperClass> innerMyList = new ArrayList<>();

    String myX;

    public NastyInnerClassWithConstructor(int x) {
      myX = ""+x;
    }

    public NastyInnerClassWithConstructor(String x) {
      myX = x;
    }

    public void doNothing() {
      System.out.println(x);
    }
  }

}
