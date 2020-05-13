package com.cofitconsulting.cofit.utility;

public class StrutturaTassa {

    private String Tassa;
    private String Importo;
    private String Scadenza;

    private StrutturaTassa(){}


    public StrutturaTassa(String tassa, String importo, String scadenza) {
        Tassa = tassa;
        Importo = importo;
        Scadenza = scadenza;
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
}
