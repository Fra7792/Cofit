package com.cofitconsulting.cofit.utility.model;

public class ModelTassa {

    private String Tassa;
    private Double Importo;
    private String Scadenza;
    private Boolean Pagato;
    private Boolean Permesso;

    private ModelTassa(){}


    public ModelTassa(String tassa, Double importo, String scadenza, Boolean pagato, Boolean permesso) {
        Tassa = tassa;
        Importo = importo;
        Scadenza = scadenza;
        Pagato = pagato;
        Permesso = permesso;
    }

    public String getTassa() {
        return Tassa;
    }

    public void setTassa(String tassa) {
        Tassa = tassa;
    }

    public Double getImporto() {
        return Importo;
    }

    public void setImporto(Double importo) {
        Importo = importo;
    }

    public String getScadenza() {
        return Scadenza;
    }

    public void setScadenza(String scadenza) {
        Scadenza = scadenza;
    }

    public Boolean getPagato() {
        return Pagato;
    }

    public void setPagato(Boolean pagato) {
        Pagato = pagato;
    }

    public Boolean getPermesso() {
        return Permesso;
    }

    public void setPermesso(Boolean permesso)
    {
        Permesso = permesso;
    }
}
