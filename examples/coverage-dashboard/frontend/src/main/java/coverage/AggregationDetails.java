package coverage;

import app.AppRouter;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class AggregationDetails extends AggregationDetailsTOP{

  @Override
  public Consumer<Void> backToOverview(){
    return $ ->{
      AppRouter.navigateToCoverageDashboard();
    };
  }

}
