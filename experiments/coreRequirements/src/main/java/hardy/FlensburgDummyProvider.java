package hardy;

import java.util.Random;

//+ HWC4T
//+ HWC-P(z.B. Demoversion)

// A version of the FlensburgDummyProvider that invents Persons with age 18 to 99
public class FlensburgDummyProvider extends FlensburgDummyProviderTOP{
  public FlensburgDummyProvider(){
    super();
  }

  public FlensburgDummyProvider(Random random){
    super(random);
  }

  @Override
  protected int inventPersonAge(){
    int min = 18;
    int max = 99;
    return min + random.nextInt(max - min);
  }
}
