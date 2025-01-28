package umlp._cocos;

import de.monticore.ast.ASTCNode;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.IllegalFormatException;

/**
 * Can be implemented by CoCos which can be tested in a CoCoTest. The getters introduced by this
 * should not be used for anything else, since sometimes they may not fully cover all aspects of a
 * CoCo
 */
public interface CoCoTestable {

  /**
   * Gets the error code this CoCo uses. If the CoCo uses multiple error codes, it is up to the
   * writer of the respektive unit test which code is returned by this method.
   *
   * @return The primary error code of this
   */
  String getErrorCode();

  /**
   * Gets the error message of this CoCo in a Java format String. If the CoCo uses multiple error
   * messages it is up to the writer of the respektive unit test, which message is returned by this
   * method.
   * <p>
   * Currently, there is no information available about the number and kind of formatting arguments
   * required apart from the format String itself.
   *
   * @return The primary error message format of this
   */
  String getErrorMessageFormat();

  /**
   * Logs an error at the {@link SourcePosition} of the given {@link ASTCNode}. This method is a
   * convenience method for {@link #logError(SourcePosition, Object...)} so see that for details.
   *
   * @param node The {@link ASTCNode} where the error occurs
   * @param args The formatting arguments for the error message format
   * @throws IllegalFormatException When too few or wrong formatting arguments were provided
   * @see #logError(SourcePosition, Object...)
   */
  default void logError(ASTCNode node, Object... args) throws IllegalFormatException {
    logError(node.get_SourcePositionStart(), args);
  }

  /**
   * Logs an error at the given {@link SourcePosition} with the error code returned by
   * {@link #getErrorCode()} and the message from {@link #getErrorMessageFormat()} formatted with the
   * provided formatting arguments.
   *
   * @param sourcePosition Where the error occurred
   * @param args           The formatting arguments for the message returned by {@link #getErrorMessageFormat()}
   * @throws IllegalFormatException When too few or wrong formatting arguments were provided
   * @see #logError(SourcePosition, String, String, Object...)
   */
  default void logError(SourcePosition sourcePosition, Object... args) throws IllegalFormatException {
    logError(sourcePosition, getErrorCode(), getErrorMessageFormat(), args);
  }

  /**
   * Logs an error (with {@link Log#error(String, SourcePosition)}) at the given
   * {@link SourcePosition} with the given error code and formatted message.
   *
   * @param sourcePosition     Where the error occurred
   * @param errorCode          The error code
   * @param errorMessageFormat The error message format
   * @param args               The formatting arguments for the error message format
   * @throws IllegalFormatException When too few or wrong formatting arguments were provided
   */
  default void logError(SourcePosition sourcePosition,
                        String errorCode,
                        String errorMessageFormat,
                        Object... args) throws IllegalFormatException {
    Log.error(errorCode + " " + String.format(errorMessageFormat, args), sourcePosition);
  }
}
