package coverage;

import coveragedashboard.CoverageSourceFile;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CoverageDetails extends CoverageDetailsTOP {
  // Add hwc

  @Override
  public Consumer<CoverageSourceFile> onFileSelect(){
    return file ->{
      this.setSelectedFile(Optional.of(file));
    };
  }
}
