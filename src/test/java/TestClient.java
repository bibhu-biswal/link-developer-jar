import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;


/* Simple class to test SampleAuth
 * Must have a myauth.yml file in src/main/java/client
 * 
 *  Format for myauth.yml: 
 *  >id: your_client_id
 *  >secret: your_client_secret
 *  
 *  */
public class TestClient extends TestCase {
	
	SampleClient test;
	Yaml obj;
	InputStream input;
	HashMap<String,String> map;
	
	@SuppressWarnings("unchecked")
	public void setUp()
	{
		test = new SampleClient();
		obj = new Yaml();
		System.getProperties().put("http.proxySet", "true");
		try {
			input = new FileInputStream(new File("src/main/java/myauth.yml"));
		} catch (FileNotFoundException e) {
			fail("File myauth.yml not found. Must have a myauth.yml file in src/main/java/client");
		}
		map = (HashMap<String, String>) obj.load(input);
	}
	
	@Test
	public void testCorrectKey()
	{
		test.auth(map);
		assertEquals("Response code must be 200 (OK).","200", ""+test.responseCode);
		assertNotNull(test.token, "Token cannot be null.");
	}
	
	@Test
	public void testIncorrectID()
	{
		map.put("id", "wrong");
		test.auth(map);
		assertEquals("Response code must be 401 (Invalid ID).","401", ""+test.responseCode);
		assertEquals("Token should be null.", test.token, null);
	}
	
	@Test
	public void testIncorrectSecret()
	{
		map.put("secret", "wrong");
		test.auth(map);
		assertEquals("Response code must be 401 (Invalid secret).","401", ""+test.responseCode);
		assertEquals("Token should be null.", test.token, null);
	}

	@Test
	public void testShortenNotAuthorized()
	{
		String returned = test.shorten("something");
		if(returned != null)
			fail("Shorten URL must return null for unauthorized users.");
	}
	
	@Test
	public void testShorten()
	{
		test.auth(map);
		String url = test.shorten("www.google.com");
		assertNotNull("Must return a non-null String", url);
	}
	
	
	public static void main(String[] args) throws IOException
	{
		  junit.textui.TestRunner.run(TestClient.class);
	}
}
