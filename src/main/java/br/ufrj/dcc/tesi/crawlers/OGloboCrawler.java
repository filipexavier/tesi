package br.ufrj.dcc.tesi.crawlers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.ufrj.dcc.tesi.enums.Portal;
import br.ufrj.dcc.tesi.models.Noticia;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;

import com.mongodb.DBCollection;

public class OGloboCrawler {

	static int PAGE_NUMBER = 1;
	static int MAX_PAGE_NUMBER = 100;
	
	static final String SITE_BASE = "http://oglobo.globo.com/";
	
	public static void main(String[] args) throws IOException {
		
		DBCollection collection = MongoDBUtil.getInstance().getDatabase().getCollection(MongoDBUtil.COLLECTION);
		while (PAGE_NUMBER <= MAX_PAGE_NUMBER) {
			getWeblink(PAGE_NUMBER, collection);
			PAGE_NUMBER++;
		}        
	}

	public static void getWeblink(int page_number, DBCollection collection) throws IOException {
		
		//Busca sobre Eleicoes 2014
		String url1 = SITE_BASE + "busca/?q=elei%C3%A7%C3%B5es+2014&page=" + PAGE_NUMBER + "&_=1415670163800&species=not%C3%ADcias";
		Document doc1 = Jsoup.connect(url1).timeout(0).get();
        Elements links1 = doc1.select("a[href]");
        
        //Busca sobre Economia Brasileira
        String url2 = SITE_BASE + "busca/?q=economia+brasil&_=1415670163800&species=not%C3%ADcias&page=" + PAGE_NUMBER;
		Document doc2 = Jsoup.connect(url2).timeout(0).get();
        Elements links2 = doc2.select("a[href]");
        
        for (Element e: links1) {
        	if (e.attr("abs:href").contains(SITE_BASE + "opiniao") ||
        			e.attr("abs:href").contains(SITE_BASE + "brasil") ||
        			e.attr("abs:href").contains(SITE_BASE + "rio") ||
        			e.attr("abs:href").contains(SITE_BASE + "economia")) {
        		if (e.hasClass("cor-produto") && e.hasAttr("title")) {
        			System.out.println(e.attr("abs:href"));
        			processPage(e.attr("abs:href"), collection);
        		} else if (e.hasClass("logo-topo")) {
        			continue;	//ignora o link para a homepage
        		}
        	}
        }
        
        for (Element e: links2) {
        	if (e.attr("abs:href").contains(SITE_BASE + "economia")) {
        		if (e.hasClass("cor-produto") && e.hasAttr("title")) {
        			System.out.println(e.attr("abs:href"));
        			processPage(e.attr("abs:href"), collection);
        		} else if (e.hasClass("logo-topo")) {
        			continue;	//ignora o link para a homepage
        		}
        	}
        }
	}
	
	public static void processPage(String url, DBCollection collection) {
		
		Elements page = null;
		Document doc = null;
		
		String titulo = null;
		String subtitulo = null;
		String autor = null;
		Date dataPublicacao = null;
		Date dataAtualizacao = null;
		Elements corpoNoticia = null;
		StringBuilder sb = new StringBuilder("");
		
		try {
			doc = Jsoup.connect(url).timeout(0).get();
			page = doc.select("article");
		} catch (Exception e) {
			e.printStackTrace();
		}		
				
		Element e;
		try {
			e = page.get(0);
			
			titulo = e.getElementsByAttributeValue("itemprop", "headline").text();
			subtitulo = e.getElementsByAttributeValue("itemprop", "description").text();
			autor =  e.getElementsByAttributeValue("itemprop", "author").text();
			
			dataPublicacao = getDate(e.getElementsByClass("data-cadastro").text());
			String dataAttStr = e.getElementsByClass("data-atualizacao").text();
			if (!dataAttStr.isEmpty()) {
				dataAtualizacao = getDate(dataAttStr.substring(11));
			}
			
			corpoNoticia = e.select("div[itemprop=articleBody]");
			for (Element p: corpoNoticia) {				
				sb.append(p.getElementsByTag("p").text() + "\n");
			}
			
	        Noticia n = new Noticia();
	        n.setPortal(Portal.OGLOBO);
	        n.setUrl(url);
	        n.setTitulo(titulo);
	        n.setSubTitulo(subtitulo);
	        n.setAutor(autor);
	        n.setData(dataPublicacao);
	        n.setDataAtualizacao(dataAtualizacao);
	        n.setTexto(sb.toString());    
	        
	        //NoticiaDAO.getInstance().save(collection, n);
	        
	        System.out.println("Titulo: " + n.getTitulo());
	        System.out.println("Subtitulo: " + n.getSubTitulo());
	        System.out.println("Corpo: " + n.getTexto());
	        System.out.println("Data: " + n.getData());
	        System.out.println("Data att: " + n.getDataAtualizacao());
	        System.out.println("--------------------------");
	        
		} catch (Exception e1) {
			System.out.println("Padrao de artigo nao reconhecido. Tentaremos outro.");
			newProcessPage(url, collection);
			e1.printStackTrace();
		}		        
	}

	//Processa outro padrao de artigo
	public static void newProcessPage(String url, DBCollection collection) {

		Elements page = null;
		Document doc = null;
		
		String titulo = null;
		Date dataPublicacao = null;
		Elements corpoNoticia = null;
		StringBuilder sb = new StringBuilder("");
		
		try {
			doc = Jsoup.connect(url).timeout(0).get();
			page = doc.select(".texteira");
		} catch (Exception e) {
			e.printStackTrace();
		}		
				
		Element e;
		try {
			e = page.get(0);
			
			titulo = e.getElementsByAttributeValue("itemprop", "headline").text();
			
			Elements els = e.getElementsByClass("data-cadastro");
			String dataPublicacaoStr = els.toString();
			String dia = dataPublicacaoStr.substring(52, 54);
			String mes = dataPublicacaoStr.substring(49, 51);;
			String ano = dataPublicacaoStr.substring(44, 48);;
			String horario = els.text();
			horario = horario.replace('h', ':');
			dataPublicacao = getDate(dia + "/" + mes + "/" + ano + " " + horario);

			corpoNoticia = e.select("div[itemprop=articleBody]");
			for (Element p: corpoNoticia) {				
				sb.append(p.getElementsByTag("p").text() + "\n");
			}
			
	        Noticia n = new Noticia();
	        n.setPortal(Portal.OGLOBO);
	        n.setUrl(url);
	        n.setTitulo(titulo);
	        n.setData(dataPublicacao);
	        n.setTexto(sb.toString());    
	        
	        //NoticiaDAO.getInstance().save(collection, n);
	        
	        System.out.println("Titulo: " + n.getTitulo());
	        System.out.println("Corpo: " + n.getTexto());
	        System.out.println("Data: " + n.getData());
	        System.out.println("--------------------------");
	        
		} catch (Exception e1) {
			System.out.println("Nao foi possivel processar o padrao de artigo");
			e1.printStackTrace();
		}
	}
	
	private static Date getDate(String text) {
		
		String[] datahora = text.split(" ");
		String data = datahora[0];
		String[] dataVec = data.split("/");
		
		int dia = Integer.parseInt(dataVec[0]);
		int mes = Integer.parseInt(dataVec[1]) - 1;
		int ano = Integer.parseInt(dataVec[2]);
		
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
		calendar.set(ano, mes, dia, h, m);
		Date d = calendar.getTime();
		
		return d;
	}
}
