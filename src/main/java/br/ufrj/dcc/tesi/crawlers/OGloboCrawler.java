package br.ufrj.dcc.tesi.crawlers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OGloboCrawler {

	static int PAGE_NUMBER = 1;
	static int MAX_PAGE_NUMBER = 50;
	
	public static void main(String[] args) throws IOException {
		
		while (PAGE_NUMBER <= MAX_PAGE_NUMBER) {
			getWeblink(PAGE_NUMBER);
			PAGE_NUMBER++;
		}        
	}

	public static void getWeblink(int page_number) throws IOException {
		
		String url = "http://oglobo.globo.com/busca/?q=elei%C3%A7%C3%B5es+2014&page=" + page_number + "&_=1415670163800&species=not%C3%ADcias";
		Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        
        for (Element e: links) {
        	if (e.attr("abs:href").contains("http://oglobo.globo.com/brasil/segundo-turno-em-debate.html")){
        		continue;
        	} else
        	if (e.attr("abs:href").contains("http://oglobo.globo.com/")) {
        		if (e.hasClass("cor-produto") && e.hasAttr("title")) {
        			System.out.println(e.attr("abs:href"));
        			processPage(e.attr("abs:href"));
        		} else if (e.hasClass("logo-topo")) {
        			continue;
        		}
        	}
        }
	}
	
	public static void processPage(String url) {
		
		Elements page = null;
		Document doc = null;
		
		try {
			doc = Jsoup.connect(url).get();
			page = doc.select("article");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
			String title = null;
			String description = null;
			String author = null;
			String pubDate = null;
			String attDate = null;
			Elements articleBody = null;
			StringBuilder text = new StringBuilder("");
			
			for (Element e: page) {
				title = e.getElementsByAttributeValue("itemprop", "headline").text();
				description = e.getElementsByAttributeValue("itemprop", "description").text();
				author =  e.getElementsByAttributeValue("itemprop", "author").text();
				pubDate = e.getElementsByClass("data-cadastro").text();
				attDate = e.getElementsByClass("data-atualizacao").text();
				if (!attDate.isEmpty()) {
					attDate = attDate.substring(11);
				}
				articleBody = e.select("div[itemprop=articleBody]");
				for (Element p: articleBody) {
					
					text.append(p.getElementsByTag("p").text() + "\n");
				}
			}
	        
			System.out.println(title);
			System.out.println(description);
			System.out.println(author);
			System.out.println(pubDate);
			System.out.println(attDate);
			System.out.println(text);
	        System.out.println("----");
        
	}
}
