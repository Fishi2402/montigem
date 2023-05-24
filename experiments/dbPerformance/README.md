# Experiment: dbPerformance

An experiment to check performance characteristics of jdbc and the db

1. Sending multiple queries or sending one big query with multiple result sets
   - relevant for deep inheritance trees
2. Encoding the concrete class/table in the id


Results (All with 1000 entries per table):

- H2 in memory with 100 Tables:
  - testBulkPreparedStatement() 16s
  - testBulk() 1min 18s
  - testEncodedInId() 3s
  - testMultiple() 42s

- Local postgres with 100 Tables
  - encodedInId ~ 90s
  - multiple ~ 900s
  - bulk ~ 550s
  - bulkPrepared ~ 199s

- H2 in memory with 1000 tables
  - testEncodedInId() 14,5s
