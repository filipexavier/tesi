package br.ufrj.dcc.tesi.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date dataInicial() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 6, 31, 23, 59);
		Date d = calendar.getTime();
		
		return d;
	}
	
	public static Date dataFinal() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 10, 1, 0, 0);
		Date d = calendar.getTime();
		
		return d;
	}

	public static String getPrettyDate(Date data) {
		StringBuilder sb = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		sb.append(calendar.get(Calendar.DAY_OF_MONTH)).append("/").append(calendar.get(Calendar.MONTH)+1).append("/").append(calendar.get(Calendar.YEAR));
		return sb.toString();
	}
	
}
