package umlp.backendrte.common;

public abstract class DomainValidationResult {
  Long gemId;
  public DomainValidationResult() {

  }
  public DomainValidationResult(long gemId) {
    this.gemId = gemId;
  }

  public abstract boolean hasErrors();
  public abstract String getDomainType();
  public long getGemId() {
    return gemId;
  }

  public boolean hasGemId() {
    return gemId != null;
  }

  public void setGemId(long gemId) {
    this.gemId = gemId;
  }
}
