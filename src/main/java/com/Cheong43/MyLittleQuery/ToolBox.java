package com.Cheong43.MyLittleQuery;

public class ToolBox {


    public static Object convertStringToValue(String rawData) {
        if (rawData.matches("[-|+]?\\d+")) {
            return Integer.valueOf(rawData);
        } else if (rawData.matches("[-|+]?\\d+(\\.\\d+)?")) {
            return Double.valueOf(rawData);
        } else if (rawData.matches("[Ff][Aa][Ll][Ss][Ee]|[Tt][Rr][Uu][Ee]")) {
            return Boolean.valueOf(rawData);
        } else if (rawData.matches("[Nn][Uu][Ll][Ll]")) {
            return new DataNullCell();
        } else {
            return "^#STRING WITHOUT QUOTE#";
        }
    }

}
