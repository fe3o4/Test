package OtherTests;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class getSourceFileTest {

	
	public static void main(String[] args){
	    try {
	    	URL url = Thread.currentThread().getContextClassLoader().getResource("getSourceFileTest.java");
			File file = new File(url.toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
