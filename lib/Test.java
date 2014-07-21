import java.io.FileOutputStream;
import java.io.IOException;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		LivePaper lp = LivePaper.auth("328mwc823xsp2vpyzfj6d61ediulz492", "iPqFk1z5CqG59whtpk3AIcrNbrSSrXHu");
		System.out.println(lp.shorten("www.google.com"));

		byte[] qr_bytes = lp.qr_bytes("www.amazon.com");
		FileOutputStream fos = new FileOutputStream("qrcode.png");
		fos.write(qr_bytes);
		fos.close();

		byte[] wm_bytes = lp.watermark_bytes("http://www.letsstartsmall.com/ITSE2313_WebAuthoring/images/unit3/jpg_example1.jpg", "http://www.hp.com");
		FileOutputStream fosWatermark = new FileOutputStream("wm.jpg");
		fosWatermark.write(wm_bytes);
		fosWatermark.close();

	}
}
