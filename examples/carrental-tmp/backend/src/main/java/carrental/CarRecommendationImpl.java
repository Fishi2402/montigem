package carrental;

public class CarRecommendationImpl extends CarRecommendationImplTOP {
  @Override
  public String calcSalesPitch() {
    return "HURRY UP TODAY AND GET $123 OFF YOUR RENTAL!";
  }

  @Override
  public float calcEstimatedCostPerMile() {
    return 0;
  }
}
