package tokenize;

public class GntTokenizer {

	public static String[] splitTokenizer(String string){
		System.out.println(">>>"+string+"<<<");
		
		String delims = "[ |\\,|\\:|\\.|\\\"|\\(|\\)|\\!|\\?]+";
		return string.split(delims);
	}

}
