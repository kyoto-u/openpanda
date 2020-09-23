package courselink.kyoto_u.ac.jp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	
	
	public static String getDateString(Date date){
		String dateString = "";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT );
		try{
			dateString = sdf.format(date);
		}catch(Exception e){
			
		}
		return dateString;
	}
}
