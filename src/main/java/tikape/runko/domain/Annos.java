/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

/**
 *
 * @author Pelle
 */

// Luokka joka kuvastaa annosta gettereillä ja settereillä.
public class Annos {
    private int id;
    private String nimi;
    
    public Annos(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getNimi() {
        return this.nimi;
    }
    
    public void setId(int uusiId) {
        this.id = uusiId;
    }
    
    public void setNimi(String uusiNimi) {
        this.nimi = uusiNimi;
    }
}
