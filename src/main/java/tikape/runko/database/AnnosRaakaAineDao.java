/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.List;
import tikape.runko.domain.AnnosRaakaAine;

/**
 *
 * @author Pelle
 */
public class AnnosRaakaAineDao {
    private Database database;
    private AnnosDao annosDao;
    private RaakaAineDao raakaAineDao;
    
    public AnnosRaakaAineDao(Database database, AnnosDao annosDao, RaakaAineDao raakaAineDao) {
        this.database = database;
        this.annosDao = annosDao;
        this.raakaAineDao = raakaAineDao;
    }
    
    /**
     * Nimensä mukaisesti kun lisätään raakaAineita, ja valitaan smoothie ja laitetaan kaikki tiedot, tämä lisää ne.
     * @param raakaAineId
     * @param annosId
     * @param jarjestys
     * @param maara
     * @param ohje
     * @throws SQLException 
     */
    public void lisaaAnnosRaakaAine(int raakaAineId, int annosId, String jarjestys, String maara, String ohje) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO AnnosRaakaAine(raaka_aine_id, annos_id, jarjestys, maara, ohje) VALUES (?, ?, ?, ?, ?)");
        stmt.setInt(1, raakaAineId);
        stmt.setInt(2, annosId);
        stmt.setString(3, jarjestys);
        stmt.setString(4, maara);
        stmt.setString(5, ohje);
        
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }
    
    /**
     * 
     * @param id Annoksen id
     * @return Palauttaa kaikki annokseen liittyvät AnnosRaakaAine objektit.
     * @throws SQLException 
     */
    public List<AnnosRaakaAine> haeAnnoksenAnnosRaakaAineet(int id) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AnnosRaakaAine WHERE AnnosRaakaAine.annos_id = ?");
        stmt.setInt(1, id);
        
        ResultSet rs = stmt.executeQuery();
        List<AnnosRaakaAine> annoksenRaakaAineet = new ArrayList<>();
        
        while(rs.next()) {
            annoksenRaakaAineet.add(new AnnosRaakaAine(this.annosDao.findOne(rs.getInt("annos_id")), this.raakaAineDao.findOne(rs.getInt("raaka_aine_id")), rs.getString("jarjestys"), rs.getString("maara"), rs.getString("ohje")));
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return annoksenRaakaAineet;
    }
    
    /**
     * Laita tämä aina jos poistetaan annos, jottei jää smoothieita, joilla on annoksia joita ei ole enää olemassa!
     * @param annosId Annoksen id
     * @throws SQLException 
     */
    public void poistaAnnoksenMukaan(int annosId) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AnnosRaakaAine WHERE AnnosRaakaAine.annos_id = ?");
        stmt.setInt(1, annosId);
        
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }
    
    /**
     * Laita tämä aina jos poistetaan raakaAine, jottei jää smoothieita, joilla on raakaAineita joita ei ole enää olemassa!
     * @param raakaAineId RaakaAineen Id
     * @throws SQLException 
     */
    public void poistaRaakaAineenMukaan(int raakaAineId) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AnnosRaakaAine WHERE AnnosRaakaAine.raaka_aine_id = ?");
        stmt.setInt(1, raakaAineId);
        
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }
}
