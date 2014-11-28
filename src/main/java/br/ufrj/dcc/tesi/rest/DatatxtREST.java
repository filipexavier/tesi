package br.ufrj.dcc.tesi.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import br.ufrj.dcc.tesi.models.Noticia;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

@Path("/noticias")
public class DatatxtREST extends AbstractREST {
	
//	@GET
//	@Produces(JSON_UTF8)
//	public String findNoticias() {
//		DBCollection collection = DBUtil.getInstance().getDatabase().getCollection(DBUtil.COLLECTION_NOTICIAS);
//		DBCursor cursor = collection.find();
//		cursor.sort(new BasicDBObject(Noticia.FIELD_TIMESTAMP, -1));
//		return serialize(cursor);
//	}
//	
//	@GET
//	@Path("{desde}")
//	@Produces(JSON_UTF8)
//	public String findNoticias(@PathParam("desde") Long desde) {
//		DBCollection collection = DBUtil.getInstance().getDatabase().getCollection(DBUtil.COLLECTION_NOTICIAS);
//		BasicDBObject query = new BasicDBObject();
//		query.put(Noticia.FIELD_TIMESTAMP, new BasicDBObject("$gt", desde));
//		DBCursor cursor = collection.find(query);
//		cursor.sort(new BasicDBObject(Noticia.FIELD_TIMESTAMP, -1));
//		return serialize(cursor);
//	}
}
