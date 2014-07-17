import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
		try {
			input = new FileInputStream(new File("src/main/java/myauth.yml"));
		} catch (FileNotFoundException e) {
			fail("File myauth.yml not found. Must have a myauth.yml file in src/main/java/");
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
	public void testShorten() throws IOException
	{
		test.auth(map);
		String url = test.shorten("www.google.com");
		assertNotNull("Must return a non-null String", url);
		System.out.println(url);
	}
	
	@Test
	public void testQRNotAuthorized() throws IOException
	{
		byte[] returned = test.qr_bytes("something");
		if(returned != null)
			fail("qr_bytes must return null for unauthorized users.");
	}
	
	@Test
	public void testQR() throws IOException
	{
		test.auth(map);
		byte[] returned = test.qr_bytes("www.google.com");
		assertNotNull("Must return a non-null byte array", returned);
		FileOutputStream stream = new FileOutputStream("qrcode.png");
		stream.write(returned);
	    stream.close();
	}

	@Test
	public void testWatermark() throws IOException, FileNotFoundException {
		test.auth(map);

		byte[] wm_bytes = test.watermark_bytes("http://www.letsstartsmall.com/ITSE2313_WebAuthoring/images/unit3/jpg_example1.jpg", "http://www.hp.com");
		assertNotNull("Must return a non-null byte array", wm_bytes);
	    
	    FileOutputStream stream = new FileOutputStream("wm.jpg");
		stream.write(wm_bytes);
	    stream.close();
	}
	
	@Test
	public void testWMNotAuthorized() throws IOException
	{
		byte[] returned = test.watermark_bytes("something", "something else");
		if(returned != null)
			fail("watermark_bytes must return null for unauthorized users.");
	}
	
	
	public static void main(String[] args) throws IOException
	{
		  junit.textui.TestRunner.run(TestClient.class);
	}
}
