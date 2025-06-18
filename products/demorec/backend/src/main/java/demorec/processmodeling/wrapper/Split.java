package demorec.processmodeling.wrapper;

import java.util.ArrayList;

public class Split {
  String id;
  ArrayList<Split> successors;
  ArrayList<Split> predecessors;

  public Split(String id) {
    this.id = id;
    this.successors = new ArrayList<>();
    this.predecessors = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void addSuccessor(Split split) {
    successors.add(split);
  }

  public void addPredecessor(Split split) {
    predecessors.add(split);
  }

  public ArrayList<Split> getSuccessors() {
    return successors;
  }
}
