package hardy.error.mock;

// TODO AHe: from a MontiGem dependency, check if still needed
public enum Status implements StatusType{
  FORBIDDEN,
  UNAUTHORIZED,
  BAD_REQUEST,
  NOT_ACCEPTABLE,
  EXPECTATION_FAILED,
  SERVICE_UNAVAILABLE
}
