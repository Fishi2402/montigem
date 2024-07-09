package functionstructure4fenix;

import java.util.Optional;

public class Function extends FunctionTOP {
  public Optional<Channel> getInChannel(String name){
    for (Port p : getInList()) {
      if (p.getName().equals(name)) {
        Channel channel = p.getChannel();
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }

  public Optional<Channel> getOutChannel(String name){
    for (Port p : getOutList()) {
      if (p.getName().equals(name)) {
        Channel channel = p.getChannel();
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }
}
