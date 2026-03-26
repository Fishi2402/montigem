package ocl2java.utils;

import jsweet.lang.Erased;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Erased
public class CypherQueryBuilder {

    private final List<CypherQueryBlock> blocks = new ArrayList<>();
    private CypherQueryBlock currentBlock = new CypherQueryBlock();

    /**
     * Starts a new Cypher query block within the query builder. If the current query block is
     * non-empty, it is added to the list of completed query blocks (`blocks`). Subsequently,
     * a new instance of `CypherQueryBlock` is created and assigned as the current block for
     * further query construction.
     *
     * This method is useful for logically separating different parts of a Cypher query into
     * distinct blocks, which can later be combined as needed. It ensures the current block
     * is persisted only if it contains meaningful content.
     */
    public void startNewBlock() {
        if (currentBlock != null && !isEmpty(currentBlock)) {
            blocks.add(currentBlock);
        }
        currentBlock = new CypherQueryBlock();
    }

    /**
     * Retrieves the current Cypher query block that is being constructed.
     * The current block represents the active segment where query components
     * such as MATCH, WHERE, and RETURN clauses are being appended.
     *
     * @return the current {@code CypherQueryBlock} under construction.
     */
    public CypherQueryBlock getCurrentBlock() {
        return this.currentBlock;
    }

    /**
     * Appends a segment to the MATCH clause of the current Cypher query block.
     *
     * @param matchPart the string representation of the MATCH clause or part of it
     *                  to be appended to the current Cypher query block.
     */
    public void appendMatch(String matchPart) {
        currentBlock.appendMatch(matchPart);
    }

    /**
     * Initiates the `WITH` clause in the current Cypher query block.
     *
     * This method sets up the `WITH` clause by appending the keyword "WITH"
     * to the `withClause` of the `CypherQueryBlock` and enabling subsequent
     * expressions to be written into the `WITH` clause until it is terminated.
     *
     * It is typically used to start creating query blocks that process or pass
     * variables between parts of a Cypher query.
     */
    public void beginWithLine(){currentBlock.beginWithLine();}

    /**
     * Marks the end of the `WITH` clause in the current Cypher query block.
     *
     * This method stops appending to the `WITH` clause by setting the internal
     * state flag for writing into the `WITH` clause to `false`. It also appends
     * a newline character to the `withClause` to signify the termination of the
     * `WITH` segment in the query block.
     *
     * This ensures that subsequent query elements are correctly separated when
     * constructing a Cypher query.
     */
    public void endWithLine(){currentBlock.endWithLine();}

    /**
     * Appends the given expression to the WHERE clause of the current Cypher query block.
     * If the current block is set to write into the WITH clause, the expression will be
     * appended to the WITH clause instead of the WHERE clause.
     *
     * @param expr the expression to be added to the WHERE clause of the current query block
     */
    public void appendToWhere(String expr) {
        currentBlock.appendToWhere(expr);
    }

    /**
     * Appends the specified content to the RETURN clause of the current query block.
     * The RETURN clause defines what data should be returned from the query.
     *
     * @param returnPart the content to be added to the RETURN clause
     */
    public void appendReturn(String returnPart) {
        currentBlock.appendReturn(returnPart);
    }

    /**
     * Constructs a Cypher query string by combining all completed query blocks
     * in the query builder. If the current block is non-empty, it is added
     * to the list of completed blocks before building the final query.
     *
     * Each completed block is processed through its `build` method and
     * concatenated using the "UNION" keyword, separated by newline characters.
     * This allows for the creation of complex Cypher queries that combine
     * multiple logical parts.
     *
     * @return the string representation of the constructed Cypher query,
     *         combining all blocks separated by "UNION".
     */
    public String build() {
        if (!isEmpty(currentBlock)) {
            blocks.add(currentBlock);
        }

        return String.join("\nUNION\n",
                blocks.stream().map(CypherQueryBlock::build).collect(Collectors.toList()));
    }

    /**
     * Clears the current state of the query builder by removing all existing query blocks
     * and resetting the current block to a new, empty instance.
     *
     * This method is useful for resetting the builder to its initial state, allowing the
     * construction of a new Cypher query from scratch without retaining any previously
     * added data.
     */
    public void clear() {
        blocks.clear();
        currentBlock = new CypherQueryBlock();
    }

    /**
     * Checks if the provided {@code CypherQueryBlock} is empty.
     * A block is considered empty if its built query string is empty
     * or consists solely of whitespace.
     *
     * @param block the {@code CypherQueryBlock} to be checked for emptiness
     * @return {@code true} if the block is empty, otherwise {@code false}
     */
    private boolean isEmpty(CypherQueryBlock block) {
        return block.build().trim().isEmpty();
    }

    /**
     * Constructs and returns a string representation of the complete Cypher query built
     * by the query builder. This includes all the query blocks that have been added and
     * combined, separated by "UNION" if applicable.
     *
     * If the current query block is non-empty, it is added to the list of completed blocks
     * before constructing the final query string.
     *
     * @return the string representation of the full Cypher query.
     */
    @Override
    public String toString() {
        return build();
    }
}
