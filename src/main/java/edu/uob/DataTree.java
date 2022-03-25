package edu.uob;

import java.io.File;
import java.util.TreeMap;

public class DataTree {

    private final TreeMap<String, DataBase> serverDataTree;
    private String CurrentDataBase;
    private final File rootDirectory;

    public DataTree(File directory) {
        this.serverDataTree = new TreeMap<>();
        this.CurrentDataBase = null;
        this.rootDirectory = directory;
    }

    public void addDataBase(String dbName) {
        this.serverDataTree.put(dbName, new DataBase());
    }

    public DataBase getDataBase(String DBName) {
        return this.serverDataTree.get(DBName);
    }

    public boolean isDataBaseExist(String DBName) {
        return this.serverDataTree.containsKey(DBName);
    }

    public void setCurrentDataBase(String dbname) {
        this.CurrentDataBase = dbname;
    }

    public String getCurrentDataBaseName() {
        return this.CurrentDataBase;
    }

    public File getRootDirectory() {
        return this.rootDirectory;
    }

    public void deleteDataBase(String dbname) {
        this.serverDataTree.remove(dbname);
    }

    public DataBase getCurrentDB() {
        return this.serverDataTree.get(getCurrentDataBaseName());
    }
}
