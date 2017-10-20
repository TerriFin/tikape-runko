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
import java.util.List;
import tikape.runko.domain.Annos;

/**
 *
 * @author Pelle
 */
public class AnnosDao implements Dao<Annos, Integer> {
    private Database database;

    public AnnosDao(Database database) {
        this.database = database;
    }

    /**
     *
     * @param key id
     * @return Jos löytää id:llä jonkin annoksen, palauttaa sen. Jos ei löydä,
     * niin palauttaa null.
     * @throws SQLException
     */
    @Override
    public Annos findOne(Integer key) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Annos WHERE Annos.id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Annos annos = new Annos(key, rs.getString("nimi"));

            rs.close();
            stmt.close();
            conn.close();

            return annos;
        } else {
            rs.close();
            stmt.close();
            conn.close();

            return null;
        }
    }

    @Override
    public List<Annos> findAll() throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Annos");

        ResultSet rs = stmt.executeQuery();
        List<Annos> annokset = new ArrayList<>();

        while (rs.next()) {
            Annos annos = new Annos(rs.getInt("id"), rs.getString("nimi"));
            annokset.add(annos);
        }

        rs.close();
        stmt.close();
        conn.close();
        
        return annokset;
    }

    /**
     * Muista laittaa myös AnnosRaakaAineDao poisto samalla id:llä jottei jää
     * reseptejä joilla on annoksia joita ei ole enää olemassa.
     *(yritin yhdistää niitä, mutta silloin niiden initialisointi olisi mahdotonta)
     * @param key
     * @throws SQLException 
     */
    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Annos WHERE Annos.id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();
        
        stmt.close();
        conn.close();
    }

    /**
     * Voit laittaa id arvoksi minkä vain, tämä metodi aina laittaa uuden id:n tilalle.
     * @param lisattava
     * @return
     * @throws SQLException 
     */
    public Annos add(Annos lisattava) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Annos(nimi) VALUES (?)");
        stmt.setString(1, lisattava.getNimi());
        
        stmt.executeUpdate();
        stmt.close();
        
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM Annos WHERE Annos.nimi = ?");
        statement.setString(1, lisattava.getNimi());
        
        ResultSet rs = statement.executeQuery();
        rs.next();
        Annos palautettavaAnnos = new Annos(rs.getInt("id"), rs.getString("nimi"));
        
        rs.close();
        statement.close();
        conn.close();
        
        return palautettavaAnnos;
    }

}
