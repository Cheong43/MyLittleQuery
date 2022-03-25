package edu.uob;


import java.util.*;

public class QueryInterpret {

    private final DataTree DTree;
    private String output;

    public QueryInterpret(DataTree DTree) {
        this.DTree = DTree;
        this.output = "";
    }

    public void run(AST ast) throws DBException, QueryException {
        switch (ast.getASType()) {
            case USE -> use((AST_Use) ast);
            case CREATE -> create((AST_Create) ast);
            case DROP -> drop((AST_Drop) ast);
            case ALTER -> alter((AST_Alter) ast);
            case INSERT -> insert((AST_Insert) ast);
            case SELECT -> select((AST_Select) ast);
            case UPDATE -> update((AST_Update) ast);
            case DELETE -> delete((AST_Delete) ast);
            case JOIN -> join((AST_Join) ast);
        }
    }

    private void use(AST_Use ast) throws QueryException {
        if (this.DTree.isDataBaseExist(ast.dataBase)) {
            this.DTree.setCurrentDataBase(ast.dataBase);
        } else {
            throw new QueryException.InterpretFailException(17);
        }
    }

    private void create(AST_Create ast) throws DBException, QueryException {
        switch (ast.createMode) {
            case TABLE -> createTable(ast);
            case DATABASE -> createDataBase(ast);
            default -> throw new QueryException.InterpretFailException(18);
        }
    }

    private void createTable(AST_Create ast) throws DBException, QueryException {
        checkWorkingDataBaseExist();
        if(this.DTree.getCurrentDB().isTableExist(ast.opField)){
            throw new QueryException.InterpretFailException(32);
        }
        this.DTree.getCurrentDB().addTable(ast.opField);
        if (ast.attributeList.size() > 0) {
            addAttributes(ast);
        }
        writeTable(ast.opField);
    }

    private void addAttributes(AST_Create ast) throws QueryException {
        for (String attribute : ast.attributeList) {
            if (!this.DTree.getCurrentDB().getTableByName(ast.opField).isAttributeExist(attribute)) {
                this.DTree.getCurrentDB().getTableByName(ast.opField).addAttribute(attribute);
            } else {
                throw new QueryException.InterpretFailException(19);
            }
        }
    }

    private void createDataBase(AST_Create ast) throws DBException, QueryException {
        if (!this.DTree.isDataBaseExist(ast.opField)) {
            this.DTree.addDataBase(ast.opField);
        } else {
            throw new QueryException.InterpretFailException(20);
        }
        DBServerIO fileIO = new DBServerIO(this.DTree.getRootDirectory());
        fileIO.createDbFolder(ast.opField);
    }

    private void drop(AST_Drop ast) throws DBException, QueryException {
        if (ast.dropMode == AST.DROP_MODE.TABLE) {
            dropTable(ast);
        } else if (ast.dropMode == AST.DROP_MODE.DATABASE) {
            dropDataBase(ast);
        }
    }

    private void dropTable(AST_Drop ast) throws DBException, QueryException {

        checkWorkingDataBaseExist();
        checkTableExistOnCurDB(ast.opField);

        this.DTree.getCurrentDB().deleteTable(ast.opField);

        DBServerIO fileIO = new DBServerIO(this.DTree.getRootDirectory());
        fileIO.deleteTable(this.DTree.getCurrentDataBaseName(), ast.opField);

    }

    private void dropDataBase(AST_Drop ast) throws DBException, QueryException {
        if (this.DTree.isDataBaseExist(ast.opField)) {
            this.DTree.deleteDataBase(ast.opField);
        } else {
            throw new QueryException.InterpretFailException(21);
        }
        DBServerIO fileIO = new DBServerIO(this.DTree.getRootDirectory());
        fileIO.deleteDbFolder(ast.opField);
    }

    private void alter(AST_Alter ast) throws DBException, QueryException {
        checkWorkingDataBaseExist();
        checkTableExistOnCurDB(ast.table);
        if (ast.alterType == AST.ALTER_TYPE.ADD) {
            alterAdd(ast);
        } else if (ast.alterType == AST.ALTER_TYPE.DROP) {
            alterDrop(ast);
        }
        writeTable(ast.table);
    }

    private void alterAdd(AST_Alter ast) {
        DataTable workTable = this.DTree.getCurrentDB().getTableByName(ast.table);
        workTable.addAttribute(ast.attribute);
    }

