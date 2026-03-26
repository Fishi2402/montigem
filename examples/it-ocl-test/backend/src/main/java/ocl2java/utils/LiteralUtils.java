package ocl2java.utils;

import de.monticore.literals.mccommonliterals._ast.*;
import jsweet.lang.Erased;

import java.util.Optional;

@Erased
public class LiteralUtils {

    private LiteralUtils() {
    }

    /**
     * Extracts the source string representation of a given AST node representing a literal.
     * Depending on the type of the provided literal node, this method returns the appropriate
     * string representation wrapped in an Optional. If the literal type is unsupported or null,
     * an empty Optional is returned.
     *
     * @param literal the AST node representing a literal. This can be of types such as boolean,
     *                character, string, numeric, or null literals. Must not be null.
     * @return an Optional containing the source string representation of the literal, or an
     *         empty Optional if the literal type is unsupported or null.
     */
    public static Optional<String> extractLiteralSource(ASTMCCommonLiteralsNode literal) {
        if (literal instanceof ASTBooleanLiteral) {
            return Optional.of(String.valueOf(((ASTBooleanLiteral) literal).getValue()));
        }

        if (literal instanceof ASTCharLiteral) {
            return Optional.of(((ASTCharLiteral) literal).getSource());
        }

        if (literal instanceof ASTStringLiteral) {
            return Optional.of("\"" + ((ASTStringLiteral) literal).getSource() + "\"");
        }

        if (literal instanceof ASTNullLiteral) {
            return Optional.of("null");
        }

        if (literal instanceof ASTSignedBasicDoubleLiteral) {
            return Optional.of(((ASTSignedBasicDoubleLiteral) literal).getSource());
        }

        if (literal instanceof ASTSignedBasicFloatLiteral) {
            return Optional.of(((ASTSignedBasicFloatLiteral) literal).getSource());
        }

        if (literal instanceof ASTSignedBasicLongLiteral) {
            return Optional.of(((ASTSignedBasicLongLiteral) literal).getSource());
        }

        if (literal instanceof ASTSignedNatLiteral) {
            return Optional.of(((ASTSignedNatLiteral) literal).getSource());
        }

        if (literal instanceof ASTBasicDoubleLiteral) {
            return Optional.of(((ASTBasicDoubleLiteral) literal).getSource());
        }

        if (literal instanceof ASTBasicFloatLiteral) {
            return Optional.of(((ASTBasicFloatLiteral) literal).getSource());
        }

        if (literal instanceof ASTBasicLongLiteral) {
            return Optional.of(((ASTBasicLongLiteral) literal).getSource());
        }

        if (literal instanceof ASTNatLiteral) {
            return Optional.of(((ASTNatLiteral) literal).getSource());
        }

        return Optional.empty();
    }
}
