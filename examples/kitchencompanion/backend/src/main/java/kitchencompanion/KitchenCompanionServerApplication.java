package kitchencompanion;

import de.se_rwth.commons.logging.Log;
import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.sql.SQLException;

public class KitchenCompanionServerApplication extends KitchenCompanionServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(KitchenCompanionServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    // Only create DummyData if database is empty
    if (KitchenCompanionManager.getUserList().isEmpty()) { // Is there a better way to check if dummy-data was already created?
      createObjects();
    } else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }


    try {
      Server webServer = Server.createWebServer("-web", "-webAllowOthers").start();
      Log.info(String.format("H2 Web Server started at: http://localhost:%s", webServer.getPort()), KitchenCompanionServerApplication.class.getName());
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }

  public void createObjects() {
    if (KitchenCompanionManager.getSearchList().isEmpty()) {
      KitchenCompanionManager.searchBuilder().searchStr("").searched(false).build();
    }


    if (KitchenCompanionManager.getUserList().isEmpty()) {
      Log.println("Creating Dummy Data");
      // VIP Users
      createUser("Bernhard", "Rumpe", "rumpe@se-rwth.de", "BR", 0.0, 0.0, KitchenCompanionRole.VIP);
      createUser("Manfred", "Nagl", "nagl@i3.informatik.rwth-aachen.de", "MN", 0.0, 0.0, KitchenCompanionRole.VIP);
      //Admin Users
      createUser("Sebastian", "Will", "will@se-rwth.de", "SWi", 0.0, 0.0, KitchenCompanionRole.ADMIN);
      //Normal Users
      createUser("Arkadii", "Gerasimov", "gerasimov@se-rwth.de", "AGe", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Alexander", "Hellwig", "hellwig@se-rwth.de", "AHe", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Alexander", "Lüpges", "luepges@se-rwth.de", "ALu", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Adrian", "Marin", "marin@se-rwth.de", "AMa", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Constantin", "Buschhaus", "buschhaus@se-rwth.de", "CBu", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Deni", "Raco", "raco@se-rwth.de", "DRa", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("David", "Schmalzing", "schmalzing@se-rwth.de", "DS", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Finn", "Kunert", "kunert@se-rwth.de", "FKu", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Florian", "Drux", "drux@se-rwth.de", "FDr", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Fabian", "Schaak", "schaak@se-rwth.de", "FSc", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Felix", "Steinfurth", "steinfurth@se-rwth.de", "FSt", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Galina", "Volkova", "volkova@se-rwth.de", "GV", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Gordana", "Zivkovic", "zivkovic@se-rwth.de", "GZ", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Hendrik", "Kausch", "kausch@se-rwth.de", "HKa", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Haron", "Nqiri", "nqiri@se-rwth.de", "HN", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Henrik", "Thillmann", "thillmann@se-rwth.de", "HT", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Joel", "Charles", "charles@se-rwth.de", "JC", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Janik", "Rapp", "rapp@se-rwth.de", "JRa", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("", "Lehrstuhl", "sek@se-rwth.de", "Lehrstuhl", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Louis", "Wachtmeister", "wachtmeister@se-rwth.de", "LW", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Lucas", "Wollenhaupt", "wollenhaupt@se-rwth.de", "LWo", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Luis", "Zech", "zech@se-rwth.de", "LZ", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Malte", "Heithoff", "heithoff@se-rwth.de", "MH", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Mathias", "Pfeiffer", "mpfeiffer@se-rwth.de", "MPf", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Max", "Stachon", "stachon@se-rwth.de", "MSh", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Marc", "Schmidt", "schmidt@se-rwth.de", "MSm", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Nico", "Jansen", "jansen@se-rwth.de", "NJ", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Paul", "Hufnagel", "hufnagel@se-rwth.de", "PHu", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Pascal", "Huppertz", "huppertz@se-rwth.de", "PHz", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Rick", "van Breugel", "vanBreugel@se-rwth.de", "RvB", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Sedat", "Cakici", "cakici@se-rwth.de", "SCa", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Sylvia", "Gunder", "gunder@se-rwth.de", "SG", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Tom", "Bursch", "bursch@se-rwth.de", "TBu", 0.0, 0.0, KitchenCompanionRole.USER);
      createUser("Vincent", "Bertram", "bertram@se-rwth.de", "VB", 0.0, 0.0, KitchenCompanionRole.USER);


      Log.println("Offers");
      // offers examples
      createOffer("Kaffee", 0.25, "assets/images/Kaffeebohnen.png", true, ProductType.DRINK);
      createOffer("Wasser", 0.75, "assets/images/Water.png", true, ProductType.DRINK);
      createOffer("Coca Cola/ Coca Cola Zero", 0.5, "assets/images/Cola.png", true, ProductType.DRINK);
      createOffer("Mate", 1.0, "assets/images/Mate.png", true, ProductType.DRINK);
      createOffer("Erdnüsse", 1.0, "assets/images/Erdnuesse.png", true, ProductType.SNACK);
      createOffer("Müsliriegel/ Fruchtriegel", 0.25, "assets/images/Muesli_Fruchtriegel.png", true, ProductType.SNACK);
      createOffer("Instant Nudeln", 0.40,"assets/images/InstantNudeln.png",true, ProductType.SNACK);

      for (User u : KitchenCompanionManager.getUserList()) {
        for (Offer o : KitchenCompanionManager.getOfferList()) {
          if (o.isInStock()) {
            u.addTallies(KitchenCompanionManager.tallyBuilder().count(0).offer(o).build().get());
            u.addPrevTallies(KitchenCompanionManager.tallyBuilder().count(0).offer(o).build().get());
          }
        }
      }
    } else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }
  }

  private void createUser(String firstName, String lastName, String email, String nickname, double sum, double balance, KitchenCompanionRole role) {
    KitchenCompanionManager.userBuilder()
        .name(firstName)
        .lastName(lastName)
        .email(email)
        .nickname(nickname)
        .balance(balance)
        .sum(sum)
        .role(role)
        .build();
  }

  private void createOffer(String name, double price, String imgSource, boolean inStock, ProductType productType) {
    KitchenCompanionManager.offerBuilder()
        .name(name)
        .price(price)
        .imgSource(imgSource)
        .inStock(inStock)
        .productType(productType)
        .build();
  }
}
