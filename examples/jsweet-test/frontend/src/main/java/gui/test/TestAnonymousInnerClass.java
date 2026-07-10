package gui.test;

import def.dom.HTMLInputElement;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

import static def.dom.Globals.document;

@Component
public class TestAnonymousInnerClass extends TestAnonymousInnerClassTOP {

  public int x = 10;

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("innerClassResultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  static class HelperClass {
    Consumer<Void> whatever;

    HelperClass(Consumer<Void> whatever) {
      this.whatever = whatever;
    }

    public void myNotification() {
    }
  }

  public class NastyInnerClass {
    public void doSomething() {
      setResultFieldEntry("NastyInnerClass: x=" + x);
    }
  }

  public class NastyInnerClassWithConstructor {
    String myX;

    public NastyInnerClassWithConstructor(int x) {
      myX = "int:" + x;
    }

    public NastyInnerClassWithConstructor(String x) {
      myX = "string:" + x;
    }

    public String getMyX() {
      return myX;
    }
  }

  public Consumer<Void> testAnonymousInnerClass() {
    return (Void) -> {
      final String[] captured = new String[]{"not-called"};
      HelperClass helper = new HelperClass($ -> captured[0] = "consumer-invoked") {
        @Override
        public void myNotification() {
          this.whatever.accept(null);
        }
      };
      helper.myNotification();
      setResultFieldEntry("anonymous: " + captured[0]);
    };
  }

  public Consumer<Void> testAnonymousCapturesLocal() {
    return (Void) -> {
      final String localValue = "captured-local-42";
      Consumer<String> anon = new Consumer<String>() {
        @Override
        public void accept(String prefix) {
          setResultFieldEntry(prefix + localValue);
        }
      };
      anon.accept("anonymousConsumer: ");
    };
  }

  public Consumer<Void> testInnerClassOuterFieldAccess() {
    return (Void) -> {
      NastyInnerClass inner = new NastyInnerClass();
      inner.doSomething();
    };
  }

  public Consumer<Void> testInnerClassCtorInt() {
    return (Void) -> {
      NastyInnerClassWithConstructor inner = new NastyInnerClassWithConstructor(7);
      setResultFieldEntry("ctor: " + inner.getMyX());
    };
  }

  public Consumer<Void> testInnerClassCtorString() {
    return (Void) -> {
      NastyInnerClassWithConstructor inner = new NastyInnerClassWithConstructor("seven");
      setResultFieldEntry("ctor: " + inner.getMyX());
    };
  }

  public Consumer<Void> testResetField() {
    return (Void) -> {
      setResultFieldEntry("");
    };
  }
}
