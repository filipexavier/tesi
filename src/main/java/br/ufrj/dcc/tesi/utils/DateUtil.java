package br.ufrj.dcc.tesi.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Date dataInicial() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 8, 1, 0, 0);
		Date d = calendar.getTime();
		
		return d;
	}
	
	public static Date dataFinal() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 10, 31, 0, 0);
		Date d = calendar.getTime();
		
		return d;
	}
	
}
