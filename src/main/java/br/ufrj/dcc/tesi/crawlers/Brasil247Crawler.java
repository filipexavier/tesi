package br.ufrj.dcc.tesi.crawlers;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.ufrj.dcc.tesi.daos.NoticiaDAO;
import br.ufrj.dcc.tesi.enums.Portal;
import br.ufrj.dcc.tesi.models.Noticia;
import br.ufrj.dcc.tesi.utils.DateUtil;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;

import com.mongodb.DBCollection;

public class Brasil247Crawler {
	
		//Resultado da busca no Brasil247 por "Eleições 2014"
		private static String url = "http://www.google.com.br/cse?cx=partner-pub-8594520780167960:4101184130&ie=UTF-8&q=elei%C3%A7%C3%B5es+2014&sa=Buscar&ref=&nojs=1";
		private static String userAgent = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
		private static Integer savedNewsNum = 1;	
		public static void main(String[] args) throws IOException, ParseException {
			
			DBCollection collection = MongoDBUtil.getInstance().getDatabase().getCollection(MongoDBUtil.COLLECTION);
			crawlSearchResults(url,collection);
		}

		private static List<Noticia> crawlSearchResults(String url, DBCollection collection) {
			String nextPageUrl = url;
			int page = 1;
			Document result = null;
			while(nextPageUrl != null){
				try {
					result = Jsoup.connect(nextPageUrl).timeout(0).userAgent(userAgent).get();
				} catch (IOException e) {
					System.out.println("Problema ao pegar o html da pagina " + page);
					e.printStackTrace();
					page++;
					nextPageUrl = getNextPage(result,page);
					continue;
				}
				List<String> noticiasLinks = getLinksResult(result);
				List<Noticia> noticias = getNoticiasFromLinks(noticiasLinks);
				System.out.println("Pagina " + page);
				savedNewsNum = NoticiaDAO.getInstance().save(collection, noticias, savedNewsNum);
				page++;
				nextPageUrl = getNextPage(result,page);
			}
			System.out.println("Terminou");
			return null;
		}
		
		private static String getNextPage(Document result, int page) {
			//Elements nextPage = result.select("div#navbar").select("td.b").select("a[href]");
			Elements nextPage = null;
			Elements navLinks = result.select("div#navbar").select("td");
			for(Element e : navLinks){
				if(e.text().equals(String.valueOf(page))){
					nextPage = e.select("a[href]");
					return nextPage.get(0).absUrl("href");
				}
			}
			return null;
		}

		private static List<Noticia> getNoticiasFromLinks(List<String> noticiasLinks) {
			List<Noticia> noticias = new ArrayList<Noticia>();
			for (String link : noticiasLinks) {
				Document doc;
				try {
					doc = Jsoup.connect(link).timeout(0).userAgent(userAgent).get();
				} catch (IOException e) {
					System.out.println("Problema ao pegar o html da noticia: " + link);
					e.printStackTrace();
					continue;
				}
				
				Noticia noticia = getNoticia(doc,DateUtil.dataInicial(),DateUtil.dataFinal());
				//System.out.println(doc.toString());
				if(noticia == null) continue;
				noticias.add(noticia);
			}
			return noticias;
		}

		private static Noticia getNoticia(Document doc, Date periodoInicio, Date periodoFim) {
			
			Elements entry = doc.select("div.entry");
			if(entry == null || entry.size() == 0) return null;
			Elements elements = entry.get(0).select("p");

			if(!verificaNoticiaComTexto(elements)) return null;
			Noticia n = new Noticia();
			String strData = null;

			//URL
			n.setUrl(doc.baseUri());
			
			//PORTAL
			n.setPortal(Portal.BRASIL247);
			
	        //CORPO
			StringBuilder texto = new StringBuilder();
	        for(Element e : elements){
	        	texto.append(e.text());
	        }
	        n.setTexto(texto.toString());
	        
	        //TITULO
	        Elements titulos = doc.select("div.featured-box");
	        n.setTitulo(titulos.select("h2").text());
	        
	        //SUBTITULO
	        Elements subtitulo = titulos.select("p");
	        if(subtitulo != null && subtitulo.size()>0){
		        texto = new StringBuilder();
		        for(Element e : subtitulo){
		        	Elements data = e.select(".meta");
		        	if(data == null || data.size() == 0)
		        		texto.append(e.text());
		        	else{
		        		strData = data.text();
		        	}
		        }
				n.setSubTitulo(texto.toString());
	        }else{//SEGUNDA ESTRATEGIA	
	        	 subtitulo = entry.select("blockquote");
	        	 if(subtitulo != null){
	        		 n.setSubTitulo(subtitulo.text());
	        	 }
	        	 //AUTOR
	        	 Elements autor_data = doc.select("div.author-details");
	        	 Elements timeElement = autor_data.select("time");
	        	 strData = timeElement.text();
	        	 String autor = autor_data.text().replace(strData,"");
	        	 n.setAutor(autor);
	        }
	        
			//DATA
	        Date dataNoticia;
			try {
				dataNoticia = getDate(strData);
			} catch (NumberFormatException e1) {
				System.out.println("Problema ao tentar converter a data: " + strData);
				System.out.println(n.getTitulo() + " - " + doc.baseUri());
				e1.printStackTrace();
				return null;
			} catch (NullPointerException e1) {
				System.out.println("Data da noticia veio nula");
				System.out.println(n.getTitulo() + " - " + doc.baseUri());
				e1.printStackTrace();
				return null;
			}
	        if(dataNoticia == null) return null;
	        if(dataNoticia.after(periodoFim) || dataNoticia.before(periodoInicio)) return null;
	        n.setData(dataNoticia);
	        
			return n;
		}

		private static Date getDate(String text) throws NullPointerException,NumberFormatException{
			if(text == null || text == "") throw new NullPointerException();
			
			Date d;
			try {
				String[] datahora = text.split("às");
				
				//DATA
				String data = datahora[0];
				String[] dataVec = data.split("de");
				int dia = Integer.parseInt(dataVec[0].replace(" ", ""));
				int mes = getMonthFromLabel(dataVec[1].replace(" ", ""));
				int ano = Integer.parseInt(dataVec[2].replace(" ", ""));
				
				//HORA
				String hora = datahora[1];
				String[] horaVec = hora.replace(" ", "").split(":");
				int h = Integer.parseInt(horaVec[0]);
				int m = Integer.parseInt(horaVec[1]);
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(ano,mes,dia,h,m);
				d = calendar.getTime();
			} catch (NumberFormatException e) {
				throw new NumberFormatException();
			}
			return d;
		}
		
		private static int getMonthFromLabel(String strMes) {
			String[] meses = DateFormatSymbols.getInstance(new Locale("pt","BR")).getMonths();
			for(int i = 0; i<meses.length; i++){
				if(strMes.compareToIgnoreCase(meses[i]) == 0){
					return i;
				}
			}
			return 0;
		}

		private static boolean verificaNoticiaComTexto(Elements elements) {
			if(elements == null || elements.size() == 0) return false;
			if(elements.size() == 1 && elements.get(0).select("a.editlink") != null) return false;
			return true;
		}

		private static List<String> getLinksResult(Document result) {
			Elements searchResultLinks = result.select("a.l");
			List<String> links = new ArrayList<String>();
			for(Element element : searchResultLinks){
				String link = element.absUrl("href");
				links.add(link);
			}
	        return links;
		}
}
