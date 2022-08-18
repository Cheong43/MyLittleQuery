# My Little Query🗄️
A database server project written on native Java. It contain a recursive descent parser to parse the SQL-like language, detect datatype with regex and store in build-in datatype system, generating abstract syntax tree data structure to fully functional and recursive interpreter, combining MVC style data container, and R/W local data with native java IO.

- Recursive descent Parser
- Datatype detection and build-in Datatype management(using java native datatype)
- Recursive AST supportted Interpreter
- MVC style data container
- Binary File Read/Write in custom .tab file ( which means a table📊!)

It support the following command:

* USE: changes the database against which the following queries will be run
* CREATE: constructs a new database or table (depending on the provided parameters)
* INSERT: adds a new record (row) to an existing table
* SELECT: searches for records that match the given condition
* UPDATE: changes the existing data contained within a table
* ALTER: changes the structure (columns) of an existing table
* DELETE: removes records that match the given condition from an existing table
* DROP: removes a specified table from a database, or removes the entire database
* JOIN: performs an inner join on two tables (returning all permutations of all matching records)
*  ......

Full BNF grammar here: [ BNF](https://github.com/Cheong43/MyLittleQuery/blob/main/BNF.txt)


The main body is the server side of project, but it also include a simple local client running on command line with JVM.

## More...

### Data Structure
The Data Structure has 3 layers of classes. *DataTree* is the top control node, it maps the *DataBase* with DBName. *DataBase* also a TreeMap that store different *DataTable*. *DataTable* using the LinkedHashMap() to store every piece of data -- to make every piece of data remain order while quick indexing!.

### IO
*DBServerIO* is like a steering wheel of *DBTableIO* : *DBServerIO* manage the entire file system, and *DBTableIO* focus on particular *.tab* file execution.

### Parser
*QueryParse* is a Query style language parser, which take in a piece of string command produce AST for *QueryInterpret* to execute.

### Abstract Syntax Tree
*AST* class is the abstract syntax tree produce by *QueryParse*. It has 9 children represent different query method: *AST_Alter, AST_Create, AST_Delete, AST_Drop, AST_Insert, AST_Join, AST_Select, AST_Update, AST_Use.*

### Interpreter
*QueryInterpret* could invoke or execute to DBServer base on different *AST*, include invoke *DBServerIO* to write the file, interact with Data Structure, prompt the data to command line client.

### Exception
*QueryException* responible for every wrong user input error, then feedback to *DBClient*. But for *DBException*, if it was invoke, basically the data structure and file system is not stable and safe anymore.

### things maybe should know
*DataNullCell* is a class "represent" the NULL value inside the table.
*ToolBox* class include a useful data convert method to IO, parser and interpreter.
  
