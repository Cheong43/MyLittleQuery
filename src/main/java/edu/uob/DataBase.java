package edu.uob;

import java.util.*;

public class DataBase {

    private final TreeMap<String, DataTable> Database;

    public DataBase() {
        this.Database = new TreeMap<>();
    }

    public void addTable(String tableName) {
        this.Database.put(tableName, new DataTable());
    }

    public void deleteTable(String tableName) {
        this.Database.remove(tableName);
    }

    public boolean isTableExist(String tableName) {
        return this.Database.containsKey(tableName);
    }

    public DataTable getTableByName(String tableName) {
        return this.Database.get(tableName);
    }

    public Set<String> getTableNameSet() {
        return this.Database.keySet();
    }

}