    private void alterDrop(AST_Alter ast) {
        DataTable workTable = this.DTree.getCurrentDB().getTableByName(ast.table);
        if (workTable.isAttributeExist(ast.attribute)) {
            workTable.deleteAttribute(ast.attribute);
        }
    }

    private void insert(AST_Insert ast) throws DBException, QueryException {
        checkWorkingDataBaseExist();
        checkTableExistOnCurDB(ast.table);
        if (ast.valueList.size() > this.DTree.getCurrentDB().getTableByName(ast.table).getCol() - 1) {
            throw new QueryException.InterpretFailException(22);
        }
        this.DTree.getCurrentDB().getTableByName(ast.table).addDataByRow(0, ast.valueList);
        writeTable(ast.table);
    }

    private void select(AST_Select ast) throws QueryException {
        checkWorkingDataBaseExist();
        checkTableExistOnCurDB(ast.table);
        // Get recursive Condition Index List
        DataTable curTable = this.DTree.getCurrentDB().getTableByName(ast.table);
        SortedSet<Integer> idSet;
        if (ast.conditionNode == null) {
            Set<Integer> tempSet = curTable.getIdSet();
            idSet = new TreeSet<>(tempSet);
        } else {
            idSet = interpretCondition(ast.conditionNode, curTable);
        }
        // Get Attribute List
        if (ast.isAllAttribute) {
            printTable(curTable, curTable.getHeader(), idSet);
        } else {
            checkAttributeList(ast);
            printTable(curTable, ast.attributeList, idSet);
        }
    }

    private void checkAttributeList(AST_Select ast) throws QueryException {
        for (String attribute : ast.attributeList) {
            if (!this.DTree.getCurrentDB().getTableByName(ast.table).isAttributeExist(attribute)) {
                throw new QueryException.InterpretFailException(23);
            }
        }
    }

    private void printTable(DataTable table, ArrayList<String> attributes, SortedSet<Integer> idSet) {
        StringBuilder line = new StringBuilder();

        // Header
        for (String attribute : attributes) {
            line.append(attribute).append(" ");
        }
        addALineToOutput(line.toString());

        // Rest of Data
        for (int key : idSet) {
            line = new StringBuilder();
            for (String attribute : attributes) {
                if (!attribute.equals("id")) {
                    line.append(table.getDataCell(key, attribute)).append(" ");
                } else {
                    line.append(key).append(" ");
                }
            }
            addALineToOutput(line.toString());
        }
    }

    private SortedSet<Integer> interpretCondition(AST.condition condition, DataTable table) throws QueryException {
        if (condition.left == null && condition.right == null) {
            return getCondition(condition, table);
        } else {
            assert condition.left != null;
            SortedSet<Integer> l = interpretCondition(condition.left, table);
            SortedSet<Integer> r = interpretCondition(condition.right, table);
            return switch (condition.relation) {
                case AND -> conditionAND(l, r);
                case OR -> conditionOR(l, r);
            };
        }
    }

    private SortedSet<Integer> conditionAND(SortedSet<Integer> l, SortedSet<Integer> r) {
        SortedSet<Integer> andSet = new TreeSet<>();
        for (int key : l) {
            if (r.contains(key)) {
                andSet.add(key);
            }
        }
        return andSet;
    }

    private SortedSet<Integer> conditionOR(SortedSet<Integer> l, SortedSet<Integer> r) {
        SortedSet<Integer> orSet = new TreeSet<>();
        orSet.addAll(l);
        orSet.addAll(r);
        return orSet;
    }

    private SortedSet<Integer> getCondition(AST.condition condition, DataTable table) throws QueryException {
        String attribute = condition.attribute;
        checkAttributeExist(table, attribute);
        SortedSet<Integer> setNumbers = new TreeSet<>();
        if (attribute.equals(table.getHeader().get(0))) {
            setNumbers.addAll(idConditionOperation(condition, table));
        } else {
            setNumbers.addAll(attributeConOperation(condition, table));
        }
        return setNumbers;
    }

    private Set<Integer> attributeConOperation(AST.condition condition, DataTable table) throws QueryException {
        return switch (condition.operator) {
            case EQ -> attributeConEQ(condition, table);
            case NEQ -> attributeConNEQ(condition, table);
            case GREATER, EQGREATER, SMALLER, EQSMALLER -> attributeCompare(condition, table);
            case LIKE -> attributeConLIKE(condition, table);
        };
    }

