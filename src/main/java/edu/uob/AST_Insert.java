package edu.uob;

import java.util.ArrayList;

import static edu.uob.AST.ASType.INSERT;

public class AST_Insert extends AST {

    public String table;
    public ArrayList<Object> valueList;

    public AST_Insert(String table) {
        this.ASType = INSERT;
        this.table = table;
        this.valueList = new ArrayList<>();
    }

    public void fillValueList(ArrayList<Object> list) {
        this.valueList.addAll(list);
    }

    @Override
    public AST.ASType getASType() {
        return this.ASType;
    }

    @Override
    public void print() {
        System.out.println(this.table);
        System.out.println(this.valueList);
    }
}
