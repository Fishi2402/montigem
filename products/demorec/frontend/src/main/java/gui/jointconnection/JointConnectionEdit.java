package gui.jointconnection;

import demorec.DemoRecManager;
import demorec.ExternalPhoto;
import demorec.Photo;
import demorec.State;
import gui.connectionkind.JointConnectionKindEditTOP;
import mc.fenix.input.gemdropdowninputtypes.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class JointConnectionEdit extends JointConnectionEditTOP  {

  @Override
  public List<Option> getConditionOptions () {
    List<Option> options = new ArrayList<>();
    options.add(new Option(State.HEALTHY.toString(), State.HEALTHY.name()));
    options.add(new Option(State.OTHER.toString(), State.OTHER.name()));
    return options;
  }

  public Consumer<String> addExternalPhoto() {
    return (String imageUrl) -> {
      ExternalPhoto externalPhoto = DemoRecManager
          .externalPhotoBuilder()
          .externalUrl(imageUrl)
          .build().get();
      this.getConnection().addPhoto(externalPhoto);
    };
  }

  @Override
  public Consumer<Void> removePhotoFromConnection(Photo photo) {
    return $ -> {
      this.getConnection().removePhoto(photo);
      if(photo instanceof ExternalPhoto) {
        DemoRecManager.deleteExternalPhoto(photo.getGemId());
      }
    };
  }
}
