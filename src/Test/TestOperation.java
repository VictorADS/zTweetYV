package Test;


public class TestOperation {

	public static void main(String[] args) {
		String obj="bonjour, je suis";
		String mot=obj.replaceAll("[^\\w\\s]","");
		String words[]=mot.split(" ");
		for(String s: words){
		System.out.println(s);
		}
	}

}
