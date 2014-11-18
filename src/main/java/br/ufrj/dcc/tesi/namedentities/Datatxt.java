package br.ufrj.dcc.tesi.namedentities;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import br.ufrj.dcc.tesi.utils.HTTPUtil;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Datatxt {
	
	private static final String ID = "0936d14f";
	private static final String KEY = "0057de6c2259788f64b622bef7e36d28";

	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException{
		PrintWriter writer = new PrintWriter("Noticias.txt", "UTF-8");
		
		DBCollection collection = MongoDBUtil.getInstance().getDatabase().getCollection(MongoDBUtil.COLLECTION);
		DBCursor cursor = collection.find();
		while(cursor.hasNext()){
			String url = "https://api.dandelion.eu/datatxt/nex/v1/?";
			DBObject n = cursor.next();
			String txt = n.get("titulo").toString();
			txt = URLEncoder.encode(txt,"UTF-8");
			StringBuilder sb = new StringBuilder(url).append("text=").append(txt).append("&include=image%2Cabstract%2Ctypes%2Ccategories%2Clod&country=-1");
			sb.append("&$app_id=").append(ID).append("&$app_key=").append(KEY);
			System.out.println("GET " + sb.toString());
			String jsonList;
			try {
				jsonList = HTTPUtil.doGET(sb.toString());
			} catch (Exception e1) {
				System.out.println("Deu problema no datatxt. Move on...");
				e1.printStackTrace();
				continue;
			}
			System.out.println(jsonList);
			JsonElement result = new JsonParser().parse(jsonList);
			result = result.getAsJsonObject().get("annotations");
			JsonArray entities;
			try {
				entities = result.getAsJsonArray();
			} catch (Exception e1) {
				System.out.println("Deu problema ao converter o json. Move on...");
				e1.printStackTrace();
				continue;
			}
			JsonArray namedEntitiesArray = new JsonArray();
			if(namedEntitiesArray.size()>0){
				writer.println(n.get("titulo"));
				writer.println(n.get("url"));
				writer.println(n.get("texto"));
				writer.println("Entidades:");
			}
			
			for(JsonElement e : entities){
//				String name = e.getAsJsonObject().get("title").getAsString();
//				String lod = e.getAsJsonObject().get("lod").getAsString();
//				String start = e.getAsJsonObject().get("start").getAsString();
//				String fim = e.getAsJsonObject().get("fim").getAsString();
				JsonObject obj = e.getAsJsonObject();
				obj.remove("uri");
				obj.remove("spot");
				obj.remove("confidence");
				obj.remove("id");
				obj.remove("abstract");
				obj.remove("title");
				obj.remove("types");
				namedEntitiesArray.add(obj);
				writer.println(obj.get("label"));
				writer.println("lod");
				writer.println("categories");
				writer.println("------------\n");
			}
			n.put("entidades", entities.toString());
            DBObject query = new BasicDBObject("url", n.get("url"));
            collection.update(query, n, true, false);
	        System.out.println("Noticia atualizada com entidades nomeadas");
		}
		writer.close();
	}
}
