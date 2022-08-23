package com.Cheong43.MyLittleQuery;

import java.util.ArrayList;

public class AST_Select extends AST {

    public String table;
    public ArrayList<String> attributeList;
    public condition conditionNode;
    public boolean isAllAttribute;

    public AST_Select() {
        this.ASType = AST.ASType.SELECT;
        this.isAllAttribute = false;
        this.attributeList = new ArrayList<>();
    }

    public void setIsAllAttributeTrue() {
        this.isAllAttribute = true;
    }

    public void fillAttribute(ArrayList<String> list) {
        this.attributeList.addAll(list);
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
        System.out.println(table);
        System.out.println(attributeList);
        System.out.println(conditionNode);
        System.out.println(isAllAttribute);
    }
}
