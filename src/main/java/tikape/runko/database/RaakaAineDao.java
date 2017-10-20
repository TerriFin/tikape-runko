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
import tikape.runko.domain.RaakaAine;

/**
 *
 * @author Pelle
 */
public class RaakaAineDao implements Dao<RaakaAine, Integer> {
    private Database database;
    
    public RaakaAineDao(Database database) {
        this.database = database;
    }
    
    /**
     *
     * @param key id
     * @return Jos löytää id:llä jonkin raakaaineen, palauttaa sen. Jos ei löydä,
     * niin palauttaa null.
     * @throws SQLException
     */
    @Override
    public RaakaAine findOne(Integer key) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RaakaAine WHERE RaakaAine.id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            RaakaAine raakaAine = new RaakaAine(key, rs.getString("nimi"));

            rs.close();
            stmt.close();
            conn.close();

            return raakaAine;
        } else {
            rs.close();
            stmt.close();
            conn.close();

            return null;
        }
    }

    @Override
    public List<RaakaAine> findAll() throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RaakaAine");

        ResultSet rs = stmt.executeQuery();
        List<RaakaAine> raakaAineet = new ArrayList<>();

        while (rs.next()) {
            RaakaAine raakaAine = new RaakaAine(rs.getInt("id"), rs.getString("nimi"));
            raakaAineet.add(raakaAine);
        }

        rs.close();
        stmt.close();
        conn.close();
        
        return raakaAineet;
    }
    
    /**
     * Muista laittaa myös AnnosRaakaAineDao poisto samalla id:llä jottei jää
     * reseptejä joilla on raaka-aineita joita ei ole enää olemassa.
     *(yritin yhdistää niitä, mutta silloin niiden initialisointi olisi mahdotonta)
     * @param key
     * @throws SQLException 
     */
    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM RaakaAine WHERE RaakaAine.id = ?");
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
    @Override
    public RaakaAine add(RaakaAine lisattava) throws SQLException {
        Connection conn = this.database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO RaakaAine(nimi) VALUES (?)");
        stmt.setString(1, lisattava.getNimi());
        
        stmt.executeUpdate();
        stmt.close();
        
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM RaakaAine WHERE RaakaAine.nimi = ?");
        statement.setString(1, lisattava.getNimi());
        
        ResultSet rs = statement.executeQuery();
        rs.next();
        RaakaAine palautettavaRaakaAine = new RaakaAine(rs.getInt("id"), rs.getString("nimi"));
        
        rs.close();
        statement.close();
        conn.close();
        
        return palautettavaRaakaAine;
    }
}
