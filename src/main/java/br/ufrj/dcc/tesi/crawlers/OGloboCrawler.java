package br.ufrj.dcc.tesi.crawlers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import twitter4j.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import br.ufrj.dcc.tesi.daos.NoticiaDAO;
import br.ufrj.dcc.tesi.enums.Portal;
import br.ufrj.dcc.tesi.models.Noticia;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;

public class OGloboCrawler {

	static int PAGE_NUMBER = 1;
	static int MAX_PAGE_NUMBER = 50;
	static int count = 0;
	
	public static void main(String[] args) throws IOException {
		
		DBCollection collection = MongoDBUtil.getInstance().getDatabase().getCollection(MongoDBUtil.COLLECTION);
		while (PAGE_NUMBER <= MAX_PAGE_NUMBER) {
			getWeblink(PAGE_NUMBER,collection);
			PAGE_NUMBER++;
		}        
	}

	public static void getWeblink(int page_number, DBCollection collection) throws IOException {
		
		String url = "http://oglobo.globo.com/busca/?q=elei%C3%A7%C3%B5es+2014&page=" + page_number + "&_=1415670163800&species=not%C3%ADcias";
		Document doc = Jsoup.connect(url).timeout(0).get();
        Elements links = doc.select("a[href]");
        
        for (Element e: links) {
        	if (e.attr("abs:href").contains("http://oglobo.globo.com/brasil/segundo-turno-em-debate.html")){
        		continue;
        	} else
        	if (e.attr("abs:href").contains("http://oglobo.globo.com/")) {
        		if (e.hasClass("cor-produto") && e.hasAttr("title")) {
        			System.out.println(e.attr("abs:href"));
        			processPage(e.attr("abs:href"),collection);
        		} else if (e.hasClass("logo-topo")) {
        			continue;
        		}
        	}
        }
	}
	
	public static void processPage(String url, DBCollection collection) {
		
		Elements page = null;
		Document doc = null;
		
		try {
			doc = Jsoup.connect(url).timeout(0).get();
			page = doc.select("article");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
			String title = null;
			String description = null;
			String author = null;
			Date pubDate = null;
			Date attDate = null;
			Elements articleBody = null;
			StringBuilder text = new StringBuilder("");
			
			Element e;
			try {
				e = page.get(0);
			} catch (Exception e1) {
				System.out.println("Noticia fora do padrao");
				e1.printStackTrace();
				return;
			}
			title = e.getElementsByAttributeValue("itemprop", "headline").text();
			description = e.getElementsByAttributeValue("itemprop", "description").text();
			author =  e.getElementsByAttributeValue("itemprop", "author").text();
			pubDate = getDate(e.getElementsByClass("data-cadastro").text());
			String attDateStr = e.getElementsByClass("data-atualizacao").text();
			if (!attDateStr.isEmpty()) {
				attDate = getDate(attDateStr.substring(11));
			}
			articleBody = e.select("div[itemprop=articleBody]");
			for (Element p: articleBody) {
				
				text.append(p.getElementsByTag("p").text() + "\n");
			}
			
	        Noticia n = new Noticia();
	        n.setAutor(author);
	        n.setData(pubDate);
	        n.setDataAtualizacao(attDate);
	        n.setPortal(Portal.OGLOBO);
	        n.setSubTitulo(description);
	        n.setTitulo(title);
	        n.setTexto(text.toString());
	        n.setUrl(url);
	        
	        //NoticiaDAO.getInstance().save(collection, n);
            count++;
        
	}

	

	private static Date getDate(String text) {
		
		String[] datahora = text.split(" ");
		String data = datahora[0];
		String[] dataVec = data.split("/");
		int dia = Integer.parseInt(dataVec[0]) - 1;
		int mes = Integer.parseInt(dataVec[1]) - 1;
		int ano = Integer.parseInt(dataVec[2]) - 1;
		String hora = datahora[1];
		String[] horaVec = hora.split(":");
		int h;
		int m;
		try {
			h = Integer.parseInt(horaVec[0]);
			m = Integer.parseInt(horaVec[1]);
		} catch (NumberFormatException e) {
			System.out.println("Problema ao fazer parse da data");
			e.printStackTrace();
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.set(ano,mes,dia,h,m);
		Date d = calendar.getTime();
		return d;
	}
}
