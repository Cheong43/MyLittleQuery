package com.Cheong43.MyLittleQuery;

import java.io.*;
import java.util.Set;

public class DBServerIO {

    private final File rootDirectory;

    public DBServerIO(File databaseDirectory) {
        this.rootDirectory = databaseDirectory;
    }

    public void readAllDataBase(DataTree serverDataTree) throws DBException {
        try {
            File[] files = this.rootDirectory.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory() && !serverDataTree.isDataBaseExist(file.getName())) {
                    serverDataTree.addDataBase(file.getName());
                    readDataBaseDirectory(serverDataTree.getDataBase(file.getName()), file.getName());
                }
            }
        }catch (NullPointerException exception){
            throw new DBException.filePointerIsNullException(this.rootDirectory);
        }

    }

    private void readDataBaseDirectory(DataBase database, String dbName) throws DBException {
        File Directory = new File(this.rootDirectory + File.separator + dbName);
        File[] files = Directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.toString().endsWith(".tab")) {
                DBTableIO fD = new DBTableIO(file);
                fD.addTableFromTabFile(database);
            }
        }
    }

    public void writeDataBaseToFileSystem(DataTree dataTree, String dbName) throws DBException {
        checkIfDirectoryValid();
        File[] files = this.rootDirectory.listFiles();
        boolean isFolderExist = false;
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equals(dbName)) {
                writeDataBaseProcess(file, dataTree.getDataBase(dbName));
                isFolderExist = true;
            }
        }
        if (!isFolderExist) {
            File file = new File(this.rootDirectory + File.separator + dbName);
            file.mkdir();
            writeDataBaseProcess(file, dataTree.getDataBase(dbName));
        }
    }

    public void writeTableToFileSystem(DataTree dataTree, String dbName, String tableName) throws DBException {
        checkIfDirectoryValid();
        File[] files = this.rootDirectory.listFiles();
        boolean isFolderExist = false;
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equals(dbName)) {
                writeTableProcess(file, dataTree.getDataBase(dbName), tableName);
                isFolderExist = true;
            }
        }
        if (!isFolderExist) {
            File file = new File(this.rootDirectory + File.separator + dbName);
            file.mkdir();
            writeTableProcess(file, dataTree.getDataBase(dbName), tableName);
        }
    }

    private void writeDataBaseProcess(File dbFolder, DataBase db) throws DBException {
        Set<String> tableSet = db.getTableNameSet();
        for (String key : tableSet) {
            DBTableIO fileIO = new DBTableIO(new File(dbFolder + File.separator + key + ".tab"));
            fileIO.writeTableToFile(db.getTableByName(key));
        }
    }

    private void writeTableProcess(File dbFolder, DataBase db, String tableName) throws DBException {
        DBTableIO fileIO = new DBTableIO(new File(dbFolder + File.separator + tableName + ".tab"));
        fileIO.writeTableToFile(db.getTableByName(tableName));

    }

    public void createDbFolder(String dbName) throws DBException {
        checkIfDirectoryValid();
        File[] files = this.rootDirectory.listFiles();
        boolean isFolderExist = false;
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equals(dbName)) {
                isFolderExist = true;
            }
        }
        if (!isFolderExist) {
            File file = new File(this.rootDirectory + File.separator + dbName);
            file.mkdir();
        }
    }

    public void deleteDbFolder(String dbName) throws DBException {
        checkIfDirectoryValid();
        File[] files = this.rootDirectory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equals(dbName)) {
                file.delete();
            }
        }
    }

    public void deleteTable(String dbName, String tableName) throws DBException {
        checkIfDirectoryValid();
        File[] files = this.rootDirectory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory() && file.getName().equals(dbName)) {
                File[] dbFolder = file.listFiles();
                assert dbFolder != null;
                for (File tab : dbFolder) {
                    if (extractFileName(tab.toString()).equals(tableName)) {
                        tab.delete();
                    }
                }
            }
        }
    }

    public void checkIfDirectoryValid() throws DBException {
        if (!this.rootDirectory.isDirectory()) {
            throw new DBException.DirectoryInvalidException(this.rootDirectory);
        }
    }

    public static String extractFileName(String str) {
        return str.replaceAll("^.*?(([^/\\\\.]+))\\.[^.]+$", "$1");
    }

}


