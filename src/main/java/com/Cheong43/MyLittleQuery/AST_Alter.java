package com.Cheong43.MyLittleQuery;

public class AST_Alter extends AST {

    public String table;
    public ALTER_TYPE alterType;
    public String attribute;

    public AST_Alter() {
        this.ASType = AST.ASType.ALTER;
    }

    public void setAlterType(String alterType) {
        switch (alterType) {
            case "ADD":
                this.alterType = ALTER_TYPE.ADD;
                break;
            case "DROP":
                this.alterType = ALTER_TYPE.DROP;
                break;
            default:
                //error
                break;
        }
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public AST.ASType getASType() {
        return this.ASType;
    }

    @Override
    public void print() {
        System.out.println(table);
        System.out.println(alterType);
        System.out.println(attribute);
    }
}
