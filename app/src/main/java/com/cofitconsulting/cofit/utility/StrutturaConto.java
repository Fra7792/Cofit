package com.cofitconsulting.cofit.utility;

import java.io.Serializable;

public class StrutturaConto implements Serializable {

    private String tipo, descrizione, importo, data;
    private int id;

    public String getImporto() {
        return importo;
    }

    public void setImporto(String importo) {
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
}