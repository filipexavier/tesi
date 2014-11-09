package br.ufrj.dcc.tesi.crawlers;

import java.io.IOException;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.ufrj.dcc.tesi.enums.Portal;
import br.ufrj.dcc.tesi.models.Noticia;
import br.ufrj.dcc.tesi.utils.MySQLUtil;

public class VejaCrawler {
	
	//Noticias relacionadas as Eleicoes 2014, de 01/08/2014 a 31/11/2014
	private static String url = "http://veja.abril.com.br/busca/?qu=elei%C3%A7%C3%B5es+2014&origembusca=bsc&multimidia-meta_nav:Not%C3%ADcia&editoria-meta_nav:Brasil&date:[2014-08-01T00:00:00Z%20TO%202014-10-31T23:59:00Z]&dt=per";
	
	public static void main(String[] args) throws IOException {
		
		Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        
        Session session = MySQLUtil.getInstance().getSession();
        Transaction t = session.beginTransaction();
        
        for(Element e: links){
        	Noticia n = new Noticia(e.absUrl("href"),Portal.VEJA);
        	
        	try {
				session.save(n);
				System.out.println("URL salva: " + n.getUrl());
			} catch (NonUniqueObjectException e1) {
				System.out.println("URL ja foi salva: " + n.getUrl());
			}
        }
        
        t.commit();
        session.close();
	}
		
}
