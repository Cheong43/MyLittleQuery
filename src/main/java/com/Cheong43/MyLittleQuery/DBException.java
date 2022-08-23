package com.Cheong43.MyLittleQuery;

import java.io.File;

/* If this part of error is waked, It means the server suffering fatal error
 * So must kill the program.
 */


public class DBException extends Exception {

    private String errorLog;

    public DBException() {
    }

    public static class FileNotFoundException extends DBException {
        public FileNotFoundException(File fileDirectory) {
            addToErrorLog("File is not found: " + fileDirectory.getAbsolutePath());
        }
    }

    public static class DirectoryInvalidException extends DBException {
        public DirectoryInvalidException(File Directory) {
            addToErrorLog("Directory is not found: " + Directory.getAbsolutePath());
        }
    }

    public static class filePointerIsNullException extends DBException {
        public filePointerIsNullException(File filename) {
            addToErrorLog("File Pointer cannot lead to accessible file: " + filename.getAbsolutePath());
        }
    }

    void addToErrorLog(String message){
        this.errorLog = message;
    }

    public String getErrorLog(){
        return this.errorLog;
    }

}
