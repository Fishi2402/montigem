/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.decorator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._ast.ASTGUIComponent;
import de.se_rwth.commons.Joiners;
import umlp.generator.gui.GUIComponentsAccessVisitor;
import umlp.generator.gui.GUIGeneratorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static umlp.generator.gui.GUIGeneratorUtil.isCreateClass;

public class GUIComponentsDecorator extends GUIAbstractDecorator {

  private final GUIComponentsAccessVisitor viewComponent;

  public GUIComponentsDecorator(final GlobalExtensionManagement glex, GUIComponentsAccessVisitor viewComponent) {
    super(glex);
    this.viewComponent = viewComponent;
  }

  public void transform(ASTGUICompilationUnit guiAst, ASTCDClass decoratedClass) {

    viewComponent.getComponents().stream().filter(GUIGeneratorUtil::isInputComponent)
      .forEach(c ->  {
      if (isCreateClass(decoratedClass)) {
        new InputValueCreateDecorator(glex, guiAst).transform(decoratedClass, c, viewComponent.getComponentName(c));
      } else {
        new InputValueEditDecorator(glex, guiAst).transform(decoratedClass, c, viewComponent.getComponentName(c));
      }
    });

    viewComponent.getComponents().stream().filter(GUIGeneratorUtil::isEventComponent)
        .forEach(c -> new EventDecorator(glex, guiAst).transform(decoratedClass, c, viewComponent.getComponentName(c)));

    filterComponentsWithPredicate(viewComponent.getComponents(), PREDICATE_NOT_TABLE).forEach(c
        -> new CallDecorator(glex, guiAst).transform(decoratedClass, c, viewComponent.getComponentName(c)));


    //------------------ TABLES ----------------------------------------------

    List<HookPoint> res = new ArrayList<>(); // List of HookPoints for table components to use in the init() method
    List<TemplateHookPoint> columnTemplates = new ArrayList<>();
    List<TemplateHookPoint> rowsTemplates = new ArrayList<>();
    List<TableDecorator> tableDecorators = new ArrayList<>();

    filterComponentsWithPredicate(viewComponent.getComponents(), PREDICATE_TABLE).forEach(c ->
        tableDecorators.add(new TableDecorator(glex, guiAst, c, viewComponent.getComponentName(c))));

    tableDecorators.forEach(td -> {
      td.transform(decoratedClass);
      columnTemplates.add(new TemplateHookPoint("umlp.gui2java.InitTableColumns", td.getComponentName(), td.getInitTableColumnsArguments(), guiDSLPrinter));
      rowsTemplates.add(new TemplateHookPoint("umlp.gui2java.InitRows", td.getComponentName(), td.getInitRowsArgument()));
    });

    columnTemplates.forEach(res::add);
    rowsTemplates.forEach(res::add);
    glex.replaceTemplate("umlp.gui2java.InitTables", res);

    //------------------ PIE ----------------------------------------------

    res = new ArrayList<>(); // List of HookPoints for table components to use in the init() method
    List<TemplateHookPoint> pieEntriesTemplates = new ArrayList<>();
    List<PieDecorator> pieDecorators = new ArrayList<>();

    filterComponentsWithPredicate(viewComponent.getComponents(), PREDICATE_PIE).forEach(c ->
        pieDecorators.add(new PieDecorator(glex, guiAst, c, viewComponent.getComponentName(c))));

    pieDecorators.forEach(td -> {
      td.transform(decoratedClass);
      pieEntriesTemplates.add(new TemplateHookPoint("umlp.gui2java.InitPieEntries", td.getComponentName(), Joiners.COMMA.join(td.getInitPieEntriesArguments())));
    });

    pieEntriesTemplates.forEach(res::add);
    glex.replaceTemplate("umlp.gui2java.InitPieCharts", res);
  }

}
