package functionstructure4fenix;

import java.util.Optional;

public class Function extends FunctionTOP {
  public Optional<Channel> getInChannel(String name){
    return getIn().stream().filter(p -> p.getName().equals(name)).map(Port::getChannel).findFirst();
  }

  public Optional<Channel> getOutChannel(String name){
    return getOut().stream().filter(p -> p.getName().equals(name)).map(Port::getChannel).findFirst();
  }
}
