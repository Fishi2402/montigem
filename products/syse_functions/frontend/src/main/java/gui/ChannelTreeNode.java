package gui;

import functionstructure4fenix.Channel;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChannelTreeNode extends ChannelTreeNodeTOP{

  @Override
  public void setChannelSelectionChecked(Boolean selected){
    List<Channel> selectedChannels = new ArrayList<>(getSelectedChannels());
    if (selected) {
      selectedChannels.remove(getChannel());
    } else {
      selectedChannels.add(getChannel());
    }
    setSelectedChannels(selectedChannels);
  }

  @Override
  public Boolean getChannelSelectionChecked(){
    return getSelectedChannels().contains(getChannel());
  }
}
