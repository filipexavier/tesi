package br.ufrj.dcc.tesi.daos;

import java.util.List;

import twitter4j.JSONObject;
import br.ufrj.dcc.tesi.models.Noticia;
import br.ufrj.dcc.tesi.utils.DateUtil;

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
	
	public Integer save(DBCollection collection, List<Noticia> noticias, Integer count) {
		for(Noticia n : noticias){
			String msg = "Noticia " + count + " de " + DateUtil.getPrettyDate(n.getData()) + " do " + n.getPortal() + " salva: " + n.getTitulo();
			save(collection, n, msg);
			count++;
		}
		return count;
	}
	public void save(DBCollection collection, List<Noticia> noticias) {
		for(Noticia n : noticias){
			save(collection, n);
		}
	}
	public void save(DBCollection collection, Noticia n) {
		save(collection,n,null);
	}
	public void save(DBCollection collection, Noticia n, String msg) {
		DBObject dbObject = (DBObject) JSON.parse(new JSONObject(n).toString());
		DBObject query = new BasicDBObject("url", n.getUrl());
		collection.update(query, dbObject, true, false);
		if(msg == null) {
			msg = "Noticia do " + n.getPortal() + " salva: " + n.getTitulo();
		}
		System.out.println(msg);
	}

}
