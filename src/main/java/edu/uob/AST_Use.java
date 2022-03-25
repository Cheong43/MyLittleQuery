package edu.uob;

import static edu.uob.AST.ASType.USE;

public class AST_Use extends AST {

    public String dataBase;

    public AST_Use(String dataBase) {
        this.dataBase = dataBase;
        this.ASType = USE;
    }

    @Override
    public ASType getASType() {
        return USE;
    }

    @Override
    public void print() {
        System.out.println(this.ASType);
        System.out.println(dataBase);
    }
}
