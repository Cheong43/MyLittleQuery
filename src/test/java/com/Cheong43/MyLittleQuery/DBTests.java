package com.Cheong43.MyLittleQuery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class DBTests {

  private DBServer server;

  // we make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup(@TempDir File dbDir) {
    // Notice the @TempDir annotation, this instructs JUnit to create a new temp directory somewhere
    // and proceeds to *delete* that directory when the test finishes.
    // You can read the specifics of this at
    // https://junit.org/junit5/docs/5.4.2/api/org/junit/jupiter/api/io/TempDir.html

    // If you want to inspect the content of the directory during/after a test run for debugging,
    // simply replace `dbDir` here with your own File instance that points to somewhere you know.
    // IMPORTANT: If you do this, make sure you rerun the tests using `dbDir` again to make sure it
    // still works and keep it that way for the submission.

    server = new DBServer(dbDir);
  }

  // Here's a basic test for spawning a new server and sending an invalid command,
  // the spec dictates that the server respond with something that starts with `[ERROR]`
  @Test
  void testInvalidCommandIsAnError() {
    assertTrue(server.handleCommand("foo").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("INSERT;").startsWith("[ERROR]"));
  }

  // Add more unit tests or integration tests here.
  // Unit tests would test individual methods or classes whereas integration tests are geared
  // towards a specific usecase (i.e. creating a table and inserting rows and asserting whether the
  // rows are actually inserted)
  @Test
  void testBasicModifyAndWriteFile() {
    assertTrue(server.handleCommand("CrEaTe dAtABasE testDB;").startsWith("[OK]"));
    assertTrue(server.handleCommand("uSE testDB;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CreaTE tabLE actors;").startsWith("[OK]"));
    assertTrue(server.handleCommand("AlTER tablE actors AdD name;").startsWith("[OK]"));
    assertTrue(server.handleCommand("AlTER tablE actors AdD age;").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSert iNtO actors ValuES('alex', 18);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSert iNtO actors ValuES('mike', 26);").startsWith("[OK]"));
    assertFalse(server.handleCommand("INSert iNtO actors ValuES('Amy', 1, true);").startsWith("[OK]"));// Dummy Input
    assertTrue(server.handleCommand("AlTER tablE actors AdD married;").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSert iNtO actors ValuES('Amy', 1, true);").startsWith("[OK]"));
    assertTrue(server.handleCommand("UpDate actors Set married = false where id < 3;").startsWith("[OK]"));
    /*
     * After all these command applied, the actors table is like:
     * SQL:> SELECT * FROM actors;
      [OK]
      id name age married
      1 alex 18 false
      2 mike 26 false
      3 Amy 1 true
     */
    assertTrue(server.handleCommand("INSert iNtO actors ValuES('hacker', 99999, NULL);").startsWith("[OK]"));
    assertTrue(server.handleCommand("Delete From actors WHere (name == 'Amy') oR (age < 19);").startsWith("[OK]"));
    /*
     * After all these command applied, the actors table is like:
     * SQL:> SELECT * FROM actors;
      [OK]
      id name age married
      2 mike 26 false
      4 hacker 99999 NULL
      *
     */
    assertTrue(server.handleCommand("Delete From actors WHere name == 'hacker';").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSert iNtO actors ValuES('Moore', 59, false);").startsWith("[OK]"));
    /*
     * After all these command applied, the actors table is like:
     * SQL:> SELECT * FROM actors;
      [OK]
      id name age married
      2 mike 26 false
      5 Moore 59 false
      *
     */
    assertTrue(server.handleCommand("AlTER tablE actors dRop name;").startsWith("[OK]"));
    assertTrue(server.handleCommand("AlTER tablE actors DrOP age;").startsWith("[OK]"));
    /*
     * After all these command applied, the actors table is like:
     * SQL:> SELECT * FROM actors;
      [OK]
      id married
      2 false
      5 false
      *
     */
    assertTrue(server.handleCommand("drop tablE actors;").startsWith("[OK]"));
    assertTrue(server.handleCommand("drop daTabase testDB;").startsWith("[OK]"));
    // check if the database and table drop successfully
    assertTrue(server.handleCommand("Use testDB;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("INSert iNtO actors ValuES('alex', 18);").startsWith("[ERROR]"));
  }

  @Test
  void testDataStructureAndReadFile() throws DBException {
    // Init the root Directory inside project folder, this may fail due to the environment differ
    File rootDirectory = new File("src"+File.separator+"test"+File.separator+"testDataTree");
    DataTree TreeNode = new DataTree(rootDirectory);
    DBServerIO serverIO = new DBServerIO(rootDirectory);
    serverIO.readAllDataBase(TreeNode);

    // Check if everything read successfully
    assertTrue(TreeNode.isDataBaseExist("testDB"));
    assertTrue(TreeNode.getDataBase("testDB").isTableExist("people"));
    assertTrue(TreeNode.getDataBase("testDB").isTableExist("sheds"));

    // Check is the data store correctly
    DataTable people = TreeNode.getDataBase("testDB").getTableByName("people");
    DataTable sheds = TreeNode.getDataBase("testDB").getTableByName("sheds");

    // Number of Row and column (Not include header)
    assertTrue(people.getCol() == 4);
    assertTrue(people.getRow() == 3);
    assertTrue(sheds.getCol() == 4);
    assertTrue(sheds.getRow() == 3);

    // Check if attribute correct
    assertTrue(people.isAttributeExist("Name"));
    assertTrue(people.isAttributeExist("Email"));
    assertTrue(sheds.isAttributeExist("id"));
    assertFalse(sheds.isAttributeExist("Email"));

    // Check if id exist
    assertTrue(people.isIdExist(1));
    assertFalse(people.isIdExist(5));
    assertTrue(sheds.isIdExist(1));
    assertFalse(sheds.isIdExist(5));

    // Check if data store correctly in the memory
    // p.s. I craete a DataNullCell class to represent NULL value
    assertTrue(people.getDataCell(1,"Age").equals(-21.1)); //float
    assertTrue(people.getDataCell(3,"Email").equals("chris@chris.ac.uk"));//String
    assertTrue((Boolean) sheds.getDataCell(1,"PurchaserID")); // boolean
    assertTrue(sheds.getDataCell(2,"Height") instanceof DataNullCell); //NULL value
  }

  @Test
  void testParser() throws QueryException {
    // USE
    QueryParse parser1 = new QueryParse("USE markbook;");
    AST_Use ast_use = (AST_Use) parser1.parse();
    assertTrue(ast_use.dataBase.equals("markbook"));

    // CREATE
    QueryParse parser2 = new QueryParse("CREATE DATABASE markbook;");
    AST_Create ast_createTable = (AST_Create)parser2.parse();
    assertTrue(ast_createTable.opField.equals("markbook"));

    // DROP
    QueryParse parser3 = new QueryParse("DROP taBle abc;");
    AST_Drop ast_drop = (AST_Drop) parser3.parse();
    assertTrue(ast_drop.opField.equals("abc"));

    // Alter
    QueryParse parser4 = new QueryParse("AlTER tablE aha dRop foo;");
    AST_Alter ast_alter = (AST_Alter) parser4.parse();
    assertTrue(ast_alter.table.equals("aha"));
    assertTrue(ast_alter.attribute.equals("foo"));

    // INSERT
    QueryParse parser5 = new QueryParse("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
    AST_Insert ast_insert = (AST_Insert) parser5.parse();
    assertTrue(ast_insert.table.equals("marks"));
    assertTrue(ast_insert.valueList.get(1).equals(65));

    // SELECT
    QueryParse parser6 = new QueryParse("SELECT * FROM marks WHERE name != 'Dave';");
    AST_Select ast_select = (AST_Select) parser6.parse();
    assertTrue(ast_select.isAllAttribute);
    assertTrue(ast_select.table.equals("marks"));
    assertTrue(ast_select.conditionNode != null);
    assertTrue(ast_select.conditionNode.left == null);
    assertTrue(ast_select.conditionNode.right == null);
    assertTrue(ast_select.conditionNode.attribute.equals("name"));
    assertTrue(ast_select.conditionNode.value.equals("Dave"));

    // UPDATE
    QueryParse parser7 = new QueryParse("UpDate actors Set married = false where id < 3;");
    AST_Update ast_update = (AST_Update) parser7.parse();
    assertTrue(ast_update.nameList.get(0).equals("married"));
    assertFalse((Boolean) ast_update.valueList.get(0));
    assertTrue(ast_update.table.equals("actors"));
    assertTrue(ast_update.conditionNode.attribute.equals("id"));

    // DELETE
    QueryParse parser8 = new QueryParse("Delete From hoo WHere name == 'foo';");
    AST_Delete ast_delete = (AST_Delete) parser8.parse();
    assertTrue(ast_delete.table.equals("hoo"));
    assertTrue(ast_delete.conditionNode.attribute.equals("name"));
    assertTrue(ast_delete.conditionNode.value.equals("foo"));

    // JOIN
    QueryParse parser9 = new QueryParse("JOIN coursework AND marks ON grade AND id;");
    AST_Join ast_join = (AST_Join) parser9.parse();
    assertTrue(ast_join.leftAttribute.equals("grade"));
    assertTrue(ast_join.rightAttribute.equals("id"));
    assertTrue(ast_join.leftTable.equals("coursework"));
    assertTrue(ast_join.rightTable.equals("marks"));
  }

  // The Query method like SELECT and JOIN is manually tested via DBClient.

}
