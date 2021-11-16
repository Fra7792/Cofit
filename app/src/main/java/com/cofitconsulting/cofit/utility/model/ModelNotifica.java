package com.cofitconsulting.cofit.utility.model;

public class ModelNotifica {

    private String Id;
    private String Email;
    private String Data;
    private Boolean Letta;

    private ModelNotifica(){}

    public ModelNotifica(String id, String email, String data, Boolean letta) {
        Id = id;
        Email = email;
        Data = data;
        Letta = letta;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public Boolean getLetta() {
        return Letta;
    }

    public void setLetta(Boolean letta) {
        Letta = letta;
    }
}
