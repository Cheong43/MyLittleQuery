package com.Cheong43.MyLittleQuery;

import static com.Cheong43.MyLittleQuery.AST.ASType.DROP;
import static com.Cheong43.MyLittleQuery.AST.DROP_MODE.DATABASE;
import static com.Cheong43.MyLittleQuery.AST.DROP_MODE.TABLE;

public class AST_Drop extends AST {

    public DROP_MODE dropMode;
    public String opField;

    public AST_Drop(String dropMode) {
        this.ASType = DROP;
        switch (dropMode) {
            case "TABLE":
                this.dropMode = TABLE;
                break;
            case "DATABASE":
                this.dropMode = DATABASE;
                break;
            default:
                //error
                break;
        }
    }

    public void setOpField(String opField) {
        this.opField = opField;
    }

    @Override
    public AST.ASType getASType() {
        return this.ASType;
    }

    @Override
    public void print() {
        System.out.println(opField);
        System.out.println(dropMode);
    }
}
