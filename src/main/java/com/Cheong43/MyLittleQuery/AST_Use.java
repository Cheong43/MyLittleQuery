package com.Cheong43.MyLittleQuery;

public class AST_Use extends AST {

    public String dataBase;

    public AST_Use(String dataBase) {
        this.dataBase = dataBase;
        this.ASType = AST.ASType.USE;
    }

    @Override
    public ASType getASType() {
        return AST.ASType.USE;
    }

    @Override
    public void print() {
        System.out.println(this.ASType);
        System.out.println(dataBase);
    }
}
