package br.ufrj.dcc.tesi.utils;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBUtil {

        private static String DB_HOST     = "ds051720.mongolab.com";
        private static int DB_PORT        = 51720;
        private static String DB_USERNAME = "tesi";
        private static String DB_PASSWORD = "tesi";
        private static String DB_NAME = "tesi";
        
        private static MongoDBUtil instance = new MongoDBUtil();
        private DB database;
        
        public static String COLLECTION = "noticias";
        
        public static MongoDBUtil getInstance() {
                if (instance.getDatabase() == null) {
                        try {
                                MongoClient mongoClient = new MongoClient(DB_HOST, DB_PORT);
                                instance.setDatabase(mongoClient.getDB( DB_NAME ));
                                instance.getDatabase().authenticate(DB_USERNAME, DB_PASSWORD.toCharArray());
                        } catch (UnknownHostException e) {
                                throw new IllegalStateException("Host do banco não encontrado.", e);
                        }
                }
                return instance;
        }

        public DB getDatabase() {
                return database;
        }
        
        private void setDatabase(DB database) {
                this.database = database;
        }

        public static void main(String[] args) {
                DB database = MongoDBUtil.getInstance().getDatabase();
                System.out.println(database);
        }
        
        public static Date parseTimestamp(String date)  {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(date));
            return calendar.getTime();
	    }
	    
	    public static Date parseDate(String date) {
	            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
	            try {   
	                    return dateFormat.parse(date);
	            } catch (ParseException e) {
	                    throw new RuntimeException(e);
	            }
    }
}
