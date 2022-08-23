package com.Cheong43.MyLittleQuery;

public abstract class AST {

    enum ASType {
        USE,
        CREATE,
        DROP,
        ALTER,
        INSERT,
        SELECT,
        UPDATE,
        DELETE,
        JOIN
    }

    public enum ALTER_TYPE {
        ADD,
        DROP
    }

    public enum DROP_MODE {
        TABLE,
        DATABASE
    }


    public enum CREATE_MODE {
        TABLE,
        DATABASE
    }

    public enum OPERATOR {
        EQ,
        GREATER,
        SMALLER,
        EQGREATER,
        EQSMALLER,
        NEQ,
        LIKE
    }

    public enum CON_RELATION {
        AND,
        OR
    }

    public static class condition {
        public condition right;
        public condition left;
        public CON_RELATION relation;

        public String attribute;
        public OPERATOR operator;
        public Object value;

        public condition() {
        }

        public void setAND() {
            this.relation = CON_RELATION.AND;
        }

        public void setOR() {
            this.relation = CON_RELATION.OR;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public void setOperator(OPERATOR operator) {
            this.operator = operator;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public static ASType ASType;

    public abstract ASType getASType();

    public abstract void print();
}
