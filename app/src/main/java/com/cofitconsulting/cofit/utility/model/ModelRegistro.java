package com.cofitconsulting.cofit.utility.model;

import java.io.Serializable;

public class ModelRegistro implements Serializable {

    private String tipo, descrizione, data, pagato;
    private  Double importo;
    private int id;

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getData() { return data; }

    public void setData(String data) { this.data = data; }

    public String getPagato() {
        return pagato;
    }

    public void setPagato(String pagato) {
        this.pagato = pagato;
    }
}