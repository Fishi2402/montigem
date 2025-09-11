package serverperformance;

import com.google.common.base.Stopwatch;
import de.se_rwth.commons.logging.Log;
import org.springframework.shell.Availability;
import org.springframework.web.socket.WebSocketHttpHeaders;
import umlp.backendrte.command.ClientCommandManager;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.service.websocket.ClientCommandWebsocketCommunication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Client {
  Random random = new Random();
  ClientCommandWebsocketCommunication clientCommandWebsocketCommunication;

  Set<C1> knownObjectsSet = new HashSet<>();
  List<C1> knownObjects = new ArrayList<>();

  public Client(String uri){
    // Connect to server via websocket
    ServerPerformanceManagerClientImpl.init();
    System.out.println("ServerPerformanceManagerClientImpl@" + ServerPerformanceManagerClientImpl.class.hashCode());
    doInit(uri, null, null);
  }

  protected void doInit(String uri, String user, String password) {
    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    if (user != null && password != null && !user.isEmpty() && !password.isEmpty()) {
      headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8)));
    }

    clientCommandWebsocketCommunication = new ClientCommandWebsocketCommunication(uri, headers)
        .onCommandReceived(cmd -> {
          String msg = String.format("Received sync command '%s' from server: %s", cmd.getCommandType(), CommandObjectMapper.serializeCommand(cmd));
          //Log.info(msg, Client.class.getName());
        });
    ClientCommandManager commandManager = new ClientCommandManager(clientCommandWebsocketCommunication);
    if (commandManager.isInitSuccessFul()) {
      ClientCommandManager.initMe(commandManager);
      Log.info(String.format("Websocket connection to server with uri '%s' was successful. Participant ID: %s",
          uri, CommandManager.getParticipantId()), Client.class.getName());
    } else {
      Log.info(String.format("Could not establish a websocket connection to server with uri '%s', please check your parameters",
          uri), Client.class.getName());
    }
  }

  public void close() throws IOException {
    if(clientCommandWebsocketCommunication != null){
      if(clientCommandWebsocketCommunication.isOpen()){
        clientCommandWebsocketCommunication.close();
      }
    }
  }

  enum Action {
    EDIT_FIELD,
    EDIT_LINK,
    CREATE_OBJECT,
    LOAD_OBJECT
  }

  /**
   * A method that is executed every x seconds
   */
  public int execRandomAction(){
    Stopwatch s = Stopwatch.createStarted();
    Action a = Action.values()[random.nextInt(Action.values().length)];

    if(a == Action.EDIT_FIELD){
      editRandomField();
    } else if(a == Action.EDIT_LINK){
      editRandomLink();
    } else if(a == Action.CREATE_OBJECT){
      createObject();
    } else if(a == Action.LOAD_OBJECT){
      loadRandomObject();
    }

    return (int) s.elapsed(TimeUnit.MILLISECONDS);
  }

  private void editRandomField() {
    if(knownObjects.isEmpty()){
      return;
    }

    C1 inst = knownObjects.get(random.nextInt(knownObjects.size()));
    if(random.nextBoolean()){
      inst.setF1(inst.getF1() + 1);
    } else {
      inst.addF4(inst.sizeF4(), 42);
      // TODO: remove, set
    }
  }


  private void editRandomLink() {
    if(knownObjects.isEmpty()){
      return;
    }

    C1 source = knownObjects.get(random.nextInt(knownObjects.size()));
    C1 target = knownObjects.get(random.nextInt(knownObjects.size()));

    int assoc = random.nextInt(6);
    if(assoc == 0){
      source.setA1_rhs(target);
    } else if(assoc == 1) {
      source.addA2_rhs(target);
    } else if(assoc == 2){
      source.addA3_rhs(target);
    } else if(assoc == 3){
      source.setA4_rhs(target);
    } else if(assoc == 4){
      source.addA5_rhs(target);
    } else if(assoc == 5){
      source.addA6_rhs(target);
    }

    // TODO: set, remove for lists; absent for optional
  }

  private void createObject() {
    C1 o = ServerPerformanceManager.c1Builder().f1(1).f4(List.of(2)).build().get();
    if(knownObjectsSet.add(o)) {
      knownObjects.add(o);
    }
  }

  private void loadRandomObject() {
    int bound = (int) ServerPerformanceManager.getFreeGemId();
    if(bound == 0){
      return;
    }

    int gemid = random.nextInt(bound);
    Optional<C1> o = ServerPerformanceManager.c1Opt(gemid);

    if(o.isPresent()){
      knownObjects.add(o.get());
    }
  }
}
