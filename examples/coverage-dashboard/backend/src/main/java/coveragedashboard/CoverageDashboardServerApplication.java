package coveragedashboard;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;

import java.io.IOException;

public class CoverageDashboardServerApplication extends CoverageDashboardServerApplicationTOP {
  public static void main(String[] args) {
    SpringApplication.run(CoverageDashboardServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    createDummyData();
  }

  public void createDummyData(){
    CoverageDashboardManager.dummyContentBuilder()
        .content("0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789")
        .build();
  }
}


