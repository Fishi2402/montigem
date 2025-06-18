package demorec.processmodeling.wrapper;

import demorec.processmodeling.BPMNFactory;

public class Gateway extends Split {
  private BPMNFactory.SupportedGatewayType type;
  private BPMNFactory.SupportedGatewayDirection direction;

  public Gateway(BPMNFactory.SupportedGatewayType type, String id, BPMNFactory.SupportedGatewayDirection direction) {
    super(id);
    this.type = type;
    this.direction = direction;
  }

  public BPMNFactory.SupportedGatewayType getType() {
    return type;
  }
}
