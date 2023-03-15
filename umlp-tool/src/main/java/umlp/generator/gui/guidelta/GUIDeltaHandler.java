package umlp.generator.gui.guidelta;

import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.*;
import de.monticore.guidsl._visitor.GUIDSLHandler;
import de.monticore.guidsl._visitor.GUIDSLTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GUIDeltaHandler implements GUIDSLHandler {

    private GUIDSLTraverser traverser;

    private final ASTGUICompilationUnit adaptedAST;
    private ASTGUIArgValue argValueToAdd = null; //stores the new ArgValue to insert
    private String componentNameToChange = null; //the Name of the Component needs to change with the current GUIDelta statement

    private AdaptionOptions adaptionOptions = null; //stores which operation to perform

    List<ASTMCImportStatement> imports = null; //stores the imports of the Delta AST

    /**
     * Used to save the four different operations between methods. NONE is used as an error state and should not be used in other cases
     */
    private enum AdaptionOptions{
        REMOVE,
        REPLACE,
        AFTER,
        BEFORE,
        NONE
    }

    @Override
    public GUIDSLTraverser getTraverser() {
        return traverser;
    }

    @Override
    public void setTraverser(GUIDSLTraverser traverser) {
        this.traverser = traverser;
    }

    /**
     * The constructor fot GUIDelta handler
     * @param adaptedAST the AST that should be adapted when traversing a guiDelta, correct function is only guaranteed
     *                   when the GUIDeltaHandler is traversing a DeltaAST that does adapt specifically the adaptedAST.
     */
    public GUIDeltaHandler(ASTGUICompilationUnit adaptedAST) {
        traverser = GUIDSLMill.traverser();
        traverser.setGUIDSLHandler(this);
        this.adaptedAST = adaptedAST;
    }

    /**
     * The ASTGUICompilationUnit contains the List of imports,
     * for a working model after the adaption imports in the GUIDelta AST need to be added to the adapted (non-GUIDelta) AST if not already present.
     * This is accomplished by saving the List of imports to the respective instance variable for later use in the traversal of the adapted AST.
     * The factor to differentiate the two ASTs is the variable componentNameToChange as it is set before the adapted ast is traversed and reset to null right after.
     * @param node the current node in the AST
     */
    public void handle (ASTGUICompilationUnit node){
        if(componentNameToChange == null){ //GUIDeltaAST
            imports = node.getMCImportStatementList();
        }
        else{ //AST to adapt, imports need to be added if not already existing
            for(ASTMCImportStatement importStatement : imports){
                if(node.getMCImportStatementList().stream().noneMatch(i -> i.getQName().equals(importStatement.getQName()))) //import statement has no equals defined so contains doesnt work
                    node.addMCImportStatement(importStatement);
            }
        }
        GUIDSLHandler.super.handle(node); //Maybe not the best
    }

    /**
     * handles the ASTGUIAdapt statement, this statement has three different operations that can be identified by the node.getPosition() method
     * this method saves them and the argValue that should be added as well as the component name to search for
     * to the respective instance variable for later use in the traversal
     * Then the AST that should be adapted is traversed to change that AST according to the GUIAdapt.
     * @param node the current node in the AST
     */
    public void handle (ASTGUIAdapt node) {

        componentNameToChange = node.getName();

        switch (node.getPosition()){
            case 2:
                adaptionOptions = AdaptionOptions.BEFORE;
                break;
            case 1:
                adaptionOptions = AdaptionOptions.AFTER;
                break;
            case 6:
                adaptionOptions = AdaptionOptions.REPLACE;
                break;
            default:
                adaptionOptions = AdaptionOptions.NONE;
                break;
        }

        argValueToAdd = node.getGUIArgValue();

        adaptedAST.accept(getTraverser());

        componentNameToChange = null; //reset in case there are more adaptions
    }

    /**
     * works the same as handle(ASTGUIAdapt) except there is only a single operator (remove) that does not add an argValue
     * @param node the current node in the AST
     */
    public  void handle (ASTGUIRemove node)
    {
        //Needed method to differentiate referenceParam

        componentNameToChange = node.getName();

        adaptionOptions = AdaptionOptions.REMOVE;
        argValueToAdd = null;

        adaptedAST.accept(getTraverser());

        componentNameToChange = null; //reset in case there are more adaptions
    }

    /**
     * If the guiComponent, guiIterate or guiGuard with the correct name (componentNameToChange) is present in this GUIList
     * the method will perform the operation specified before in adaptionOptions.
     * Otherwise, the children are traversed normally this is also the case when traversing a GUIDelta AST
     * The factor to differentiate the two ASTs is the variable componentNameToChange as it is set before the adapted ast is traversed and reset to null right after
     * @param node the current node in the AST
     */
    public void handle(ASTGUIList node){
        if(componentNameToChange != null) {  //otherwise traversing GUIDelta AST

            for (ASTGUIArgValue argValue : node.getGUIArgValueList()) {
                if ((argValue.isPresentGUIComponent()
                        && argValue.getGUIComponent().isPresentId()
                        && argValue.getGUIComponent().getId().getName().equals(componentNameToChange)) ||
                    (argValue.isPresentGUIGuard()
                        && argValue.getGUIGuard().isPresentId()
                        && argValue.getGUIGuard().getId().getName().equals(componentNameToChange)) ||
                    (argValue.isPresentGUIIterate()
                        && argValue.getGUIIterate().isPresentId()
                        && argValue.getGUIIterate().getId().getName().equals(componentNameToChange))
                ) {
                    int index = node.indexOfGUIArgValue(argValue);
                    switch (adaptionOptions) {
                        case BEFORE:
                            node.addAllGUIArgValues(index, flatMapGUIArgValue(argValueToAdd));
                            return;
                        case AFTER:
                            node.addAllGUIArgValues(index + 1, flatMapGUIArgValue(argValueToAdd));
                            return;
                        case REMOVE:
                            node.removeGUIArgValue(index);
                            return;
                        case REPLACE:
                            node.removeGUIArgValue(index);
                            node.addAllGUIArgValues(index, flatMapGUIArgValue(argValueToAdd));
                            return;
                        case NONE:
                            Log.warn("Error when selecting delta Options, this is probably the result of a grammar alteration");
                    }
                }
            }
        }

        //variable not in List or GUIDelta
        node.forEachGUIArgValues(v -> v.accept(getTraverser()));
    }

    /**
     * Works the same as handle(ASTGUIList) except that the argValues need to be wrapped in a GUIBlock context before they can be added
     * @param node the current node in the AST
     */
    public void handle(ASTGUIBlock node){

        if(componentNameToChange != null) {  //otherwise traversing GUIDelta AST

            List<ASTGUIBlockContent> blockContents = node.getGUIBlockContentList();

            //adapts and removes shouldn't be here as GUIDelta should not be adapted itself
            for (ASTGUIBlockContent blockContent : blockContents) { //Error checking first
                if (blockContent.isPresentGUIRemove() || blockContent.isPresentGUIAdapt())
                    Log.error("Attempting to adapt GUIDelta, this is not allowed");
            }

            for (ASTGUIBlockContent blockContent : blockContents) {
                if ((blockContent.isPresentGUIComponent()
                        && blockContent.getGUIComponent().isPresentId()
                        && blockContent.getGUIComponent().getId().getName().equals(componentNameToChange)) ||
                    (blockContent.isPresentGUIGuard()
                        && blockContent.getGUIGuard().isPresentId()
                        && blockContent.getGUIGuard().getId().getName().equals(componentNameToChange)) ||
                    (blockContent.isPresentGUIIterate()
                        && blockContent.getGUIIterate().isPresentId()
                        && blockContent.getGUIIterate().getId().getName().equals(componentNameToChange))
                ) {
                    int index = blockContents.indexOf(blockContent);
                    switch (adaptionOptions) {
                        case BEFORE:
                            node.addAllGUIBlockContents(index, mapToBlockContent(flatMapGUIArgValue(argValueToAdd)));
                            return;
                        case AFTER:
                            node.addAllGUIBlockContents(index + 1, mapToBlockContent(flatMapGUIArgValue(argValueToAdd)));
                            return;
                        case REMOVE:
                            node.removeGUIBlockContent(index);
                            return;
                        case REPLACE:
                            node.removeGUIBlockContent(index);
                            node.addAllGUIBlockContents(index, mapToBlockContent(flatMapGUIArgValue(argValueToAdd)));
                            return;
                        case NONE:
                            Log.warn("Error when selecting delta Options, this is probably the result of a grammar alteration");
                    }
                }

            }
        }

        //variable not in Block or GUIDelta
        node.forEachGUIBlockContents(c -> c.accept(getTraverser()));
    }

    /**
     * Wraps all argValues in the parameter to GUIBlockContext, as GUIBlockContext does not support GUILists
     * none of the argValues can be a GUIList
     * @param argValues the argValues to be wrapped, non should be a GUIList
     * @return a List of the Wrapped ArgValues
     */
    private List<ASTGUIBlockContent> mapToBlockContent(List<ASTGUIArgValue> argValues){
        return argValues.stream().map(this::buildGUIBlockContext).collect(Collectors.toList());
    }

    /**
     * Wraps the argValue in a GUIBlockContext, this only works if the parameter is not a GUIList,
     * as GUIBlockContext does not support GUILists.
     * @param argValue the argValue to wrap in a GUIBlockContext, should not be a GUIList
     * @return the GUIBlockContext containing the parameter
     */
    private ASTGUIBlockContent buildGUIBlockContext(ASTGUIArgValue argValue){
        if(argValue.isPresentGUIComponent()){
            return GUIDSLMill.gUIBlockContentBuilder()
                    .setGUIComponent(argValue.getGUIComponent())
                    .build();
        }
        if(argValue.isPresentGUIIterate()){
            return GUIDSLMill.gUIBlockContentBuilder()
                    .setGUIIterate(argValue.getGUIIterate())
                    .build();
        }
        if(argValue.isPresentExpression()){
            return GUIDSLMill.gUIBlockContentBuilder()
                    .setExpression(argValue.getExpression())
                    .build();
        }
        if(argValue.isPresentGUIGuard()){
            return GUIDSLMill.gUIBlockContentBuilder()
                    .setGUIGuard(argValue.getGUIGuard())
                    .build();
        }
        if(argValue.isPresentGUIList()){
            Log.error("cannot build guiBlockContext from GuiList");
        }
        else{
            Log.error("encountered unknown type of guiArgValue, this is likely due to grammar changes");
        }
        return null;
    }

    /**
     * maps all the argValues contained in the parameter argValue,
     * this means that argValues contained in a GUIList will be recursively flat mapped to a single list
     * @param argValue the Arg value to map
     * @return a List of all ArgValues contained in the parameter
     */
    private List<ASTGUIArgValue> flatMapGUIArgValue(ASTGUIArgValue argValue){
        LinkedList<ASTGUIArgValue> result = new LinkedList<>();
        if(argValue.isPresentGUIList()) { //Lists need to be mapped to a single list
            for(ASTGUIArgValue argValueFromList : argValue.getGUIList().getGUIArgValueList()){
                result.addAll(flatMapGUIArgValue(argValueFromList));
            }
        }
        else result.add(argValue);

        return result;
    }
}
