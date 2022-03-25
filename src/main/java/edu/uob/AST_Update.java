package edu.uob;

import java.util.ArrayList;

import static edu.uob.AST.ASType.UPDATE;

public class AST_Update extends AST {

    public String table;
    public condition conditionNode;
    public ArrayList<String> nameList;
    public ArrayList<Object> valueList;
    public int nameValueCounter;

    public AST_Update() {
        this.ASType = UPDATE;
        nameValueCounter = 0;
        this.nameList = new ArrayList<>();
        this.valueList = new ArrayList<>();
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setConditionNode(condition conditionNode) {
        this.conditionNode = conditionNode;
    }

    public void addNameValuePair(String name, Object value) {
        this.nameList.add(name);
        this.valueList.add(value);
        this.nameValueCounter++;
    }

    @Override
    public AST.ASType getASType() {
        return this.ASType;
    }

    @Override
    public void print() {
        System.out.println(getASType());
        System.out.println(table);
    }
}
