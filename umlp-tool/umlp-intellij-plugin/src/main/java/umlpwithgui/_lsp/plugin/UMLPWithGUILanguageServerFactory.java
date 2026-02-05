package umlpwithgui._lsp.plugin;

import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl;
import com.redhat.devtools.lsp4ij.client.features.LSPClientFeatures;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ProgressParams;
import org.eclipse.lsp4j.WorkDoneProgressCreateParams;
import org.eclipse.lsp4j.WorkDoneProgressKind;
import org.eclipse.lsp4j.WorkDoneProgressNotification;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UMLPWithGUILanguageServerFactory extends UMLPWithGUILanguageServerFactoryTOP {
  public static final String LANGUAGE_SERVER_INIT_WORK_DONE_TOKEN = "language-server-init-work-done-token";
  public static AtomicBoolean initDone =  new AtomicBoolean(false);
  public static Set<String> finishedIndexing = Collections.synchronizedSet(new HashSet<>());

  @Override
  public @NotNull LSPClientFeatures createClientFeatures() {
    return new LSPClientFeatures(){
      @Override
      public void initializeParams(@NotNull InitializeParams initializeParams) {
        initializeParams.setWorkDoneToken(LANGUAGE_SERVER_INIT_WORK_DONE_TOKEN);
      }
    };
  }

  @Override
  public @NotNull LanguageClientImpl createLanguageClient(@NotNull Project project) {
    initDone.set(false);
    LanguageClientImpl res = new LanguageClientImpl(project) {
      Pattern indexingFinishedPattern = Pattern.compile("Indexing finished for '(.*)'");

      @Override
      public void logMessage(MessageParams message) {
        super.logMessage(message);
        if(message == null) return;
        if(!MessageType.Log.equals(message.getType())) return;
        if(message.getMessage() == null) return;

        Matcher m = indexingFinishedPattern.matcher(message.getMessage());
        if(!m.matches()) return;

        String dir = m.group(1);
        finishedIndexing.add(dir);
      }

      @Override
      public void notifyProgress(ProgressParams params) {
        super.notifyProgress(params);

        if (params.getToken() != null && params.getToken().isLeft()) {
          if (LANGUAGE_SERVER_INIT_WORK_DONE_TOKEN.equals(params.getToken().getLeft())) {
            if (params.getValue() != null && params.getValue().isLeft()) {
              WorkDoneProgressNotification wpn = params.getValue().getLeft();
              if (wpn != null && wpn.getKind() == WorkDoneProgressKind.end) {
                System.out.println("Initialization done");
                initDone.set(true);
              }
            }
          }
        }


      }
    };

    res.createProgress(new WorkDoneProgressCreateParams(Either.forLeft(LANGUAGE_SERVER_INIT_WORK_DONE_TOKEN)));
    return res;
  }
}
