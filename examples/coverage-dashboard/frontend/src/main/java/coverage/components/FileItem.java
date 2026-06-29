package coverage.components;

import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class FileItem extends FileItemTOP {

  @Override
  public Consumer<Void> onLeftClick(){
    return $ ->{
      this.getFileSelect().accept(this.getFile());
    };
  }
}
