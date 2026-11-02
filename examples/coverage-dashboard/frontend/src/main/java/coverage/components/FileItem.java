package coverage.components;

import java.util.function.Consumer;

public class FileItem extends FileItemTOP {

  @Override
  public Consumer<Void> onLeftClick(){
    return $ ->{
      this.getFileSelect().accept(this.getFile());
    };
  }
}
