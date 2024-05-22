package e2e;

import dtcockpit.DtcockpitServerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

public class E2EServerApplication extends DtcockpitServerApplication {
  private static final Logger logger = LoggerFactory.getLogger(E2EServerApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(E2EServerApplication.class, args);
  }

  @Override
  protected void createInitialData() {
    super.createInitialData();
    logger.info("We could define additional data/another testbed here");
  }


}
