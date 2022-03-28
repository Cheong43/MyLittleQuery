# DataBase Project 
Univeristy of Bristol student project, including a parser & interpreter implementing the SQL style query language, and IO & data stroage stuff for Data Base Server.

## Data Structure
The Data Structure has 3 layers of classes. *DataTree* is the top control node, it maps the *DataBase* with DBName. *DataBase* also a TreeMap that store different *DataTable*. *DataTable* using the LinkedHashMap() to store every piece of data.

## IO
*DBServerIO* is like a steering wheel of *DBTableIO* : *DBServerIO* manage the entire file system, and *DBTableIO* focus on particular *.tab* file execution.

## Parser
*QueryParse* is a Query style language parser, which take in a piece of string command produce AST for *QueryInterpret* to execute.

## Abstract Syntax Tree
*AST* class is the abstract syntax tree produce by *QueryParse*. It has 9 children represent different query method: *AST_Alter, AST_Create, AST_Delete, AST_Drop, AST_Insert, AST_Join, AST_Select, AST_Update, AST_Use.*

## Interpreter
*QueryInterpret* could invoke or execute to DBServer base on different *AST*, include invoke *DBServerIO* to write the file, interact with Data Structure, prompt the data to command line client.

## Exception
*QueryException* responible for every wrong user input error, then feedback to *DBClient*. But for *DBException*, if it was invoke, basically the data structure and file system is not stable and safe anymore.

## More..
*DataNullCell* is a class "represent" the NULL value inside the table.
*ToolBox* class include a useful data convert method to IO, parser and interpreter.
  
