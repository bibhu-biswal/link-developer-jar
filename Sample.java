import com.hp.*;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class Sample		
{
	public static void main(String[] args) throws IOException
	{
		Scanner scan = new Scanner(new File("cred.txt"));
		String id = scan.nextLine();
		String secret = scan.nextLine();
		LivePaper lp = LivePaper.auth(id, secret);

		if(lp == null)
		{
        	System.err.println("Authentication failure");
        	System.exit(1);
		}

		try {
		System.out.println(lp.shorten("http://en.wikipedia.org/wiki/Bitly"));

		byte[] qrbytes = lp.qr_bytes("http://en.wikipedia.org/wiki/QR_code");
		FileOutputStream fos = new FileOutputStream("qrcode.png");
		fos.write(qrbytes);
		fos.close(); 

		byte[] wm_bytes = lp.watermark_bytes("http://www.jpl.nasa.gov/spaceimages/images/mediumsize/PIA17011_ip.jpg",
											 "http://en.wikipedia.org/wiki/Watermark");
		FileOutputStream fos2 = new FileOutputStream("wm.jpg");
		fos2.write(wm_bytes);
		fos2.close();
		}
		catch (Exception e)
		{
			System.err.println("Error!");
			e.printStackTrace();
		}

	}
}
