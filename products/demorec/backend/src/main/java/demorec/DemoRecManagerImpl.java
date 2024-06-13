package demorec;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.common.NotificationScope;

import java.nio.file.Paths;
import java.util.Optional;

public class DemoRecManagerImpl extends DemoRecManagerImplTOP {

  @Override
  protected  boolean _doDeletePhoto(Long gemId, NotificationScope scope){
    Optional<Photo> optPhoto = getPhotoWithoutDoOpt(gemId);
    String fileName = optPhoto.get().getFilename();
    Log.info("Deleting photo with GemId "+gemId, this.getClass().getName());
    if(super._doDeletePhoto(gemId, scope)) {
      boolean deleteSuccessful = deleteActualFile(fileName);
      if(deleteSuccessful) {
        return true;
      } else {
        // Can the system recover from this point?
        Log.error("Something went wrong trying to delete Photo from FileSystem " + fileName);
      }
    }
    /* TODO Shitty State where Photo might exist on FS but not in Fenix App
     Frage: macht es einen Unterschied die Datei vorher zu cachen, erst auf dem FS zu löschen und danach
     aus der Fenix App und falls das löschen aus der Fenix App nicht funktioniert hat die Datei zu recovern?
    */
    return false;
  }

  protected boolean deleteActualFile(String fileName) {
    return Paths.get(DemoRecServerApplication.blobStorageLocation+"/"+fileName).toFile().delete();
  }


  private Optional<Photo> getPhotoWithoutDoOpt(long gemId) {

    if(knownPhotos.containsKey(gemId)){
      return Optional.ofNullable(knownPhotos.get(gemId));
    }
    {
      Optional<Photo> res = DemoRecDAOFactory.getPhotoDAO().selectPhoto(gemId);
      if(res.isPresent()){
        Photo h = res.get();
        if(!h.containsObserver(thePhotoDbObserver)){
          h.addObserver(thePhotoDbObserver);
        }
        knownPhotos.put(h.getGemId(), h);
        return Optional.of(h);
      }
    }
    return Optional.empty();
  }
}
