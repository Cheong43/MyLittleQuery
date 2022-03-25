package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class DBTableIO {

    private final File filePath;

    public DBTableIO(File FileDirectory) {
        this.filePath = FileDirectory;
    }

    public void addTableFromTabFile(DataBase dataBase) throws DBException {
        String tableName = DBServerIO.extractFileName(this.filePath.getName()).toLowerCase();
        scanTableFromTabFile(dataBase, tableName);
    }

    private void scanTableFromTabFile(DataBase db, String tableName) throws DBException {
        db.addTable(tableName);
        BufferedReader scanner = openFileDirectory();
        try {
            scanHeaderFromScanner(db.getTableByName(tableName), scanner.readLine());//First Row
            addDataFromTable(db.getTableByName(tableName), scanner);
            scanner.close();
        } catch (IOException e) {
            throw new DBException.filePointerIsNullException(filePath);
        }
    }

    private BufferedReader openFileDirectory() throws DBException {
        BufferedReader scanner;
        try {
            scanner = new BufferedReader(new FileReader(this.filePath));
        } catch (FileNotFoundException FileNotFound) {
            throw new DBException.FileNotFoundException(this.filePath);
        }
        return scanner;
    }

    private void scanHeaderFromScanner(DataTable table, String dataLine) {
        Scanner scanner = new Scanner(dataLine);
        scanner.next();
        while (scanner.hasNext()) {
            table.addAttribute(scanner.next());
        }
        scanner.close();
    }

    private void addDataFromTable(DataTable table, BufferedReader scanner) throws DBException {
        String currentLine;
        try {
            while ((currentLine = scanner.readLine()) != null) {
                addLineToTable(table, currentLine);
            }
        } catch (IOException e) {
            throw new DBException.filePointerIsNullException(filePath);
        }
    }

    private void addLineToTable(DataTable table, String curLine) {
        if (curLine.length() > 0) {
            String[] temp = curLine.split("\\s+");
            ArrayList<Object> data = new ArrayList<>();
            for (int i = 1; i < table.getCol(); i++) {
                Object t1 = ToolBox.convertStringToValue(temp[i]);
                if (t1.equals("^#STRING WITHOUT QUOTE#")) {
                    data.add(temp[i]);
                } else {
                    data.add(t1);
                }
            }
            table.addDataByRow(Integer.parseInt(temp[0]), data);
        }
    }

    public void writeTableToFile(DataTable table) throws DBException {
        try {
            FileWriter writer = new FileWriter(this.filePath, false);
            //First Line
            for (int i = 0; i < table.getCol(); i++) {
                writer.write(table.getHeader().get(i));
                writer.write(" ");
            }
            writer.write("\n");
            // Rest of Data
            Set<Integer> idSet = table.getIdSet();
            for (Integer key : idSet) {
                writer.write(key.toString());
                writer.write(" ");
                for (Object cell : table.getDataById(key)) {
                    writer.write(cell.toString());
                    writer.write(" ");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new DBException.filePointerIsNullException(this.filePath);
        }

    }

}
