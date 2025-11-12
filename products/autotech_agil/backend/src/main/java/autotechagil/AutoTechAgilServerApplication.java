/* (c) https://github.com/MontiCore/monticore */
package autotechagil;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@SpringBootApplication(
    exclude={ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "service"}
)

public class AutoTechAgilServerApplication extends AutoTechAgilServerApplicationTOP {

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    this.createDummyData();
  }

  public void createDummyData() {
    Service cameraService = createService("Camera");
    Service lidarService = createService("LIDAR Sensor");
    Service steeringService = createService("Steering");

    Port portA = createPort("Port A");
    Port portB = createPort("Port B");
    Port portC = createPort("Port C");
    Port portD = createPort("Port D");
    Port portE = createPort("Port E");
    Port portF = createPort("Port F");
    Port portG = createPort("Port G");
    Port portTargetAngleIn = createPort("Target Angle");
    Port portAngleOut = createPort("Angle", createSteeringValueStream());
    Port portMomentOut = createPort("Moment");
    Port portSpeedOut = createPort("Speed");

    cameraService.addAllIns(Arrays.asList(portA, portB));
    cameraService.addOut(portC);

    lidarService.addIn(portD);
    lidarService.addAllOuts(Arrays.asList(portE, portF, portG));

    steeringService.addIn(portTargetAngleIn);
    steeringService.addAllOuts(
        Arrays.asList(portAngleOut, portMomentOut, portSpeedOut)
    );
  }

  protected Service createService(String functionName) {
    return AutoTechAgilManager.serviceBuilder()
        .name(functionName)
        .build()
        .get();
  }

  protected Port createPort(String portName) {
    return AutoTechAgilManager.portBuilder()
        .name(portName)
        .category(AutoTechAgilManager.categoryBuilder()
            .kind(AutoTechAgilManager.dataBuilder().build().get())
            .type(AutoTechAgilManager.typeBuilder().mctype("SteeringValue").build().get())
            .build()
            .get())
        .build()
        .get();
  }

  protected Port createPort(String portName, FunctionStream stream) {
    return AutoTechAgilManager.portBuilder()
        .name(portName)
        .category(AutoTechAgilManager.categoryBuilder()
            .kind(AutoTechAgilManager.dataBuilder().build().get())
            .type(AutoTechAgilManager.typeBuilder().mctype("SteeringValue").build().get())
            .build()
            .get())
        .functionStream(stream)
        .build()
        .get();
  }

  protected FunctionStream createSteeringValueStream() {
    List<Value> values = new ArrayList<>();
    LocalDateTime time = LocalDateTime.now();
    float angle = 0.0f;
    for (int i = 0; i < 7; i++) {
      angle += getRandomFloatValue(angle, angle + 1);
      SteeringData data = AutoTechAgilManager.steeringDataBuilder()
          .angle(getRandomFloatValue(i, i + 1))
          .build()
          .get();
      SteeringQuality quality = AutoTechAgilManager.steeringQualityBuilder()
          .latency(getRandomFloatValue(60, 100))
          .refreshRate(getRandomFloatValue(80, 120))
          .status(QualityStatus.GREEN)
          .build()
          .get();
      time = time.plusNanos(getRandomLongValue(9_000_000L, 10_000_000L));
      values.add(AutoTechAgilManager.steeringValueBuilder()
          .timestamp(time)
          .data(data)
          .quality(quality)
          .build()
          .get()
      );
    }
    for (int i = 7; i < 11; i++) {
      angle += getRandomFloatValue(angle, angle + 1);
      SteeringData data = AutoTechAgilManager.steeringDataBuilder()
          .angle(getRandomFloatValue(i, i + 1))
          .build()
          .get();
      SteeringQuality quality = AutoTechAgilManager.steeringQualityBuilder()
          .latency(getRandomFloatValue(150, 200))
          .refreshRate(getRandomFloatValue(80, 120))
          .status(QualityStatus.YELLOW)
          .build()
          .get();
      time = time.plusNanos(getRandomLongValue(9_000_000L, 10_000_000L));
      values.add(AutoTechAgilManager.steeringValueBuilder()
          .timestamp(time)
          .data(data)
          .quality(quality)
          .build()
          .get()
      );
    }
    angle += getRandomFloatValue(angle, angle + 1);
    SteeringData data = AutoTechAgilManager.steeringDataBuilder()
        .angle(getRandomFloatValue(12, 12 + 1))
        .build()
        .get();
    SteeringQuality quality = AutoTechAgilManager.steeringQualityBuilder()
        .latency(0.0f)
        .refreshRate(0.0f)
        .status(QualityStatus.RED)
        .build()
        .get();
    time = time.plusNanos(getRandomLongValue(9_000_000L, 10_000_000L));
    values.add(AutoTechAgilManager.steeringValueBuilder()
        .timestamp(time)
        .data(data)
        .quality(quality)
        .build()
        .get()
    );
    FunctionStream valueStream = AutoTechAgilManager.functionStreamBuilder().build().get();
    valueStream.addAllValues(values);
    return valueStream;
  }

  private float getRandomFloatValue(float min, float max) {
    Random rand = new Random();
    return rand.nextFloat() * (max - min) + min;
  }

  private long getRandomLongValue(long min, long max) {
    return ThreadLocalRandom.current().nextLong(min, max);
  }

  private QualityStatus getStatusByPercentage(int total, int current) {
    if ((current / (float) total) < 0.60) {
      return QualityStatus.GREEN;
    }
    else if ((current / (float) total) < 0.30) {
      return QualityStatus.YELLOW;
    }
    else {
      return QualityStatus.RED;
    }
  }
}
