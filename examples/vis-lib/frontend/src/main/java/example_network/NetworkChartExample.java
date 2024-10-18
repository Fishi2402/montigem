/* (c) https://github.com/MontiCore/monticore */
package example_network;

import umlp.jsweet.extension.annotation.Component;

@Component(
    imports = {"...config.imports", "GemGraphComponent"},
    tsImports = {"GemGraphComponent, @umlp/networkgraph"}
)
public class NetworkChartExample extends NetworkChartExampleTOP {
}
