package kizam;

import java.io.IOException;

public class KizamServerApplication extends KizamServerApplicationTOP {

  @javax.annotation.PostConstruct
  @Override
  public  void init () throws IOException {
    super.init();
    KizamManager.jobBuilder().helloWorld("Hello World!").build();
  }
}
