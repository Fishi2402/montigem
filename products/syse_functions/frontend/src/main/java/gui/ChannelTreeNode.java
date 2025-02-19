package gui;

import functionstructure4fenix.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChannelTreeNode extends ChannelTreeNodeTOP{

  public Consumer<Void> toggleSelectedChannel(){
    return () -> {
      List<Channel> selectedChannels = new ArrayList<>(getSelectedChannels());
      if (getSelectedChannels().contains(getChannel())) {
        selectedChannels.remove(getChannel());
      } else {
        selectedChannels.add(getChannel());
      }
      setSelectedChannels(selectedChannels);
    };
  }

  public boolean isChannelSelected(){
    return getSelectedChannels().contains(getChannel());
  }
}
