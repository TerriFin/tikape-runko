package tikape.runko;

import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AnnosDao;
import tikape.runko.database.AnnosRaakaAineDao;
import tikape.runko.database.Database;
import tikape.runko.database.RaakaAineDao;
import tikape.runko.domain.Annos;
import tikape.runko.domain.*;

public class Main {
    
    /*
    Käytetyt tietokantataulut:
    
    CREATE TABLE Annos(
        id Integer PRIMARY KEY,
        nimi varchar(50));
    CREATE TABLE RaakaAine(
        id Integer PRIMARY KEY,
        nimi varchar(50));
    CREATE TABLE AnnosRaakaAine(
        raaka_aine_id Integer,
        annos_id Integer,
        jarjestys varchar(100),
        maara varchar(30),
        ohje varchar(200),
        FOREIGN KEY (raaka_aine_id)
        REFERENCES RaakaAine(id), FOREIGN KEY (annos_id) REFERENCES Annos(id));
    */

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:Smoothiet.db");
        
        AnnosDao annosDao = new AnnosDao(database);
        RaakaAineDao raakaAineDao = new RaakaAineDao(database);
        AnnosRaakaAineDao annosRaakaAineDao = new AnnosRaakaAineDao(database, annosDao, raakaAineDao);
    
        
        // Pääsivu
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        
        // Raaka-aineet sivu
        get("RaakaAineet/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("raakaAineet", raakaAineDao.findAll());

            return new ModelAndView(map, "RaakaAineet");
        }, new ThymeleafTemplateEngine());
        
            // Raaka-aineen poistaminen
            post("/poistaRaakaAine", (req, res) -> {
                int poistettavaRaakaAineId = Integer.parseInt(req.queryParams("poistettavaRaakaAine"));
                // Raaka-aine poistetaan ensin jokaisesta annoksesta jossa se on ainesosana.
                annosRaakaAineDao.poistaRaakaAineenMukaan(poistettavaRaakaAineId);
                // Sitten itse raaka-aine voidaan poistaa kun siihen ei enää ole viitteitä toisesta tietokantataulusta.
                raakaAineDao.delete(poistettavaRaakaAineId);

                res.redirect("RaakaAineet/");
                return "";
            });

            // Raaka-aineen lisääminen
            post("/lisaaRaakaAine", (req, res) -> {
                raakaAineDao.add(new RaakaAine(-1, req.queryParams("lisattavaRaakaAine")));

                res.redirect("RaakaAineet/");
                return "";
            });
        
            
        // Annokset sivu 
        get("Annokset/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("annokset", annosDao.findAll());
            map.put("raakaAineet", raakaAineDao.findAll());

            return new ModelAndView(map, "Annokset");
        }, new ThymeleafTemplateEngine());
        
            // Annosken poistaminen
            post("/poistaAnnos", (req, res) -> {
                int poistettavaAnnosId = Integer.parseInt(req.queryParams("poistettavaAnnos"));
                // Poistetaan ensin tiedot mitä raaka-aineita tähän annokseen kuuluu.
                annosRaakaAineDao.poistaAnnoksenMukaan(poistettavaAnnosId);
                // Sitten itse annos voidaan poistaa kun siihen ei enää ole viitteitä toisesta tietokantataulusta.
                annosDao.delete(poistettavaAnnosId);

                res.redirect("Annokset/");
                return "";
            });
        
            // Uuden annoksen lisäys
            post("/lisaaAnnos", (req, res) -> {
                annosDao.add(new Annos(-1, req.queryParams("lisattavaAnnos")));

                res.redirect("Annokset/");
                return "";
            });
            
            // Raaka-aineen lisäys annokseen
            post("/muokkaaAnnos", (req, res) -> {
                annosRaakaAineDao.lisaaAnnosRaakaAine(
                        Integer.parseInt(req.queryParams("annokseenLisattavaRaakaAine")),
                        Integer.parseInt(req.queryParams("muokattavaAnnos")),
                        req.queryParams("jarjestys"),
                        req.queryParams("maara"),
                        req.queryParams("ohje"));
                                                        
                res.redirect("Annokset/");
                return "";
            });
        
        
        // Annoksen sivu
        get("Annokset/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            
            Integer annosId = Integer.parseInt(req.params(":id"));
            
            // Annoksen nimi.
            map.put("annos", annosDao.findOne(annosId));
            
            // Annoksen raaka-aineet ja oheistiedot(eli siis AnnosRaakaAineet) listassa järjestettynä.
            List<AnnosRaakaAine> annosRaakaAineet = annosRaakaAineDao.haeAnnoksenAnnosRaakaAineet(annosId);
            annosRaakaAineet.sort((annosRaakaAine1, annosRaakaAine2) -> {
                return annosRaakaAine1.getJarjestys().compareTo(annosRaakaAine2.getJarjestys());
            });
            map.put("annosRaakaAineet", annosRaakaAineet);

            return new ModelAndView(map, "Annos");
        }, new ThymeleafTemplateEngine());

    }
}
