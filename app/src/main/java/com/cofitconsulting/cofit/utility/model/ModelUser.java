package com.cofitconsulting.cofit.utility.model;

public class ModelUser {

    private String Id;
    private String Denominazione;
    private String Cognome;
    private String Email;

    private ModelUser(String cognome){
        this.Cognome = cognome;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getDenominazione() {
        return Denominazione;
    }

    public  String getCognome()
    {
        return Cognome;
    }

    public void setCognome(String cognome) {
        this.Cognome = cognome;
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

    public ModelUser(String id, String denominazione, String cognome, String email) {
        this.Id = id;
        this.Denominazione = denominazione;
        this.Cognome = cognome;
        this.Email = email;
    }
}



