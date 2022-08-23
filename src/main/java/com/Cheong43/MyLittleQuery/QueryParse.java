package com.Cheong43.MyLittleQuery;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryParse {

    private static final Pattern STRING_PATTERN = Pattern.compile("\'(.*?)\'");

    private final StringTokenizer commandTokens;
    private final ArrayList<String> stringTokens;
    private int stringTokensCounter;

    public QueryParse(String command) {
        this.stringTokens = new ArrayList<>();
        this.commandTokens = tokenizer(command);
        this.stringTokensCounter = 0;
    }

    public StringTokenizer tokenizer(String command) {
        Matcher matcher = STRING_PATTERN.matcher(command);
        while (matcher.find()) {
            this.stringTokens.add(matcher.group(1));
        }
        String tempCommand = command.replaceAll("\'.*?\'", " #STR1Ng# ").replaceAll("[(|)|,|;]", " $0 ");
        String cmd = tempCommand.replaceAll("\\=\\=|\\>\\=?|\\<\\=?|\\!\\=", " $0 ");
        return new StringTokenizer(cmd, " ");
    }

    public AST parse() throws QueryException {
        try {
            return switch (this.commandTokens.nextToken().toUpperCase()) {
                case "USE" -> parseUSE();
                case "CREATE" -> parseCREATE();
                case "DROP" -> parseDrop();
                case "ALTER" -> parseAlter();
                case "INSERT" -> parseInsert();
                case "SELECT" -> parseSelect();
                case "UPDATE" -> parseUpdate();
                case "DELETE" -> parseDelete();
                case "JOIN" -> parseJoin();
                default -> throw new QueryException.QuerySyntaxException(1);
            };
        }catch (NoSuchElementException e){
            throw new QueryException.QuerySyntaxException(15);
        }
    }

    private AST_Use parseUSE() throws QueryException {
        AST_Use cmd = new AST_Use(commandTokens.nextToken());
        checkEndMark();
        return cmd;
    }

    private AST_Create parseCREATE() throws QueryException {
        return switch (commandTokens.nextToken().toUpperCase()) {
            case "DATABASE" -> parseCreateDatabase();
            case "TABLE" -> parseCreateTable();
            default -> throw new QueryException.QuerySyntaxException(2);
        };

    }

    private AST_Create parseCreateDatabase() throws QueryException {
        AST_Create cmd = new AST_Create("DATABASE");
        cmd.setOpField(commandTokens.nextToken());
        checkEndMark();
        return cmd;
    }

    private AST_Create parseCreateTable() throws QueryException {
        AST_Create cmd = new AST_Create("TABLE");
        cmd.setOpField(commandTokens.nextToken());
        switch (commandTokens.nextToken()) {
            case ";":
                break;
            case "(":
                cmd.fillAttribute(scanAttributeListWithBracket());
                checkEndMark();
                break;
            default:
                throw new QueryException.QuerySyntaxException(3);
        }
        return cmd;
    }

    private ArrayList<String> scanAttributeListWithBracket() {
        String AttributeName = commandTokens.nextToken();
        ArrayList<String> AttributeList = new ArrayList<>();
        while (!AttributeName.equals(")")) {
            if (!AttributeName.equals(",")) {
                AttributeList.add(AttributeName);
            }
            AttributeName = commandTokens.nextToken();
        }
        return AttributeList;
    }

    private AST_Drop parseDrop() throws QueryException {
        AST_Drop cmd = switch (commandTokens.nextToken().toUpperCase()) {
            case "TABLE" -> new AST_Drop("TABLE");
            case "DATABASE" -> new AST_Drop("DATABASE");
            default -> throw new QueryException.QuerySyntaxException(4);
        };
        cmd.setOpField(commandTokens.nextToken());
        checkEndMark();
        return cmd;
    }

    private AST_Alter parseAlter() throws QueryException {
        if (!commandTokens.nextToken().equalsIgnoreCase("TABLE")) {
            throw new QueryException.QuerySyntaxException(10);
        }
        AST_Alter cmd = new AST_Alter();
        cmd.setTable(commandTokens.nextToken());
        switch (commandTokens.nextToken().toUpperCase()) {
            case "ADD" -> cmd.setAlterType("ADD");
            case "DROP" -> cmd.setAlterType("DROP");
            default -> throw new QueryException.QuerySyntaxException(5);
        }
        cmd.setAttribute(commandTokens.nextToken());
        checkEndMark();
        return cmd;
    }

    private AST_Insert parseInsert() throws QueryException {
        if (!commandTokens.nextToken().equalsIgnoreCase("INTO")) {
            throw new QueryException.QuerySyntaxException(6);
        }
        AST_Insert cmd = new AST_Insert(commandTokens.nextToken());
        if (!commandTokens.nextToken().equalsIgnoreCase("VALUES")) {
            throw new QueryException.QuerySyntaxException(6);
        }
        if (!commandTokens.nextToken().equalsIgnoreCase("(")) {
            throw new QueryException.QuerySyntaxException(6);
        }
        cmd.fillValueList(scanValueList());
        checkEndMark();
        return cmd;
    }

    private ArrayList<Object> scanValueList() throws QueryException {
        ArrayList<Object> valueList = new ArrayList<>();
        String token = commandTokens.nextToken();
        StringBuilder tempValue = new StringBuilder();
        while (!token.equals(")")) {
            if (token.equals(",")) {
                valueList.add(scanValue(tempValue.toString()));
                tempValue = new StringBuilder();
            } else {
                tempValue.append(token);
            }
            token = commandTokens.nextToken();
        }
        valueList.add(scanValue(tempValue.toString()));
        return valueList;
    }


    private Object scanValue(String data) throws QueryException {
        if (data.matches("#STR1Ng#")) {
            return getNextStringToken();
        }
        Object value = ToolBox.convertStringToValue(data);
        if (value.equals("^#STRING WITHOUT QUOTE#")) {
            throw new QueryException.QuerySyntaxException(7);
        }
        return value;
    }

    private String getNextStringToken() {
        String token = this.stringTokens.get(this.stringTokensCounter);
        this.stringTokensCounter++;
        return token;
    }

    private AST_Select parseSelect() throws QueryException {
        AST_Select cmd = new AST_Select();
        scanWildAttribute(cmd);
        cmd.setTable(this.commandTokens.nextToken());
        switch (this.commandTokens.nextToken().toUpperCase()) {
            case ";":
                return cmd;
            case "WHERE":
                cmd.setConditionNode(parseCondition());
                checkEndMark();
                return cmd;
            default:
                throw new QueryException.QuerySyntaxException(8);
        }
    }

    private void scanWildAttribute(AST_Select cmd) throws QueryException {
        String token = this.commandTokens.nextToken();
        if (token.equals("*")) {
            cmd.setIsAllAttributeTrue();
            if (!this.commandTokens.nextToken().equalsIgnoreCase("FROM")) {
                throw new QueryException.QuerySyntaxException(8);
            }
        } else {
            cmd.fillAttribute(scanAttributeListEndFrom(token));
        }
    }

    private ArrayList<String> scanAttributeListEndFrom(String FirstAttribute) {
        ArrayList<String> attributeList = new ArrayList<>();
        attributeList.add(FirstAttribute);
        String temp = this.commandTokens.nextToken();
        while (!temp.equalsIgnoreCase("FROM")) {
            if (!temp.equals(",")) {
                attributeList.add(temp);
            }
            temp = this.commandTokens.nextToken();
        }
        return attributeList;
    }

    private AST.condition parseCondition() throws QueryException {
        AST.condition condition = new AST.condition();
        String t1 = this.commandTokens.nextToken();
        if (t1.equals("(")) {
            condition.left = parseCondition();
            checkRightBracket();
            scanConditionRelation(condition);
            checkLeftBracket();
            condition.right = parseCondition();
            checkRightBracket();
        } else {
            scanConditionOperation(condition, t1);
            return condition;
        }
        return condition;
    }

    private void scanConditionOperation(AST.condition condition, String Attribute) throws QueryException {
        condition.setAttribute(Attribute);
        condition.setOperator(identifyOperator(this.commandTokens.nextToken()));
        String nt = this.commandTokens.nextToken();
        Object value;
        if (nt.matches("#STR1Ng#")) {
            value = getNextStringToken();
        } else {
            value = ToolBox.convertStringToValue(nt);
        }
        if (value.equals("^#STRING WITHOUT QUOTE#")) {
            throw new QueryException.QuerySyntaxException(7);
        }
        condition.setValue(value);
    }

    private AST_Update parseUpdate() throws QueryException {
        AST_Update cmd = new AST_Update();
        cmd.setTable(this.commandTokens.nextToken());
        if (!this.commandTokens.nextToken().equalsIgnoreCase("SET")) {
            throw new QueryException.QuerySyntaxException(9);
        }
        scanNameValuePair(cmd);
        cmd.setConditionNode(parseCondition());
        checkEndMark();
        return cmd;
    }

    private void scanNameValuePair(AST_Update cmd) throws QueryException {
        String temp = "", t1 = this.commandTokens.nextToken();
        while (!t1.equalsIgnoreCase("WHERE")) {
            if (t1.equals(",")) {
                addNameValuePair(cmd, temp);
            }
            temp = temp + t1;
            t1 = this.commandTokens.nextToken();
        }
        addNameValuePair(cmd, temp);
    }

    private void addNameValuePair(AST_Update cmd, String pair) throws QueryException {
        String t2 = pair.replaceAll("=", " = ");
        String[] tempToken = t2.split("\\s+");
        if (tempToken[2].matches("#STR1Ng#")) {
            cmd.addNameValuePair(tempToken[0], getNextStringToken());
        }
        Object value = ToolBox.convertStringToValue(tempToken[2]);
        if (value.equals("^#STRING WITHOUT QUOTE#")) {
            throw new QueryException.QuerySyntaxException(7);
        }
        cmd.addNameValuePair(tempToken[0], value);
    }

    private AST_Delete parseDelete() throws QueryException {
        AST_Delete cmd = new AST_Delete();
        if (!this.commandTokens.nextToken().equalsIgnoreCase("FROM")) {
            throw new QueryException.QuerySyntaxException(11);
        }
        cmd.setTable(this.commandTokens.nextToken());
        if (!this.commandTokens.nextToken().equalsIgnoreCase("WHERE")) {
            throw new QueryException.QuerySyntaxException(11);
        }
        cmd.setConditionNode(parseCondition());
        checkEndMark();
        return cmd;
    }

    private AST_Join parseJoin() throws QueryException {
        AST_Join cmd = new AST_Join();
        cmd.setLeftTable(this.commandTokens.nextToken());
        if (!this.commandTokens.nextToken().equalsIgnoreCase("AND")) {
            throw new QueryException.QuerySyntaxException(12);
        }
        cmd.setRightTable(this.commandTokens.nextToken());
        if (!this.commandTokens.nextToken().equalsIgnoreCase("ON")) {
            throw new QueryException.QuerySyntaxException(12);
        }
        cmd.setLeftAttribute(this.commandTokens.nextToken());
        if (!this.commandTokens.nextToken().equalsIgnoreCase("AND")) {
            throw new QueryException.QuerySyntaxException(12);
        }
        cmd.setRightAttribute(this.commandTokens.nextToken());
        checkEndMark();
        return cmd;
    }

    private void checkRightBracket() throws QueryException {
        if (!this.commandTokens.nextToken().equals(")")) {
            throw new QueryException.QuerySyntaxException(13);
        }
    }

    private void checkLeftBracket() throws QueryException {
        if (!this.commandTokens.nextToken().equals("(")) {
            throw new QueryException.QuerySyntaxException(13);
        }
    }

    private void scanConditionRelation(AST.condition con) throws QueryException {
        String token = this.commandTokens.nextToken().toUpperCase();
        if (token.equals("AND")) {
            con.setAND();
        } else if (token.equals("OR")) {
            con.setOR();
        } else {
            throw new QueryException.QuerySyntaxException(14);
        }
    }

    private void checkEndMark() throws QueryException {
        if (!(commandTokens.nextToken().equals(";"))) {
            throw new QueryException.QuerySyntaxException(15);
        }
    }

    private static AST.OPERATOR identifyOperator(String OpStr) {
        return switch (OpStr.toUpperCase()) {
            case "==" -> AST.OPERATOR.EQ;
            case ">" -> AST.OPERATOR.GREATER;
            case "<" -> AST.OPERATOR.SMALLER;
            case ">=" -> AST.OPERATOR.EQGREATER;
            case "<=" -> AST.OPERATOR.EQSMALLER;
            case "!=" -> AST.OPERATOR.NEQ;
            case "LIKE" -> AST.OPERATOR.LIKE;
            default -> null;
        };
    }
}
