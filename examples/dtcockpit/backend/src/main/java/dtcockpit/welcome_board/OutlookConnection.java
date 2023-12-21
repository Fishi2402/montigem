package dtcockpit.welcome_board;

import de.se_rwth.commons.logging.Log;
import dtcockpit.App;
import dtcockpit.DtcockpitManager;
import dtcockpit.Meeting;
import dtcockpit.MeetingDeSer;
import dtcockpit.MessageOfTheDay;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import org.jsoup.Jsoup;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class OutlookConnection {
  private ExchangeService service;

  private static void clearOldMeetings() {
    Log.info("Clearing old meetings", "OutlookConnection");
    List<Long> gemIds = new ArrayList<>();
    for (Meeting m : DtcockpitManager.getMeetingList()) {
      if (!m.getCategoriesList().contains("Dev")) {
        gemIds.add(m.getGemId());
      }
    }

    for (Long gemId : gemIds) {
      DtcockpitManager.deleteMeeting(gemId);
    }

    Log.info("Cleared old meetings", "OutlookConnection");
  }

  public void updateMeetings() {
    Log.info("Loading Meetings from exchange", "OutlookConnection");
    try {
      clearOldMeetings();
      ensureServiceCreated();
      buildMeetingsFromExchange();
      updateMessageOfTheDay();
      updateDisplayedMeetings();
    } catch (Throwable e) {
      Log.warn("Error while loading meetings from exchange", e);
    }
    Log.info("Finished loading Meetings from exchange", "OutlookConnection");
  }

  protected void updateDisplayedMeetings() {
    Log.info("Updating displayedMeetings", "OutlookConnection");
    LocalDateTime now = LocalDateTime.now();
    App app = DtcockpitManager.getApp();
    app.getDisplayedMeetings().clear();

    List<Meeting> displayedMeetings = DtcockpitManager.getMeetingList().stream()
        .filter(it -> !it.isEmptyCategories())
        .filter(it -> !it.containsCategories("Privat"))
        .filter(it -> !it.containsCategories("Ankündigung"))
        .filter(it -> it.getEnd().isAfter(now))
        .collect(Collectors.toList());

    app.getDisplayedMeetings().addAll(displayedMeetings);
    Log.info(displayedMeetings.size() + " meetings will be displayed", "OutlookConnection");
  }

  protected void updateMessageOfTheDay() {
    Log.info("Updating MessageOfTheDay", "OutlookConnection");
    App app = DtcockpitManager.getApp();

    for (Meeting meeting : DtcockpitManager.getMeetingList()) {
      LocalDateTime now = LocalDateTime.now();
      if (meeting.containsCategories("Ankündigung")
          && meeting.getStart().isBefore(now)
          && meeting.getEnd().isAfter(now)
      ) {
        String newMsg = meeting.getTitle();
        if (!app.getCurrentMessageGem().isPresent() || !app.getCurrentMessage().getMessage().equals(newMsg)) {
          Log.info("Replacing MessageOfTheDay", "OutlookConnection");
          MessageOfTheDay tmp = DtcockpitManager.messageOfTheDayBuilder()
              .message(newMsg).date(now.toLocalDate()).build().get();

          app.setCurrentMessage(tmp);
        }
      }
    }
    Log.info("Updated MessageOfTheDay", "OutlookConnection");
  }

  private void buildMeetingsFromExchange() throws Exception {
    Log.info("Building meetings from Appointments", "OutlookConnection");
    CalendarFolder folder = CalendarFolder.bind(service, WellKnownFolderName.Calendar);
    Instant start = new Date().toInstant().minus(1, ChronoUnit.DAYS);
    Instant end = new Date().toInstant().plus(7, ChronoUnit.DAYS);
    FindItemsResults<Appointment> appointments = folder.findAppointments(new CalendarView(Date.from(start), Date.from(end)));

    Log.info("Loaded " + appointments.getTotalCount() + " Appointments from exchange", "OutlookConnection");

    for (Appointment appointment : appointments) {
      appointment.load(new PropertySet(
          AppointmentSchema.Subject,
          AppointmentSchema.Body,
          AppointmentSchema.Start,
          AppointmentSchema.End,
          AppointmentSchema.Categories,
          AppointmentSchema.Location
      ));
      Date appStart = appointment.getStart();
      Date appEnd = appointment.getEnd();
      String subject = appointment.getSubject();
      String body;

      MessageBody messageBody = appointment.getBody();
      if (messageBody.getBodyType().equals(BodyType.HTML)) {
        body = Jsoup.parse(messageBody.toString()).wholeText();
      } else {
        body = messageBody.toString();
      }

      List<String> categories = new ArrayList<>();
      for (String category : appointment.getCategories()) {
        categories.add(category);
      }

      var m = DtcockpitManager.meetingBuilder()
          .start(LocalDateTime.from(appStart.toInstant().atZone(TimeZone.getTimeZone("Europe/Berlin").toZoneId())))
          .end(LocalDateTime.from(appEnd.toInstant().atZone(TimeZone.getTimeZone("Europe/Berlin").toZoneId())))
          .title(subject)
          .description(Optional.ofNullable(body))
          .categories(categories)
          .location(Optional.ofNullable(appointment.getLocation()))
          .build();
      Log.info("Build meeting " + m.map(it -> new MeetingDeSer().serialize(it)), "OutlookConnection");
    }
    Log.info("Build meetings from Appointments", "OutlookConnection");
  }

  private void ensureServiceCreated() {
    if (service == null) {
      Log.info("Creating ExchangeService", "OutlookConnection");
      service = new ExchangeService();
      ExchangeCredentials credentials = new WebCredentials(
          System.getenv("WELCOME_BOARD_EXCHANGE_USER"),
          System.getenv("WELCOME_BOARD_EXCHANGE_PASSWORD")
      );
      service.setCredentials(credentials);
      service.setUrl(URI.create("https://mail.rwth-aachen.de/EWS/Exchange.asmx"));
      service.setTimeout(10 * 1000);
      Log.info("Created ExchangeService", "OutlookConnection");
    }
  }
}
