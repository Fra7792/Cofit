package com.cofitconsulting.cofit.utility;

public class User {

    private String Id;
    private String Denominazione;
    private String Email;

    private User(){}

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getDenominazione() {
        return Denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.Denominazione = denominazione;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public User(String id, String denominazione, String email) {
        this.Id = id;
        this.Denominazione = denominazione;
        this.Email = email;
    }
}



