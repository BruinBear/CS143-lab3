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

Step4 : simpledb.Parser.handleInsertStatement()
This step gets the table associated with the related insertion and its tuple descriptor. It then validates field types and add to the tuples. If it contains a query uses parseQueryLogicalPlan()

Step5 : simpledb.Parser.parseQueryLogicalPlan()
This ste first goes through all the tables to make sure they exesits and subsequently parses the WHERE clause. When needed this method also creates filter and join nodes. Next, this method looks for
GROUP BY statements. The last two steps are to sort that associated data and check the SELECT fields and Aggregate clauses checking for validity. 

Step6 : simpledb.Parser.handleQueryStatement()
This step first creates a logical plan as described in the previous step (step5) and from this logical plan, it creates a physical plan. Next, this method will print out he query plan with a 
visualizer in a tree format.

Step7 : simpledb.Parser.handleDeleteStatement()
This first thing this method does is to make sure the table exists, if not an exception is thrown. Next, it must get the table and create a logical plan. Next this method creates a new delete
transaction. Finally the query is returned. 
