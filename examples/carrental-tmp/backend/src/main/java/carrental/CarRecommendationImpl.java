package carrental;

public class CarRecommendationImpl extends CarRecommendationImplTOP {
  @Override
  public String getSalesPitch() {
    return "HURRY UP TODAY AND GET $123 OFF YOUR RENTAL!";
  }

  @Override
  public float getEstimatedCostPerMile() {
    return 0;
  }
}
