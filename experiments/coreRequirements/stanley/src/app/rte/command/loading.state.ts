/* (c) https://github.com/MontiCore/monticore */
//alpha, this class is not fully developed,
//only to try it out and might change (change name?)

/**
 * signifies whether a lazily loadable object has been loaded or not
 */
export enum LoadingState {
  /**
   * there has been no attempt to load the object yet
   */
  NOT_YET_LOADED,
  /**
   * the object has been loaded
   */
  LOADED,
  /**
   * if the object only exists optionally ({@link java.util.Optional})
   * and the object does not exist,
   * then empty signifies the non-existence of the object
   */
  EMPTY,
  /**
   * if one recieves this, one is not authorized
   * to access the object in question,
   * whether it exists or not
   */
  NOT_AUTHORIZED,
  /**
   * there has been an error loading the object
   * e.g. the server could not be reached
   */
  FAILED_TO_LOAD,
}
