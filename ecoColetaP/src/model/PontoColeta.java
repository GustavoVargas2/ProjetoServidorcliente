package model;

import java.io.Serializable;

public class PontoColeta implements Serializable {
    private static final long serialVersionUID = 1L; 

    private int id; 
    private String nome;
    private String endereco;
    private String tipoResiduo;

    
    public PontoColeta(String nome, String endereco, String tipoResiduo) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipoResiduo = tipoResiduo;
    }

  
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTipoResiduo() { return tipoResiduo; }

    @Override
    public String toString() {
        return String.format("ID: %d | Nome: %s | Endereço: %s | Resíduo: %s", 
                             id, nome, endereco, tipoResiduo);
    }
}
