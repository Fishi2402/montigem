package jsweettest;

import umlp.jsweet.extension.annotation.BackendOnly;

public class A extends ATOP {
  @BackendOnly
  public void foo(){
    System.out.println("This should not be callable in the transpiled frontend since it uses backend only classes!");
    System.out.println(JsweettestDAOFactory.getADAO().selectAllIdsA());
  }
}
