/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;

import de.se_rwth.commons.logging.Log;
import dtcockpit.pipelines.PipelineInformationExtractor;
import dtcockpit.welcome_board.OutlookConnection;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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


    PipelineInformationExtractor extractor = new PipelineInformationExtractor();

    // Create two default projects, if no projects are present
    App app = DtcockpitManager.getApp();
    if (DtcockpitManager.getProjectList().isEmpty()) {
      Log.println("Creating dummy project data");
      Project monticore = DtcockpitManager.projectBuilder()
          .name("MontiCore")
          .git("monticore/monticore")
          .build().get();

      extractor.extractPipelineDataOf("https://git.rwth-aachen.de/", monticore);
      DtcockpitManager.projectBuilder()
          .name("umlp")
          .git("monticore/umlp")
          .build();
      Group swt = DtcockpitManager.groupBuilder().name("SWT").git("121471").build().get();
      extractor.extractPipelineDataOfAllProjectsInGroup("https://git.rwth-aachen.de/", swt);
      app.setDashboardGroup(swt);
      // TODO: App#getProject should contain all projects by default?
      app.addProject(monticore);
    }

    if(app.getMontiCoreProjects().isEmpty()){
      List<String> projectNames = List.of("monticore", "umlp"); // TODO: all

      for (Project project : DtcockpitManager.getProjectList()) {
        if(projectNames.contains(project.getName().toLowerCase())){
          app.addMontiCoreProjects(project);
        }
      }
    }
    ScheduledExecutorService pipelineScheduler = Executors.newScheduledThreadPool(1);
    pipelineScheduler.scheduleAtFixedRate(() -> updateMontiCorePipelines(extractor, app), 1, 5, TimeUnit.MINUTES);

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

    OutlookConnection outlookConnection = new OutlookConnection();
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(outlookConnection::updateMeetings, 0, 5, TimeUnit.MINUTES);

    // For dev: 2 dummy meetings
    DtcockpitManager.meetingBuilder()
        .start(LocalDateTime.now())
        .end(LocalDateTime.now().plusHours(1))
        .categories(List.of("Vortrag", "Dev")) // Dev entries will not be deleted by connector
        .title("DevTest")
        .description(Optional.of("This is a test"))
        .location(Optional.of("Seminarraum"))
        .build();

    DtcockpitManager.meetingBuilder()
        .start(LocalDateTime.now().plusHours(1))
        .end(LocalDateTime.now().plusHours(2))
        .categories(List.of("Privat", "Dev")) // Dev entries will not be deleted by connector
        .title("DevTest2")
        .description(Optional.of("This is another test that should not be displayed, since it is private"))
        .location(Optional.of("Smartroom"))
        .build();
  }

  private static void updateMontiCorePipelines(PipelineInformationExtractor extractor, App app) {
    List<String> devBranchNames = List.of("dev", "develop");

    for (Project montiCoreProject : app.getMontiCoreProjects()) {
      extractor.extractPipelineDataOf("https://git.rwth-aachen.de/", montiCoreProject);
      Optional<PipelineStatistic> last = montiCoreProject.getPStatistics().stream()
          .filter(it -> devBranchNames.contains(it.getReference()))
          .max(Comparator.comparing(PipelineStatistic::getTime)
                         .thenComparing(PipelineStatistic::getGitlabId)
      );
      if(last.isPresent()) {
        montiCoreProject.setLastPipeline(last.get());
      }
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
