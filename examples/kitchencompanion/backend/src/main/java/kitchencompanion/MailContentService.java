package kitchencompanion;

import jsweet.lang.Erased;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
@Erased
public abstract class MailContentService {

  public static String getMailContent(User user) {
    Receipt receipt = user.getReceipts(0);
    String text = "/* English version below */ <br><br>" +
        // Deutsch
        "Hallo " + user.getName() + " " + user.getLastName() + ",<br>" +
        "Ihre KitchenCompanion-Rechnung  vom " + getDate("deu") + " ist verfügbar.<br><br>";
    if (receipt.getPastBalance() > 0) {
      text += "Guthaben: <strong>" + NumberFormat.getNumberInstance(getLocale("deu")).format(0 - receipt.getPastBalance()) + "€" + "</strong><br><br>";
    } else if (receipt.getDebt() > 0) {
      text += "Noch nicht getilgte Schulden: <strong>" + NumberFormat.getNumberInstance(getLocale("deu")).format(0 - receipt.getDebt()) + "€" + "</strong><br><br>";
    }
    text += "Diesen Monat gekauft: <strong>" + NumberFormat.getNumberInstance(getLocale("deu")).format(receipt.getSum()) + "€" + "</strong><br><br>" +
        "Gesamtbetrag: <strong>" + NumberFormat.getNumberInstance(getLocale("deu")).format(receipt.getAmountToPay()) + "€" + "</strong><br><br>" +
        "Mit freundlichen Grüßen<br>" +
        "Ihr KitchenCompanion-System<br><br>" +
        "---<br>" +
        "Diese E-Mail wurde automatisch generiert. Bitte antworten Sie nicht auf diese Adresse.<br>" +
        "---<br>" +
        "Für weitere Fragen wenden Sie sich bitte an will@se-rwth.de <br><br>" +
        "######################################################################## <br><br>" +
        // English
        "Hello " + user.getName() + " " + user.getLastName() + ",<br>" +
        "Your KitchenCompanion invoice from " + getDate("eng") + " is now available.<br><br>";
    if (receipt.getPastBalance() > 0) {
      text += "Credit balance: <strong>"
          + NumberFormat.getNumberInstance(getLocale("eng")).format(0 - receipt.getPastBalance())
          + "€</strong><br><br>";
    } else if (receipt.getDebt() < 0) {
      text += "Outstanding debt: <strong>"
          + NumberFormat.getNumberInstance(getLocale("eng")).format(receipt.getDebt())
          + "€</strong><br><br>";
    }

    text += "Purchased this month: <strong>"
        + NumberFormat.getNumberInstance(getLocale("eng")).format(receipt.getSum())
        + "€</strong><br><br>" +
        "Total amount: <strong>"
        + NumberFormat.getNumberInstance(getLocale("eng")).format(receipt.getSum() + receipt.getDebt() - receipt.getPastBalance())
        + "€</strong><br><br>" +
        "Kind regards,<br>" +
        "Your KitchenCompanion System<br><br>" +
        "---<br>" +
        "This email was generated automatically. Please do not reply to this address.<br>" +
        "---<br>" +
        "For further questions, please contact will@se-rwth.de<br><br>";
    return text;
  }

  public static String getAdminMailContent() {
    return "/* English version below */ <br><br>" +
        "// Deutsch <br>" +
        "Monatliche KitchenCompanion-Abrechnung vom " + getDate("deu") + " – Admin-Übersicht<br><br>" +
        "Gesamte Übersicht aller Nutzer und Beträge:<br><br>" +
        getUserTable("deu") + "<br><br>" +   //
        "Mit freundlichen Grüßen<br>" +
        "Ihr KitchenCompanion System<br><br>" +
        "---<br>" +
        "Diese E-Mail wurde automatisch generiert. Bitte nicht auf dieser antworten antworten.<br>" +
        "---<br><br>" +
        "######################################################################## <br><br><br><br>" +
        "// English<br>" +
        "Monthly KitchenCompanion Billing for " + getDate("eng") + " – Admin Overview<br><br>" +
        "Complete overview of all users and their amounts:<br><br>" +
        getUserTable("eng") + "<br><br>" +
        "Best regards<br>" +
        "Your KitchenCompanion System<br><br>" +
        "---<br>" +
        "This email was generated automatically. Please do not reply.<br>" +
        "---<br>";
  }

  private static String getUserTable(String lang) {
    String adminTable;
    if (lang != "eng") {
      adminTable = "<table border='1' cellspacing='0' cellpadding='6'>" +
          "<tr><th>Name</th><th>Betrag</th><th>Guthaben</th><th>Schulden</th><th>Summe</th></tr>";
    } else {
      adminTable = "<table border='1' cellspacing='0' cellpadding='6'>" +
          "<tr><th>Name</th><th>Value</th><th>Balance</th><th>Debt</th><th>Total</th></tr>";
    }
    for (User u : sortUsersList(KitchenCompanionManager.getUserList())) {
      if(!u.getReceiptsList().isEmpty()) {
        Receipt receipt = u.getReceipts(0);
          adminTable += "<tr><td>" + u.getName() + " " + u.getLastName() + "</td><td>"
              + NumberFormat.getNumberInstance(getLocale(lang)).format(receipt.getSum()) + " €</td><td>"
              + NumberFormat.getNumberInstance(getLocale(lang)).format(0 - receipt.getPastBalance()) + " €</td><td>"
              + NumberFormat.getNumberInstance(getLocale(lang)).format(receipt.getDebt())
              + " €</td><td>" + NumberFormat.getNumberInstance(getLocale(lang)).format(receipt.getAmountToPay())
              + " €</td></tr>";
      } else{
        adminTable += "<tr><td>" + u.getName() + " " + u.getLastName() + "</td><td>"
            + NumberFormat.getNumberInstance(getLocale(lang)).format(0) + " €</td><td>"
            + NumberFormat.getNumberInstance(getLocale(lang)).format(u.getBalance()) + " €</td><td>"
            + NumberFormat.getNumberInstance(getLocale(lang)).format(0)
            + " €</td><td>" + NumberFormat.getNumberInstance(getLocale(lang)).format(0)
            + " €</td></tr>";
      }
    }
    return adminTable + "</table>";
  }

  public static String getDate(String lang) {
    LocalDateTime date = LocalDateTime.now().minusMonths(1);
    return date.getMonth().getDisplayName(TextStyle.FULL, getLocale(lang)) + " " + date.getYear();
  }

  private static Locale getLocale(String lang) {
    if (lang == "eng") {
      return Locale.ENGLISH;
    } else {
      return Locale.GERMAN;
    }
  }
  private static List<User> sortUsersList(List<User> userList) {
    List<User> users = new ArrayList<>(userList);
    int n = users.size();
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - i - 1; j++) {
        User a = users.get(j);
        User b = users.get(j + 1);

        boolean shouldSwap = false;
        int prioA = getPriority(a);
        int prioB = getPriority(b);

        if (prioA != prioB) {
          shouldSwap = prioA < prioB;
        } else {
          int cmpLast = a.getLastName().compareToIgnoreCase(b.getLastName());
          if (cmpLast > 0) {
            shouldSwap = true;
          } else if (cmpLast == 0) {
            int cmpFirst = a.getName().compareToIgnoreCase(b.getName());
            shouldSwap = cmpFirst > 0;
          }
        }
        if (shouldSwap) {
          users.set(j, b);
          users.set(j + 1, a);
        }
      }
    }
    return users;
  }
  private static int getPriority(User u) {
    if (u.getLastName().compareToIgnoreCase("Rumpe") == 0) {
      return 2;
    }
    return 0;
  }
}
