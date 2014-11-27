package br.ufrj.dcc.tesi.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.mongodb.DBObject;
import br.ufrj.dcc.tesi.enums.Portal;
import br.ufrj.dcc.tesi.utils.MongoDBUtil;

public class Noticia implements Serializable {

	private static final long serialVersionUID = -3401155538699619489L;
	
	private long id;	
	private String url;
	private Portal portal;	
	private String titulo;
	private String subTitulo;
	private String texto;
	private Date data;
	private Date dataAtualizacao;

	@Column
	private List<DBObject> entidades;
	
	private String autor;
	
	public Noticia(){
		
	}
	
	public Noticia(String url, Portal portal){
		this.url = url;
		this.portal = portal;
	}
	
	public Noticia(String url, Portal portal, String titulo, String texto, Date data){
		this.url = url;
		this.portal = portal;
		this.titulo = titulo;
		this.texto = texto;
		this.data = data;
	}


	public Noticia(DBObject n) {
		try {
			this.url = n.get("url").toString();
		} catch (Exception e) {
			System.out.println("Nao foi possivel obter o campo url da noticia recuperada do banco");
		}
		try {
		this.autor = n.get("autor").toString();
		} catch (Exception e) {
			System.out.println("Nao foi possivel obter o campo autor da noticia recuperada do banco");
		}
		try {
			this.texto = n.get("texto").toString();
		} catch (Exception e) {
			System.out.println("Nao foi possivel obter o campo texto da noticia recuperada do banco");
		}
		try {
			this.titulo = n.get("titulo").toString();
		} catch (Exception e) {
			System.out.println("Nao foi possivel obter o campo titulo da noticia recuperada do banco");
		}
		try {
			this.data = MongoDBUtil.parseDate(n.get("data").toString());
		} catch (Exception e) {
			System.out.println("Nao foi possivel obter o campo data da noticia recuperada do banco");
		}
		try {
			this.portal = Portal.valueOf((n.get("portal").toString()));
		} catch (Exception e) {
			System.out.println("Nao foi possivel obter o campo portal da noticia recuperada do banco");
		}
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Portal PortalVO() {
		return portal;
	}

	public String getPortal() {
		return portal.name();
	}

	public void setPortal(Portal portal) {
		this.portal = portal;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long get_id() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<DBObject> getEntidades() {
		return entidades;
	}

	public void setEntidades(List<DBObject> entities) {
		this.entidades = entities;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getSubTitulo() {
		return subTitulo;
	}

	public void setSubTitulo(String subTitulo) {
		this.subTitulo = subTitulo;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
}
