package com.cofitconsulting.cofit.utility.model;

public class ModelTassa {

    private String Tassa;
    private String Importo;
    private String Scadenza;
    private String Pagato;

    private ModelTassa(){}


    public ModelTassa(String tassa, String importo, String scadenza, String pagato) {
        Tassa = tassa;
        Importo = importo;
        Scadenza = scadenza;
        Pagato = pagato;
    }

    public String getTassa() {
        return Tassa;
    }

    public void setTassa(String tassa) {
        Tassa = tassa;
    }

    public String getImporto() {
        return Importo;
    }

    public void setImporto(String importo) {
        Importo = importo;
    }

    public String getScadenza() {
        return Scadenza;
    }

    public void setScadenza(String scadenza) {
        Scadenza = scadenza;
    }

    public String getPagato() {
        return Pagato;
    }

    public void setPagato(String pagato) {
        Pagato = pagato;
    }
}
