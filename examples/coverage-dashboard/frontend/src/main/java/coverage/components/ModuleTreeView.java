package coverage.components;

import coveragedashboard.CoverageSourceFile;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ModuleTreeView extends ModuleTreeViewTOP{


  @Override
  public Consumer<Void> onFileNodeClick(CoverageSourceFile file){
    return $->{
      this.getFileSelect().accept(file);
    };
  }
}
