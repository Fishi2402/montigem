package demorec;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.common.NotificationScope;

import java.nio.file.Paths;
import java.util.Optional;

public class DemoRecManagerImpl extends DemoRecManagerImplTOP {

  @Override
  protected  boolean _doDeleteLocalPhoto(Long gemId, NotificationScope scope){
    Optional<LocalPhoto> optLocalPhoto = getPhotoWithoutDoOpt(gemId);
    String fileName = optLocalPhoto.get().getFilename();
    Log.info("Deleting photo with GemId "+gemId, this.getClass().getName());
    if(super._doDeleteLocalPhoto(gemId, scope)) {
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


  private Optional<LocalPhoto> getPhotoWithoutDoOpt(long gemId) {

    if(knownLocalPhotos.containsKey(gemId)){
      return Optional.ofNullable(knownLocalPhotos.get(gemId));
    }
    {
      Optional<LocalPhoto> res = DemoRecDAOFactory.getLocalPhotoDAO().selectLocalPhoto(gemId);
      if(res.isPresent()){
        LocalPhoto h = res.get();
        if(!h.containsObserver(theLocalPhotoDbObserver)){
          h.addObserver(theLocalPhotoDbObserver);
        }
        knownLocalPhotos.put(h.getGemId(), h);
        return Optional.of(h);
      }
    }
    return Optional.empty();
  }
}
