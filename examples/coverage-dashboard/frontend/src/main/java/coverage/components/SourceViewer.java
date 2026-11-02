package coverage.components;

import coveragedashboard.CoverageSourceFile;
import coveragedashboard.commands.GetFileContent;
import umlp.backendrte.command.CommandManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class SourceViewer extends SourceViewerTOP{

  @Override
  public void setFile(Optional<CoverageSourceFile> file){
    super.setFile(file);

    this.updateEditor();
  }

  private void updateEditor(){
    if(this.getFile().isEmpty()) {
      this.setContent("");
      this.setDecoration(new ArrayList<>());
      return;
    }
    CoverageSourceFile file = this.getFile().get();
    this.setContent(CommandManager.executeCommand(new GetFileContent(file.getGemId())).asSimpleResult().get());

    this.setDecoration(SourceViewerUtil.calculateDecoration(file));
    this.setLanguage("java");
  }
}
