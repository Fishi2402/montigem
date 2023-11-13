package dtcockpit;

import de.se_rwth.commons.logging.Log;
import dtcockpit.pipelines.PipelineInformationExtractor;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import umlp.backendrte.service.mqtt.MqttService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "dtcockpit.service", "security"}
)
public class DtcockpitServerApplication extends dtcockpit.DtcockpitServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(DtcockpitServerApplication.class, args);
  }

  public Room createRoom(int roomNumber) {
    return DtcockpitManager.roomBuilder()
        .roomNumber(roomNumber)
        .level(3)
        .temperature(-17.0)
        .humidity(0.0)
        .atmosphericPressure(0.0)
        .motionDetected(false)
        .build().get();
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    // Only create DummyData if database is empty
    if (DtcockpitManager.getUserList().isEmpty()) { // Is there a better way to check if dummy-data was already created?
      Log.println("Creating Dummy Data");
      DtcockpitManager.userBuilder()
          .username("MyUser")
          .password(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("MyPasswort"))
          .registrationDate(LocalDateTime.now())
          .email("unknown")
          .role(DtcockpitRole.ADMIN)
          .build();
    }
    else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }

    // Create two default projects, if no projects are present
    if (DtcockpitManager.getProjectList().isEmpty()) {
      Log.println("Creating dummy project data");
      Project monticore = DtcockpitManager.projectBuilder()
          .name("MontiCore")
          .git("monticore/monticore")
          .build().get();
      PipelineInformationExtractor extractor = new PipelineInformationExtractor();
      extractor.extractPipelineDataOf("https://git.rwth-aachen.de/", monticore);
      DtcockpitManager.projectBuilder()
          .name("umlp")
          .git("https://git.rwth-aachen.de/monticore/umlp")
          .build();
      extractor.extractPipelineDataOfAllProjectsInGroup("https://git.rwth-aachen.de/",
          DtcockpitManager.groupBuilder().name("SWT").git("121471").build().get());
      // TODO: App#getProject should contain all projects by default?
      DtcockpitManager.getApp().addProject(monticore);
    }

    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), DtcockpitServerApplication.class.getName());
    }
    catch (SQLException e) {
      throw new IOException(e);
    }

    MqttSerializer.instance = new MqttSerializer();
    MqttService service = new MqttService();
    service.connect();

    SendRoomChangesToMqttObserver mqttRoomObserver = new SendRoomChangesToMqttObserver();
    DtcockpitManager.addObserver(new DtcockpitObserver() {
      @Override
      public void notifyRoomAdded(Room room) {
        room.addObserver(mqttRoomObserver);
      }
    });

    Room room17 = createRoom(17);
    Room rumpeRoom = createRoom(4301);
    Room ckiRoom = createRoom(4315);
    Room smartRoom = createRoom(4311);
    Room seminarRoom = createRoom(4312);

    // Increase temperature every 10s
    new Timer(false).scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        room17.setTemperature(room17.getTemperature() + 1);
      }
    }, 10_000, 10_000);
  }

}
