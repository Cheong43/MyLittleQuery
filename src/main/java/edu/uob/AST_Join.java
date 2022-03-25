package edu.uob;

import static edu.uob.AST.ASType.JOIN;

public class AST_Join extends AST {

    public String leftTable;
    public String rightTable;
    public String leftAttribute;
    public String rightAttribute;

    public AST_Join() {
        this.ASType = JOIN;
    }

    public void setLeftAttribute(String leftAttribute) {
        this.leftAttribute = leftAttribute;
    }

    public void setLeftTable(String leftTable) {
        this.leftTable = leftTable;
    }

    public void setRightAttribute(String rightAttribute) {
        this.rightAttribute = rightAttribute;
    }

    public void setRightTable(String rightTable) {
        this.rightTable = rightTable;
    }

    @Override
    public AST.ASType getASType() {
        return this.ASType;
    }

    @Override
    public void print() {
        System.out.println(leftTable);
        System.out.println(rightTable);
        System.out.println(leftAttribute);
        System.out.println(rightAttribute);
    }
}
