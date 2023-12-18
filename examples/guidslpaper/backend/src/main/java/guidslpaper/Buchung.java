package guidslpaper;

import java.time.format.DateTimeFormatter;

public class Buchung extends BuchungTOP {

  @Override
  public String getLfdeNummer() {
    return String.format("%s/%d", this.getBuchungsdatum().format(DateTimeFormatter.ofPattern("yy")), getGemId());
  }


}
