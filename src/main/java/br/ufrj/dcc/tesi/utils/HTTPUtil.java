package br.ufrj.dcc.tesi.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTPUtil {
	public static String doGET(String url) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(url));
			return IOUtils.toString(response.getEntity().getContent());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
