package edu.uob;

import static edu.uob.AST.ALTER_TYPE.ADD;
import static edu.uob.AST.ALTER_TYPE.DROP;
import static edu.uob.AST.ASType.ALTER;

public class AST_Alter extends AST {

    public String table;
    public ALTER_TYPE alterType;
    public String attribute;

    public AST_Alter() {
        this.ASType = ALTER;
    }

    public void setAlterType(String alterType) {
        switch (alterType) {
            case "ADD":
                this.alterType = ADD;
                break;
            case "DROP":
                this.alterType = DROP;
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
