package br.ufrj.dcc.tesi.namedentities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import br.ufrj.dcc.tesi.utils.HTTPUtil;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class Datatxt {
	
	private static final String ID = "0936d14f";
	private static final String KEY = "0057de6c2259788f64b622bef7e36d28";

	public static void main(String[] args) throws UnsupportedEncodingException{
		DBCollection collection = MongoDBUtil.getInstance().getDatabase().getCollection(MongoDBUtil.COLLECTION);
		DBCursor cursor = collection.find();
		while(cursor.hasNext()){
			String url = "https://api.dandelion.eu/datatxt/nex/v1/?";
			String txt = cursor.next().get("titulo").toString();
			txt = URLEncoder.encode(txt,"UTF-8");
			StringBuilder sb = new StringBuilder(url).append("text=").append(txt).append("&include=image%2Cabstract%2Ctypes%2Ccategories%2Clod&country=-1");
			sb.append("&$app_id=").append(ID).append("&$app_key=").append(KEY);
			System.out.println("GET " + sb.toString());
			String jsonList = HTTPUtil.doGET(sb.toString());
			System.out.println(jsonList);
			JsonElement result = new JsonParser().parse(jsonList);
			result = result.getAsJsonObject().get("annotations");
			JsonArray entities = result.getAsJsonArray();
			
			for(JsonElement e : entities){
				String name = e.getAsJsonObject().get("title").getAsString();
				String lod = e.getAsJsonObject().get("lod").getAsString();
				String start = e.getAsJsonObject().get("start").getAsString();
				String fim = e.getAsJsonObject().get("fim").getAsString();
				//JsonObject obj = new org.json.JsonObject().;
				
			}
			break;
		}
	}
}
