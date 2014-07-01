import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.yaml.snakeyaml.Yaml;


/* Simple class to test SampleAuth
 * Must have a myauth.yml file in src/main/java/client
 * 
 *  Format for myauth.yml: 
 *  >id: your_client_id
 *  >secret: your_client_secret
 *  
 *  */
public class TestAuth {

	public static void main(String[] args) throws IOException
	{
		SampleAuth test = new SampleAuth();
		
		Yaml obj = new Yaml();
		
	    InputStream input = new FileInputStream(new File("src/main/java/myauth.yml"));
		
	    //Does the input have to be a map (for the final jar)? Might be easier as an array of strings
	    
	    //Uncomment if testing with yml file
	    //HashMap<String,String> map = (HashMap<String, String>) obj.load(input);
		//test.auth(map);
	    
	    //comment out if testing with yml file
	    test.auth("clientID", "secret");
	}
}
