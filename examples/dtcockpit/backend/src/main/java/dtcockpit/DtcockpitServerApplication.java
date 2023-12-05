/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;

import de.se_rwth.commons.logging.Log;
import dtcockpit.montiverse.MontiVerseLite;
import dtcockpit.pipelines.PipelineInformationExtractor;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.service.mqtt.MqttService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "dtcockpit.service", "security"}
)
public class DtcockpitServerApplication extends dtcockpit.DtcockpitServerApplicationTOP {
  private static Set<Server> servers;

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
      Project umlp = DtcockpitManager.projectBuilder()
          .name("umlp")
          .git("monticore/umlp")
          .build().get();
      Group swt = DtcockpitManager.groupBuilder().name("SWT").git("121471").build().get();
      extractor.extractPipelineDataOfAllProjectsInGroup("https://git.rwth-aachen.de/", swt);
      DtcockpitManager.getApp().setDashboardGroup(swt);
      // TODO: App#getProject should contain all projects by default?
      DtcockpitManager.getApp().addProject(monticore);

      MontiVerseLite montiVerseLite = new MontiVerseLite();
      Project montiVerse = montiVerseLite.createProjects();
      extractor.extractPipelineDataOf("https://git.rwth-aachen.de/", montiVerse);
      for (Project project: montiVerse.getDependentProjects()) {
        montiVerseLite.createBranches(project);
      }
    }

    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), DtcockpitServerApplication.class.getName());
      servers = Set.of(webServer);
    }
    catch (SQLException e) {
      throw new IOException(e);
    }

    MqttSerializer.instance = new MqttSerializer();

    MqttService.getInstance().connect();

    SendRoomChangesToMqttObserver mqttRoomObserver = new SendRoomChangesToMqttObserver();
    DtcockpitManager.addObserver(new DtcockpitObserver() {
      @Override
      public void notifyRoomAdded(Room room) {
        room.addObserver(mqttRoomObserver);
      }
    });

    if(DtcockpitManager.getRoomList().isEmpty()) {
      Room rumpeRoom = createRoom(4301);
      Room ageRoom = createRoom(4304);
      Room ckiRoom = createRoom(4315);
      Room smartRoom = createRoom(4311);
      Room seminarRoom = createRoom(4312);
    }
  }

  // For HotReload helpful. Otherwise the h2-webserverport is used when reloading
  // ToDo: How to preserve the most data across reload? e.g. when dataschema is the same
  @PreDestroy
  public void onExit() {
    Log.warn("PreDestroy: Closing DB Connection");
    try {
      DatabaseAccessManager.closeConnection();
    } catch (SQLException ignored) {
    }
    for (Server server : servers) {
      server.stop();
    }
  }
}
