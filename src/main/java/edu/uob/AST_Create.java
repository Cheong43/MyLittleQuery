package edu.uob;

import java.util.ArrayList;

import static edu.uob.AST.ASType.CREATE;
import static edu.uob.AST.CREATE_MODE.DATABASE;
import static edu.uob.AST.CREATE_MODE.TABLE;

public class AST_Create extends AST {

    public CREATE_MODE createMode;
    public String opField;
    public ArrayList<String> attributeList;

    public AST_Create(String CreateMode) {
        this.ASType = CREATE;
        this.attributeList = new ArrayList<>();
        switch (CreateMode) {
            case "TABLE":
                this.createMode = TABLE;
                break;
            case "DATABASE":
                this.createMode = DATABASE;
                break;
            default:
                // error
        }
    }

    public void setOpField(String opField) {
        this.opField = opField;
    }

    public void fillAttribute(ArrayList<String> attributeList) {
        this.attributeList.addAll(attributeList);
    }

    @Override
    public AST.ASType getASType() {
        return this.ASType;
    }

    @Override
    public void print() {
        System.out.println(createMode);
        System.out.println(opField);
        System.out.println(attributeList);
    }
}
