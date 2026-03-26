package ocl2cypher.utils;

import jsweet.lang.Erased;

@Erased
public class CypherQueryBlock {

    private final StringBuilder matchClause = new StringBuilder();
    private final StringBuilder withClause = new StringBuilder();
    private final StringBuilder whereClause = new StringBuilder();
    private final StringBuilder returnClause = new StringBuilder();
    private boolean writeIntoWith = false;

    /**
     * Retrieves the internal representation of the WHERE clause.
     *
     * This method returns the current state of the internal StringBuilder
     * that represents the WHERE clause of the Cypher query block. The
     * returned StringBuilder contains the accumulated content of the WHERE clause.
     *
     * @return the StringBuilder containing the WHERE clause content
     */
    public StringBuilder getWhereClause() {
        return whereClause;
    }

    /**
     * Initializes the WITH clause of a Cypher query.
     *
     * This method appends the "WITH " keyword to the internal representation
     * of the WITH clause and sets a flag to indicate that the WITH clause
     * is currently being written to. It is used to start structuring the WITH
     * clause within a Cypher query block.
     */
    public void beginWithLine() {
        withClause.append("WITH ");
        writeIntoWith = true;
    }

    /**
     * Ends the current WITH clause by adding a newline character.
     *
     * This method finalizes the WITH clause of the Cypher query by appending a newline
     * character to its content and setting an internal flag to indicate that the WITH
     * clause is no longer being actively written to. This is useful for structuring
     * the query in preparation for appending additional clauses.
     */
    public void endWithLine() {
        writeIntoWith = false;
        withClause.append("\n");
    }

    /**
     * Appends the provided string to the internal representation of the MATCH clause.
     *
     * This method modifies the Cypher query block by adding the given string to the
     * MATCH clause. The provided string is directly appended to the existing content
     * of the MATCH clause.
     *
     * @param matchPart the string to append to the MATCH clause
     */
    public void appendMatch(String matchPart) {
        matchClause.append(matchPart);
    }

    /**
     * Appends the given string expression to the appropriate internal clause.
     *
     * This method modifies the query block by appending the provided expression
     * to either the WITH clause or the WHERE clause, depending on the state
     * of the query block. If the query block is currently handling the WITH clause,
     * the expression is appended to the WITH clause; otherwise, it is appended to
     * the WHERE clause.
     *
     * @param expr the string expression to append to the WITH clause or WHERE clause
     */
    public void appendToWhere(String expr) {

        if (writeIntoWith){
            withClause.append(expr);
        }else {
            whereClause.append(expr);
        }

    }

    /**
     * Appends the specified string to the internal representation of the RETURN clause.
     *
     * This method modifies the query block by adding the given string to the
     * RETURN clause. The provided string is directly appended to the existing content
     * of the RETURN clause.
     *
     * @param returnPart the string to append to the RETURN clause
     */
    public void appendReturn(String returnPart) {
        returnClause.append(returnPart);
    }

    /**
     * Builds and returns a string representation of the current query content.
     *
     * This method concatenates the contents of the internal string builders
     * corresponding to the MATCH, WITH, WHERE, and RETURN clauses, in that order,
     * constructing a complete Cypher query string. Each clause is included only if
     * it has content. The MATCH and WHERE clauses include appropriate keywords
     * ("MATCH" and "WHERE") before their content, while the RETURN clause includes
     * the "RETURN" keyword. The WITH clause is included as-is.
     *
     * @return the constructed Cypher query string based on the current state of the query block
     */
    public String build() {
        StringBuilder result = new StringBuilder();
        if (matchClause.length() > 0) {
            result.append("MATCH ").append(matchClause).append("\n");
        }
        if (withClause.length() > 5) {
            result.append(withClause);
        }
        if (whereClause.length() > 0) {
            result.append("WHERE ").append(whereClause).append("\n");
        }
        if (returnClause.length() > 0) {
            result.append("RETURN ").append(returnClause);
        }
        return result.toString();
    }

    /**
     * Clears all the content stored in the query block, resetting it to its initial state.
     *
     * This method resets the internal string builders for the MATCH, WITH, WHERE,
     * and RETURN clauses to have a length of 0, effectively erasing any query content
     * accumulated so far. It also resets the flag used for handling the WITH clause.
     * Use this method to prepare the query block for constructing a new query.
     */
    public void clear() {
        matchClause.setLength(0);
        withClause.setLength(0);
        writeIntoWith = false;
        whereClause.setLength(0);
        returnClause.setLength(0);
    }
}
