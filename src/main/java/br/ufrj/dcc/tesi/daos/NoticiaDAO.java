package br.ufrj.dcc.tesi.daos;

import java.util.List;

import twitter4j.JSONObject;
import br.ufrj.dcc.tesi.models.Noticia;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class NoticiaDAO {

	private static NoticiaDAO instance = null;

	public static NoticiaDAO getInstance(){
		if(instance == null) instance = new NoticiaDAO();
		return instance;
	}
	
	public void save(DBCollection collection, List<Noticia> noticias) {
		for(Noticia n : noticias){
			save(collection, n);
		}
	}
	public void save(DBCollection collection, Noticia n) {
		DBObject dbObject = (DBObject) JSON.parse(new JSONObject(n).toString());
		DBObject query = new BasicDBObject("url", n.getUrl());
		collection.update(query, dbObject, true, false);
		System.out.println("Noticia do " + n.getPortal() + " salva: " + n.getTitulo());
	}
	
	public void save(DBCollection collection, JsonObject n) {
		DBObject dbObject = (DBObject) JSON.parse(new JSONObject(n).toString());
		DBObject query = new BasicDBObject("url", n.get("url"));
		collection.update(query, dbObject, true, false);
		System.out.println("Noticia salva: " + n.get("titulo"));
	}
}
