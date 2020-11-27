package com.cofitconsulting.cofit.utility.model;

public class ModelNotifica {

    private String Id;
    private String Email;
    private String Data;
    private String Visto;

    private ModelNotifica(){}

    public ModelNotifica(String id, String email, String data, String visto) {
        Id = id;
        Email = email;
        Data = data;
        Visto = visto;
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

    public String getVisto() {
        return Visto;
    }

    public void setVisto(String visto) {
        Visto = visto;
    }
}
