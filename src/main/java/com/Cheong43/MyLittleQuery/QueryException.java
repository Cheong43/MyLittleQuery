package com.Cheong43.MyLittleQuery;


public class QueryException extends Exception {

    private final String[] errorRef;

    {
        errorRef = new String[]{
                "",
                "Syntax Error 1: Use incorrect command keyword (USE/CREATE/...) ",
                "Syntax Error 2: Incorrect Create keyword. Use \"CREATE DATABASE \" or \"CREATE TABLE \"",
                "Syntax Error 3: \"CREATE TABLE \" <TableName> | \"CREATE TABLE \" <TableName> \"(\" <AttributeList> \")\"",
                "Syntax Error 4: Incorrect Create keyword. Use \"DROP DATABASE \" or \"DROP TABLE \"",
                "Syntax Error 5: Incorrect Create keyword. Use ADD or DROP after Alter TableName",
                "Syntax Error 6: \"INSERT INTO \" <TableName> \" VALUES(\" <ValueList> \")\"",
                "Syntax Error 7: Incorrect value format. Do you quote the string by <'> ?",
                "Syntax Error 8: \"SELECT \" <WildAttribList> \" FROM \" <TableName> \" WHERE \" <Condition> ",
                "Syntax Error 9: \"UPDATE \" <TableName> \" SET \" <NameValueList> \" WHERE \" <Condition> ",
                "Syntax Error 10: \"ALTER TABLE \" <TableName> \" \" <AlterationType> \" \" <AttributeName>",
                "Syntax Error 11: \"DELETE FROM \" <TableName> \" WHERE \" <Condition>",
                "Syntax Error 12: \"JOIN \" <TableName> \" AND \" <TableName> \" ON \" <AttributeName> \" AND \" <AttributeName>",
                "Syntax Error 13: The bracket does not close correctly.",
                "Syntax Error 14: The relation between conditions can only be AND / OR.",
                "Syntax Error 15: Your command do not end correctly. Have you added <;> ?",
                "Syntax Error 16: Please enter a command. ",
                "Interpret Error 17: Database not exist.",
                "Interpret Error 18: *Unknown Error* createMode not found",
                "Interpret Error 19: One or more attribute name in the command already exist.",
                "Interpret Error 20: Database already existed.",
                "Interpret Error 21: Database going to delete not exist.",
                "Interpret Error 22: Insert too many values.",
                "Interpret Error 23: One or more attribute name in the command not exist.",
                "Interpret Error 24: Cannot use LIKE with non-String Value.",
                "Interpret Error 25:  < / > / =< / => Operator can only execute on Integer OR Float Value.",
                "Interpret Error 26:  Cannot compare id with non-Integer type value.",
                "Interpret Error 27:  LIKE can only perform with String.",
                "Interpret Error 28:  Can not update attributes. Check if the NameValuePair contain <id> or other invalid name?",
                "Interpret Error 29:  The table going to join do not exist.",
                "Interpret Error 30:  Current working Database not set. Try the <USE> <DataBase> method? ",
                "Interpret Error 31:  Table not exist. Try the <CREATE> <Table> method? ",
                "Interpret Error 32:  Table existed. Create table not success. ",
        };
    }

    private int errorCode;

    public QueryException() {
    }

    public static class QuerySyntaxException extends QueryException {
        public QuerySyntaxException(int errorCode) {
            setErrorCode(errorCode);
        }
    }

    public static class EmptyCommandException extends QueryException {
        public EmptyCommandException(int errorCode) {
            setErrorCode(errorCode);
        }
    }

    public static class InterpretFailException extends QueryException {
        public InterpretFailException(int errorCode) {
            setErrorCode(errorCode);
        }
    }

    void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    String getErrorLog() {
        return this.errorRef[errorCode];
    }
}
