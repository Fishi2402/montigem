package umlp.backendrte.common;

import jsweet.lang.Async;

public abstract class DomainValidationResult {
  Long gemId;
  public DomainValidationResult() {

  }

  @Async
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
