package br.ufrj.dcc.tesi.rest;

import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;

public abstract class AbstractREST {
	protected static final String JSON_UTF8 = MediaType.APPLICATION_JSON + "; charset=UTF-8";
	protected String serialize(DBCursor cursor) {
		return JSON.serialize(cursor);
	}
	
	protected String prettySerialize(DBCursor cursor) {
		String json = JSON.serialize(cursor);
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		return gson.toJson(new JsonParser().parse(json));
	}
}
