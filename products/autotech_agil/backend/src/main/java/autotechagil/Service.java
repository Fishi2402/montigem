package autotechagil;

public class Service extends ServiceTOP {
  @Override
  public QualityStatus getServiceSelfAssessment() {
    return QualityStatus.RED;
  }
}
