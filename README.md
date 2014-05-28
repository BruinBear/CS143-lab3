CS143-lab3
==========
Jingyu Liu & Dennis Grijalva

Exercise 1: Parser.java
LIFE OF A QUERY IN SIMPLEDB

Step 1: simpledb.Parser.main() and simpledb.Parser.start() simpledb.Parser.main() is the entry point for the SimpleDB system. It calls simpledb.Parser.start(). The latter performs three main actions:

It populates the SimpleDB catalog from the catalog text file provided by the user as argument (Database.getCatalog().loadSchema(argv[0]);).
For each table defined in the system catalog, it computes statistics over the data in the table by calling: TableStats.computeStatistics(), which for each table does: TableStats s = new TableStats(tableid, IOCOSTPERPAGE);
It processes the statements submitted by the user (processNextStatement(new ByteArrayInputStream(statementBytes));)

Step 2: simpledb.Parser.processNextStatement() 
This method takes two key actions:
First, it gets a physical plan for the query by invoking handleQueryStatement((ZQuery)s);
Then it executes the query by calling query.execute();

Step 3: simpledb.Parser.handleTransactStatement()
This method examines different type of transaction statement and proceeds to different treatments, including commit, rollback, set transaction, unsupported exception.

Step : simpledb.Parser.handleInsertStatement()
This step gets the table associated with the related insertion and its tuple descriptor. It then validates field types and add to the tuples. If it contains a query uses parseQueryLogicalPlan()

Step : simpledb.Parser.parseQueryLogicalPlan()


Step : simpledb.Parser.handleQueryStatement()


Step : simpledb.Parser.handleDeleteStatement()
Step : simpledb.Parser.handleQueryStatement()
Step : no query file, run interactive prompt and processNextStatement
