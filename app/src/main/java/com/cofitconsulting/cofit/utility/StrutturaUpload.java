package com.cofitconsulting.cofit.utility;

import com.google.firebase.database.Exclude;

public class StrutturaUpload {

    private String FileName;
    private String FileUrl;
    private String Key;


    public StrutturaUpload(){

    }

    public StrutturaUpload(String fileName, String fileUrl) {
        if(fileName.trim().equals(""))
        {
            fileName = "No name";
        }

        FileName = fileName;
        FileUrl = fileUrl;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    @Exclude
    public String getKey() {
        return Key;
    }

    @Exclude
    public void setKey(String key) {
        Key = key;
    }
}
