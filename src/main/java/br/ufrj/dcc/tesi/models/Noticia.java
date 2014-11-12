package br.ufrj.dcc.tesi.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrj.dcc.tesi.enums.Portal;

@Entity
@Table(name="noticia")
public class Noticia implements Serializable {

	private static final long serialVersionUID = -3401155538699619489L;

	@Transient
	private long id;
	
	@Id
	@Column
	private String url;
	
	@Column
	private Portal portal;
	
	@Column
	private String titulo;
	
	@Column
	private String texto;
	
	@Column
	private Date data;
	
	
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
}
