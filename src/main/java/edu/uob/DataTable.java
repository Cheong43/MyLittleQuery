package edu.uob;

import java.util.*;

public class DataTable {
    private int row;
    private int col;
    private final ArrayList<String> header;
    public LinkedHashMap<Integer, ArrayList<Object>> data;
    private int idCounter;

    public DataTable() {
        this.row = 0;
        this.col = 0;
        this.header = new ArrayList<>();
        this.data = new LinkedHashMap<>();
        this.idCounter = 0;
        initIDCol();
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    private void addRow() {
        this.row++;
    }

    private void addCol() {
        this.col++;
    }

    private void subtractRow() {
        this.row--;
    }

    private void subtractCol() {
        this.col--;
    }

    public int getIndexOfAttribute(String attributeName) {
        return this.header.indexOf(attributeName);
    }

    public boolean isAttributeExist(String attributeName) {
        return this.header.contains(attributeName);
    }

    public ArrayList<String> getHeader() {
        return new ArrayList<>(this.header);
    }

    public ArrayList<Object> getDataById(int id) {
        return new ArrayList<>(this.data.get(id));
    }

    public void addIDCounter() {
        this.idCounter++;
    }

    public int getIDCounter() {
        return idCounter;
    }

    private void setIdCounter(int num) {
        this.idCounter = num;
    }

    public boolean isIdExist(int num) {
        return data.containsKey(num);
    }

    public SortedSet<Integer> getIdSet() {
        return new TreeSet<>(this.data.keySet());
    }

    public Object getDataCell(int id, String attribute) {
        return this.data.get(id).get(getIndexOfAttribute(attribute) - 1);
    }

    public void setDataCell(int id, String attribute, Object value) {
        this.data.get(id).set(getIndexOfAttribute(attribute) - 1, value);
    }

    public void addAttribute(String attributeName) {
        this.header.add(attributeName);
        addOneFullNullColumn();
        addCol();
    }

    public void deleteAttribute(String attributeName) {
        Set<Integer> keys = this.data.keySet();
        for (int key : keys) {
            this.data.get(key).remove(getIndexOfAttribute(attributeName) - 1);// Offset for the id occupied col
        }
        this.header.remove(getIndexOfAttribute(attributeName));
        subtractCol();
    }

    public void addDataByRow(int id, ArrayList<Object> data) {
        if (id == 0) {
            addIDCounter();
        } else {
            setIdCounter(id);
        }
        this.data.put(this.getIDCounter(), data);
        addRow();
    }

    public void deleteDataByRow(int id) {
        this.data.remove(id);
        subtractRow();
    }

    private void addOneFullNullColumn() {
        Set<Integer> keys = this.data.keySet();
        for (int key : keys) {
            this.data.get(key).add(new DataNullCell());
        }
    }

    private void initIDCol() {
        this.header.add("id");
        addCol();
    }
}
