package fileio;

public class Logger {

	public void logi(String text){out("[i]" , text);}
	public void logw(String text){out("[w]" , text);}
	public void loge(String text){out("[e]" , text);}
	
	public void out(String symbol, String text){
		System.out.println(symbol + " " + text);
	}
	
}
