/* (c) https://github.com/MontiCore/monticore */
package gui.tv;

import umlp.jsweet.extension.annotation.Component;

@Component
public class LastPipelineStat extends LastPipelineStatTOP {

  @Override
  public String getStatusIconSource() {
    String res = this.getP().getLastPipeline().isSuccessful()
        ? "assets/@gitlab/svgs/sprite_icons/status_success.svg"
        : "assets/@gitlab/svgs/sprite_icons/status_failed.svg";
    return res;
  }

  @Override
  public String getStatusIconStatus() {
    String res = this.getP().getLastPipeline().isSuccessful()
        ? "success"
        : "failed";
    return res;
  }

  @Override
  public String getStatusTextValue() {
    String res = this.getP().getLastPipeline().isSuccessful()
        ? "passed"
        : "failed";
    return res;
  }

  @Override
  public String getDurationValue() {
    double totalSeconds = Math.floor((double) this.getP().getLastPipeline().getDuration() / 1000);
    double hours = Math.floor(totalSeconds / 3600);
    double minutes = Math.floor((totalSeconds % 3600) / 60);
    double seconds = totalSeconds % 60;

    String formattedHours = format(hours);
    String formattedMinutes = format(minutes);
    String formattedSeconds = format(seconds);

    return formattedHours + ":" + formattedMinutes + ":" + formattedSeconds;
  }

  protected String format(double num) {
    String s = String.valueOf(num);
    if (s.length() < 2) {
      s = "0" + s;
    }
    return s;
  }

}
