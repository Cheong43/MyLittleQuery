package com.Cheong43.MyLittleQuery;

import static com.Cheong43.MyLittleQuery.AST.ASType.DELETE;

public class AST_Delete extends AST {

    public String table;
    public condition conditionNode;

    public AST_Delete() {
        this.ASType = DELETE;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setConditionNode(condition conditionNode) {
        this.conditionNode = conditionNode;
    }

    @Override
    public AST.ASType getASType() {
        return this.ASType;
    }

    @Override
    public void print() {
        System.out.println(getASType());
        System.out.println(table);
        System.out.println(conditionNode);
    }
}
