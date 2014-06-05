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
This step first goes through all the tables to make sure they exist and subsequently parses the WHERE clause. When needed this method also creates filter and join nodes. Next, this method looks for
GROUP BY statements. The last two steps are to sort that associated data and check the SELECT fields and Aggregate clauses checking for validity. 

Step6 : simpledb.Parser.handleQueryStatement()
This step first creates a logical plan as described in the previous step (step5) and from this logical plan, it creates a physical plan. Next, this method will print out he query plan with a 
visualizer in a tree format.

Step7 : simpledb.Parser.handleDeleteStatement()
This first thing this method does is to make sure the table exists, if not an exception is thrown. Next, it must get the table and create a logical plan. Next this method creates a new delete
transaction. Finally the query is returned. 

Exercise 6:
select d.fname, d.lname
from Actor a, Casts c, Movie_Director m, Director d
where a.id=c.pid and c.mid=m.mid and m.did=d.id
and a.fname='John' and a.lname='Spicer';

0.01 DB

The query plan is:
                            π(d.fname,d.lname),card:1
                            |
                            ⨝(a.id=c.pid),card:1
  __________________________|___________________________
  |                                                    |
  σ(a.lname=Spicer),card:1                             ⨝(m.mid=c.mid),card:29729
  |                                    ________________|_________________
  σ(a.fname=John),card:1               |                                |
  |                                    ⨝(d.id=m.did),card:2791          |
  |                           _________|_________                       |
  |                           |                 |                     scan(Casts c)
scan(Actor a)               scan(Director d)  scan(Movie_Director m)

Explanation: Because all the joins are '=' this should be the only way to plan this query.


select d.fname, d.lname
from Actor a, Casts c, Movie_Director m, Director d
where a.id=c.pid and c.mid=m.mid and m.did=d.id
and a.fname!='John';

The query plan is:
                                           π(d.fname,d.lname),card:26026
                                           |
                                           ⨝(c.pid=a.id),card:26026
                           ________________|________________
                           |                               |
                           ⨝(m.mid=c.mid),card:29729       σ(a.fname<>John),card:26026
           ________________|_________________              |
           |                                |              |
           ⨝(d.id=m.did),card:2791          |              |
  _________|_________                       |              |
  |                 |                     scan(Casts c)    |
scan(Director d)  scan(Movie_Director m)                 scan(Actor a)

Explanation: 
