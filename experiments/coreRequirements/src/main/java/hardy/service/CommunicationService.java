package hardy.service;

public abstract class CommunicationService {
  protected static CommunicationService instance;

  public static CommunicationService getInstance() {
    return instance;
  }


}
