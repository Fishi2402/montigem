package gradle;

import de.se_rwth.commons.logging.Log;
import org.gradle.workers.WorkAction;
import umlp.UMLPTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class UMLPAction implements WorkAction<UMLPParameter> {
    @Override
    public void execute() {
        Log.warn("Running on file " + this.getParameters().getInputFile().getAsFile().get().getName());
        List<String> args = new ArrayList<>();
        args.add("-i");
        args.add(this.getParameters().getInputFile().getAsFile().get().getPath());

        args.add("-o");
        args.add(this.getParameters().getOutputDir().getAsFile().get().getPath());

        args.add("-r");
        args.add(this.getParameters().getReportDir().getAsFile().get().getPath());

        if(this.getParameters().getHwcDir().isPresent()){
            args.add("-hwc");
            args.add(this.getParameters().getHwcDir().getAsFile().get().getPath());
        }

        if(this.getParameters().getMoreArgs().isPresent()){
            args.addAll(this.getParameters().getMoreArgs().get());
        }

        String[] arrayArgs = args.toArray(new String[0]);

        Log.warn(Arrays.toString(arrayArgs));

        UMLPTool.main(arrayArgs);
    }
}