    private Set<Integer> attributeConLIKE(AST.condition condition, DataTable table) throws QueryException {
        Set<Integer> idSet = table.getIdSet();
        if (!(condition.value instanceof String)) {
            throw new QueryException.InterpretFailException(24);
        }
        idSet.removeIf(id -> !table.getDataCell(id, condition.attribute).toString().contains((String) condition.value));
        return idSet;
    }

    private Set<Integer> attributeConEQ(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> !table.getDataCell(id, condition.attribute).equals(condition.value));
        return idSet;
    }

    private Set<Integer> attributeConNEQ(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> table.getDataCell(id, condition.attribute).equals(condition.value));
        return idSet;
    }

    private Set<Integer> attributeCompare(AST.condition condition, DataTable table) throws QueryException {
        Set<Integer> idSet;
        if (condition.value instanceof Integer) {
            idSet = intValueCompare(condition, table);
        } else if (condition.value instanceof Double) {
            idSet = doubleValueCompare(condition, table);
        } else {
            throw new QueryException.InterpretFailException(25);
        }
        return idSet;
    }

    private Set<Integer> doubleValueCompare(AST.condition condition, DataTable table) throws QueryException {
        return switch (condition.operator) {
            case GREATER -> doubleGREATERCon(condition, table);
            case SMALLER -> doubleSMALLERCon(condition, table);
            case EQGREATER -> doubleEQGREATERCon(condition, table);
            case EQSMALLER -> doubleEQSMALLERCon(condition, table);
            default -> throw new QueryException.InterpretFailException(25);
        };
    }

    private Set<Integer> doubleGREATERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Double) table.getDataCell(id, condition.attribute) <= (Double) condition.value);
        return idSet;
    }

    private Set<Integer> doubleSMALLERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Double) table.getDataCell(id, condition.attribute) >= (Double) condition.value);
        return idSet;
    }

    private Set<Integer> doubleEQGREATERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Double) table.getDataCell(id, condition.attribute) < (Double) condition.value);
        return idSet;
    }

    private Set<Integer> doubleEQSMALLERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Double) table.getDataCell(id, condition.attribute) > (Double) condition.value);
        return idSet;
    }

    private Set<Integer> intValueCompare(AST.condition condition, DataTable table) throws QueryException {
        return switch (condition.operator) {
            case GREATER -> intGREATERCon(condition, table);
            case SMALLER -> intSMALLERCon(condition, table);
            case EQGREATER -> intEQGREATERCon(condition, table);
            case EQSMALLER -> intEQSMALLERCon(condition, table);
            default -> throw new QueryException.InterpretFailException(25);
        };
    }

    private Set<Integer> intGREATERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Integer) table.getDataCell(id, condition.attribute) <= (Integer) condition.value);
        return idSet;
    }

    private Set<Integer> intSMALLERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Integer) table.getDataCell(id, condition.attribute) >= (Integer) condition.value);
        return idSet;
    }

    private Set<Integer> intEQGREATERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Integer) table.getDataCell(id, condition.attribute) < (Integer) condition.value);
        return idSet;
    }

    private Set<Integer> intEQSMALLERCon(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(id -> (Integer) table.getDataCell(id, condition.attribute) > (Integer) condition.value);
        return idSet;
    }

    private Set<Integer> idConditionOperation(AST.condition condition, DataTable table) throws QueryException {
        if (!(condition.value instanceof Integer)) {
            throw new QueryException.InterpretFailException(26);
        }
        return switch (condition.operator) {
            case EQ -> idConditionEQ(condition, table);
            case NEQ -> idConditionNEQ(condition, table);
            case EQGREATER -> idConditionEQGREATER(condition, table);
            case EQSMALLER -> idConditionEQSMALLER(condition, table);
            case GREATER -> idConditionGREATER(condition, table);
            case SMALLER -> idConditionSMALLER(condition, table);
            default -> throw new QueryException.InterpretFailException(27);
        };
    }

    private Set<Integer> idConditionEQ(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(element -> element != condition.value);
        return idSet;
    }

    private Set<Integer> idConditionNEQ(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(element -> element == condition.value);
        return idSet;
    }

    private Set<Integer> idConditionGREATER(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(element -> element <= (Integer) condition.value);
        return idSet;
    }

    private Set<Integer> idConditionEQGREATER(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(element -> element < (Integer) condition.value);
        return idSet;
    }

    private Set<Integer> idConditionSMALLER(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(element -> element >= (Integer) condition.value);
        return idSet;
    }

    private Set<Integer> idConditionEQSMALLER(AST.condition condition, DataTable table) {
        Set<Integer> idSet = table.getIdSet();
        idSet.removeIf(element -> element > (Integer) condition.value);
        return idSet;
    }

    private void update(AST_Update ast) throws DBException, QueryException {
        checkWorkingDataBaseExist();
        checkTableExistOnCurDB(ast.table);
        // Get recursive Condition Index List
        DataTable curTable = this.DTree.getCurrentDB().getTableByName(ast.table);
        SortedSet<Integer> idSet = interpretCondition(ast.conditionNode, curTable);

        for (int key : idSet) {
            for (int i = 0; i < ast.nameValueCounter; i++) {
                if (ast.nameList.get(i).equals("id") || !curTable.isAttributeExist(ast.nameList.get(i))) {
                    throw new QueryException.InterpretFailException(28);
                }
                curTable.setDataCell(key, ast.nameList.get(i), ast.valueList.get(i));
            }
        }
        writeTable(ast.table);
    }

    private void delete(AST_Delete ast) throws DBException, QueryException {
        checkWorkingDataBaseExist();
        checkTableExistOnCurDB(ast.table);
        // Get recursive Condition Index List
        DataTable curTable = this.DTree.getCurrentDB().getTableByName(ast.table);
        SortedSet<Integer> idSet = interpretCondition(ast.conditionNode, curTable);
        for (int key : idSet) {
            curTable.deleteDataByRow(key);
        }
        writeTable(ast.table);
    }

    private void join(AST_Join ast) throws QueryException {
        checkWorkingDataBaseExist();
        // Init temp table
        DataTable tempTable = new DataTable();
        // Check if table exist
        DataBase curDB = this.DTree.getCurrentDB();
        if (!curDB.isTableExist(ast.leftTable) || !curDB.isTableExist(ast.rightTable)) {
            throw new QueryException.InterpretFailException(29);
        }
        DataTable leftTable = curDB.getTableByName(ast.leftTable);
        DataTable rightTable = curDB.getTableByName(ast.rightTable);
        // Adding header to tempTable
        combineHeaderFrom2Tables(tempTable, leftTable, rightTable, ast.leftAttribute, ast.rightAttribute);
        // Fill the temp table
        innerJoinSearch(tempTable, leftTable, rightTable, ast.leftAttribute, ast.rightAttribute);
        // Print it out!
        printTable(tempTable, tempTable.getHeader(), tempTable.getIdSet());
    }

    private void innerJoinSearch(DataTable des, DataTable t1, DataTable t2, String a1, String a2) throws QueryException {
        if (!t1.isAttributeExist(a1) || !t2.isAttributeExist(a2)) {
            throw new QueryException.InterpretFailException(23);
        }
        if (a1.equals("id") && a2.equals("id")) {
            sameIdJoin(des, t1, t2);
        } else if (a1.equals("id")) {
            leftIdJoin(des, t1, t2, a2);
        } else if (a2.equals("id")) {
            rightIdJoin(des, t1, t2, a1);
        } else {
            valueJoin(des, t1, t2, a1, a2);
        }
    }

    private void valueJoin(DataTable des, DataTable t1, DataTable t2, String attribute1, String attribute2) {
        SortedSet<Integer> leftIds = t1.getIdSet();
        SortedSet<Integer> rightIds = t2.getIdSet();
        for (int leftID : leftIds) {
            for (int rightID : rightIds) {
                if (t1.getDataCell(leftID, attribute1).equals(t2.getDataCell(rightID, attribute2))) {
                    ArrayList<Object> tempValue = new ArrayList<>();
                    ArrayList<Object> leftData = t1.getDataById(leftID);
                    ArrayList<Object> rightData = t2.getDataById(rightID);
                    leftData.remove(t1.getIndexOfAttribute(attribute1) - 1);
                    rightData.remove(t2.getIndexOfAttribute(attribute2) - 1);
                    tempValue.addAll(leftData);
                    tempValue.addAll(rightData);
                    des.addDataByRow(0, tempValue);
                }
            }
        }
    }

    private void rightIdJoin(DataTable des, DataTable t1, DataTable t2, String a1) throws QueryException {
        SortedSet<Integer> leftIds = t1.getIdSet();
        SortedSet<Integer> rightIds = t2.getIdSet();
        for (int leftID : leftIds) {
            if (!(t1.getDataCell(leftID, a1) instanceof Integer)) {
                throw new QueryException.InterpretFailException(26);
            }
        }
        for (int leftID : leftIds) {
            for (int rightID : rightIds) {
                if (rightID == (Integer) t1.getDataCell(leftID, a1)) {
                    ArrayList<Object> tempValue = new ArrayList<>();
                    ArrayList<Object> leftData = t1.getDataById(leftID);
                    leftData.remove(t1.getIndexOfAttribute(a1) - 1);
                    tempValue.addAll(leftData);
                    tempValue.addAll(t2.getDataById(rightID));
                    des.addDataByRow(0, tempValue);
                }
            }
        }
    }

    private void leftIdJoin(DataTable des, DataTable t1, DataTable t2, String a2) throws QueryException {
        SortedSet<Integer> leftIds = t1.getIdSet();
        SortedSet<Integer> rightIds = t2.getIdSet();
        for (int rightID : rightIds) {
            if (!(t2.getDataCell(rightID, a2) instanceof Integer)) {
                throw new QueryException.InterpretFailException(26);
            }
        }
        for (int leftID : leftIds) {
            for (int rightID : rightIds) {
                if (leftID == (Integer) t2.getDataCell(rightID, a2)) {
                    ArrayList<Object> tempValue = new ArrayList<>();
                    ArrayList<Object> rightData = t2.getDataById(rightID);
                    rightData.remove(t2.getIndexOfAttribute(a2) - 1);
                    tempValue.addAll(t1.getDataById(leftID));
                    tempValue.addAll(rightData);
                    des.addDataByRow(0, tempValue);
                }
            }
        }
    }

    private void sameIdJoin(DataTable des, DataTable t1, DataTable t2) {
        SortedSet<Integer> leftIds = t1.getIdSet();
        SortedSet<Integer> rightIds = t2.getIdSet();
        for (int leftID : leftIds) {
            for (int rightID : rightIds) {
                if (leftID == rightID) {
                    ArrayList<Object> tempValue = new ArrayList<>();
                    tempValue.addAll(t1.getDataById(leftID));
                    tempValue.addAll(t2.getDataById(rightID));
                    des.addDataByRow(0, tempValue);
                }
            }
        }
    }

    private void combineHeaderFrom2Tables(DataTable des, DataTable t1, DataTable t2, String a1, String a2) {
        ArrayList<String> header1 = t1.getHeader();
        header1.remove(0);
        for (String attribute : header1) {
            if (!attribute.equals(a1)) {
                des.addAttribute(attribute);
            }
        }
        ArrayList<String> header2 = t2.getHeader();
        header2.remove(0);
        for (String attribute : header2) {
            if (!attribute.equals(a2)) {
                des.addAttribute(attribute);
            }
        }
    }

    private void writeTable(String tableName) throws DBException {
        DBServerIO fileIO = new DBServerIO(this.DTree.getRootDirectory());
        fileIO.writeTableToFileSystem(this.DTree, this.DTree.getCurrentDataBaseName(), tableName);
    }

    private void checkAttributeExist(DataTable table, String attribute) throws QueryException {
        if (!table.isAttributeExist(attribute)) {
            throw new QueryException.InterpretFailException(23);
        }
    }

    private void checkTableExistOnCurDB(String tableName) throws QueryException {
        if (!this.DTree.getCurrentDB().isTableExist(tableName)) {
            throw new QueryException.InterpretFailException(31);
        }
    }

    private void checkWorkingDataBaseExist() throws QueryException {
        if (!this.DTree.isDataBaseExist(this.DTree.getCurrentDataBaseName())) {
            throw new QueryException.InterpretFailException(30);
        }
    }

    private void addALineToOutput(String line) {
        this.output = this.output + "\n" + line;
    }

    public String getOutput() {
        return this.output;
    }
}
