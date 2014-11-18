package br.ufrj.dcc.tesi.crawlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import twitter4j.JSONObject;
import br.ufrj.dcc.tesi.Resources;
import br.ufrj.dcc.tesi.daos.NoticiaDAO;
import br.ufrj.dcc.tesi.enums.Portal;
import br.ufrj.dcc.tesi.models.Noticia;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;
import br.ufrj.dcc.tesi.utils.MySQLUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class VejaCrawler {
	
	//Noticias relacionadas as Eleicoes 2014, de 01/08/2014 a 31/11/2014
	private static String url = "http://veja.abril.com.br/busca/?qu=elei%C3%A7%C3%B5es+2014&origembusca=bsc&multimidia-meta_nav:Not%C3%ADcia&editoria-meta_nav:Brasil&date:[2014-08-01T00:00:00Z%20TO%202014-10-31T23:59:00Z]&dt=per";
							
	public static void main(String[] args) throws IOException, ParseException {
		
		System.out.println(Jsoup.connect(url).timeout(0).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").get().html());
		
		
		List<StringBuilder> sbList = new ArrayList<StringBuilder>();
        for(int i = 1; i <=22; i++){
        	sbList.add(getFile("Resultados_pag" + i + ".html"));
        }
        List<Noticia> noticias = getLinksResult(sbList);
        DBCollection collection = MongoDBUtil.getInstance().getDatabase().getCollection(MongoDBUtil.COLLECTION);
        
        saveNoticias(noticias, collection);
	}

	private static void saveNoticias(List<Noticia> noticias,
			DBCollection collection) throws IOException, ParseException {
		for(Noticia n : noticias){
	        Document doc = Jsoup.connect(n.getUrl()).timeout(0).get();
	        StringBuilder corpo = new StringBuilder();
	        for(Element e : doc.getElementsByTag("p")){
	        	corpo.append(e.text());
	        }
	        n.setTitulo(doc.select("h1.t-bigger-black").get(0).text());
	        n.setData(getDate(doc.getElementsByClass("data-hora").get(0).text()));
	        n.setTexto(corpo.toString());
            NoticiaDAO.getInstance().save(collection, n);
        }
	}

	private static Date getDate(String text) throws ParseException {
		
		String[] datahora = text.replaceAll(" ", "").split("-");
		String data = datahora[0];
		String[] dataVec = data.split("/");
		int dia = Integer.parseInt(dataVec[0]) - 1;
		int mes = Integer.parseInt(dataVec[1]) - 1;
		int ano = Integer.parseInt(dataVec[2]) - 1;
		String hora = datahora[1];
		String[] horaVec = hora.split(":");
		int h = Integer.parseInt(horaVec[0]);
		int m = Integer.parseInt(horaVec[1]);
		Calendar calendar = Calendar.getInstance();
		calendar.set(ano,mes,dia,h,m);
		Date d = calendar.getTime();
		return d;
	}

	private static List<Noticia> getLinksResult(List<StringBuilder> sbList) {
		List<Noticia> noticias = new ArrayList<Noticia>();
		for(StringBuilder sb : sbList){
			Document doc = Jsoup.parse(sbList.toString());
			Elements links = doc.getElementsByClass("bsc_resultado_titulo");
	        
	        for(Element e: links){
	        	Elements link = e.select("a[href]");
	        	Noticia n = new Noticia(link.get(0).absUrl("href"),Portal.VEJA);
	        	noticias.add(n);
	        }
		}
        return noticias;
	}

	private static StringBuilder getFile(String path) throws IOException {
		InputStream is = Resources.class.getResourceAsStream(path);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null){
            sb.append(inputLine);
        }
        in.close();
		return sb;
	}
		
}
