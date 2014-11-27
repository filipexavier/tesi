package br.ufrj.dcc.tesi.models;

import java.io.Serializable;
import java.util.Date;

import br.ufrj.dcc.tesi.enums.Portal;

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
	private String entidades;
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

	public String getEntidades() {
		return entidades;
	}

	public void setEntidades(String entidades) {
		this.entidades = entidades;
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
