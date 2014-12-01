package br.ufrj.dcc.tesi.namedentities;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import br.ufrj.dcc.tesi.daos.NoticiaDAO;
import br.ufrj.dcc.tesi.models.Noticia;
import br.ufrj.dcc.tesi.utils.HTTPUtil;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class Datatxt {
	
	private static final String ID = "822a9e86";
	private static final String KEY = "bfcc413d10910ba6c3542356b9478337";
	public static final String URL = "https://api.dandelion.eu/datatxt/nex/v1/?";
	public static final String APPEND_CHARAC = "&";
	public static final String ATTRIBUTES = "include=image%2Ccategories%2Clod&country=-1";
	public static final int MAX_CHARAC_HTML_GET = 3000;

	public static void main(String[] args){
		System.out.println("Iniciando...");
		populaNoticiasDatatxt();
	}

	public static void populaNoticiasDatatxt(){
		System.out.println("Tentando conexao com o Mongolab...");
		DBCollection collection = MongoDBUtil.getInstance().getDatabase().getCollection(MongoDBUtil.COLLECTION);
		System.out.println("Conectado ao banco!");
		DBObject query = new BasicDBObject("entidades", new BasicDBObject("$exists", false));
		System.out.println("Lista de noticias recuperada do banco.");
		DBCursor cursor = collection.find(query);
		try {
			saveNamedEntities(cursor,collection);
		} catch (UnsupportedEncodingException e) {
			System.out.println("Problema ao montar a url");
			e.printStackTrace();
		}
	}
	
	public static void saveNamedEntities(DBCursor cursor, DBCollection collection) throws UnsupportedEncodingException{
		while(cursor.hasNext()){
			DBObject n = cursor.next();
			if(n.get("texto") == null) continue;
			String txt = n.get("texto").toString();
			List<String> entitiesList = runDatatxtRequest(txt);
			if(entitiesList == null) continue;
			
			System.out.println(entitiesList);
			List<DBObject> entities;
			try {
				entities = getEntitiesFromJson(entitiesList);
			} catch (Exception e) {
				System.out.println("Problema ao converter Json");
				e.printStackTrace();
				continue;
			}
			Noticia noticia = new Noticia(n);
			noticia.setEntidades(entities);
            NoticiaDAO.getInstance().save(collection, noticia);
	        System.out.println("Noticia atualizada com entidades nomeadas");
		}
	}
	
	private static List<DBObject> getEntitiesFromJson(List<String> noticiaEntitiesList) {
		//Itera sobre strings com o resultado do dataTxt
		List<DBObject> namedEntitiesArray = new ArrayList<DBObject>();
		List<String> entidadesNoArray = new ArrayList<String>();
		for(String strEntities : noticiaEntitiesList){
			DBObject annotations = (DBObject) JSON.parse(strEntities);
			BasicDBList requestEntitiesList = (BasicDBList) annotations.get("annotations");
			//writeHeaderToFile(writer,n,namedEntitiesArray);
			
			//Itera sobre Lista de entidades deste pedaco/requisicao, e adiciona na lista de todos desta noticia
			for(Object obj : requestEntitiesList){
				
				DBObject entityObj = (DBObject) JSON.parse(obj.toString());
				String labelEntidade = entityObj.get("label").toString();
				if(!entidadesNoArray.contains(labelEntidade)){
					removeIrrelevantData(entityObj);
					namedEntitiesArray.add(entityObj);
					entidadesNoArray.add(labelEntidade);
					//writeDataToFile(writer, obj);
				}
			}
		}

		return namedEntitiesArray;
	}
	
	private static List<String> runDatatxtRequest(String txt){
		try {
			txt = URLEncoder.encode(txt,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Problema no encoding ao converter texto para urlCompatible");
			e.printStackTrace();
		}
		
		List<String> entitiesList = new ArrayList<String>();
		if(txt.length()>MAX_CHARAC_HTML_GET){
			List<String> subLists = divideTexto(txt, MAX_CHARAC_HTML_GET);
			System.out.println("texto " + txt );
			System.out.println(subLists);
			for(String s : subLists){
				String entities = getNamedEntities(s);
				entitiesList.add(entities);
			}
		}else{
			String entities = getNamedEntities(txt);
			entitiesList.add(entities);
		}
		return entitiesList;
	}
	
	private static String getNamedEntities(String txt) {
		StringBuilder sb = new StringBuilder(URL).append("text=").append(txt).
				append(APPEND_CHARAC).append(ATTRIBUTES).
				append(APPEND_CHARAC).append("$app_id=").append(ID).
				append(APPEND_CHARAC).append("$app_key=").append(KEY);
		System.out.println("GET ("+ sb.toString().length() + ") - " + sb.toString());
		String jsonList;
		try {
			jsonList = HTTPUtil.doGET(sb.toString());
		} catch (Exception e1) {
			System.out.println("Deu problema na requisicao do datatxt");
			e1.printStackTrace();
			return null;
		}
		return jsonList;
	}
	
	private static List<String> divideTexto(String txt, int maxCharacHtmlGet) {
		List<String> subLists = new ArrayList<String>();
		while(txt.length()>maxCharacHtmlGet){
			System.out.println("Dividindo o corpo da noticia...");
			for(int i = maxCharacHtmlGet; i>=0; i--){
				if(String.valueOf(txt.charAt(i)).equals(".")){
					subLists.add(txt.subSequence(0, i).toString());
					if(String.valueOf(txt.charAt(i+1)).equals("+")){
						txt = txt.substring(i+2);
					} else{
						txt = txt.substring(i+1);
					}
					break;
				}
				if(i == 0){
					subLists.add(txt.subSequence(0, maxCharacHtmlGet).toString());
					txt = txt.substring(maxCharacHtmlGet+1);
				}
			}
		}
		//Add ultima parte, que pode ter mais que o maximo
		subLists.add(txt);
		
		return subLists;
	}
	private static void removeIrrelevantData(DBObject obj) {
		obj.removeField("uri");
		obj.removeField("spot");
		obj.removeField("confidence");
		obj.removeField("id");
		obj.removeField("title");
	}
	private static void writeDataToFile(PrintWriter writer, JsonObject obj) {
		writer.println(obj.get("label"));
		writer.println("lod");
		writer.println("categories");
		writer.println("------------\n");
	}
	private static void writeHeaderToFile(PrintWriter writer, DBObject n, JsonArray namedEntitiesArray) {
		if(namedEntitiesArray.size()>0){
			writer.println(n.get("titulo"));
			writer.println(n.get("url"));
			writer.println(n.get("texto"));
			writer.println("Entidades:");
		}
	}
}
