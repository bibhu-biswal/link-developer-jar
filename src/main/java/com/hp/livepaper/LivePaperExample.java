package com.hp.livepaper;

import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.File;

public class LivePaperExample {
  /* When main() is run, this example client will expect to find your "client
       id" and "secret id" credentials (and nothing else) on successive lines
       of a "mykeys.txt" file in the local directory.
     To obtain your credentials, you will need to create an account at the
       LivePaper API site (https://link.livepaperdeveloper.com), which will
       then show the credentials to you which you can then copy intto your local
       "mykeys.txt" file.
   */
  public static void main(String[] args) {
    try {
      String key_file = "mykeys.txt";
      Scanner scan = new Scanner(new File(key_file));
      String id = scan.nextLine();
      String secret = scan.nextLine();
      scan.close();
      if (true) {
        System.out.println("Authenticating with LivePaperSession...");
        LivePaperSession.setLppBasicAuth(id, secret);
        System.out.println("  ShortTrigger.create()...");
        ShortTrigger tr = ShortTrigger.create("My ShortTrigger");
        System.out.println("    Trigger Name: \""+tr.getName()+"\"");
        System.out.println("    Trigger Id: \""+tr.getId()+"\"");
        System.out.println("    Short URL: \""+tr.getShortUrl()+"\"");
      //System.exit(0);
      }
      System.out.println("Authenticating with LivePaper...");
      LivePaper lp = LivePaper.auth(id, secret);
      if (lp == null) {
        System.err.println("  Authentication failure!");
        System.exit(1);
      }
      if (true) {
        String url = "http://www.hp.com";
        System.out.println("Creating short URL for \"" + url + '"');
        String short_url = lp.shorten(url);
        System.out.println("  Your short URL is => \"" + short_url + '"');
      }
      if (true) {
        String url = "http://www.hp.com";
        String img_out = "image_qr_code.png";
        System.out.println("Creating QR code for \"" + url + '"');
        byte[] qrbytes = lp.qr_bytes(url);
        FileOutputStream fos = new FileOutputStream(img_out);
        fos.write(qrbytes);
        fos.close();
        System.out.println("  into local file \"" + img_out + '"');
      }
      // Watermarked image
      if (true) {
        String url = "http://www.hp.com";
        String img_in = "http://bit.ly/1zlfjU6";
        String img_out = "image_watermark.jpg";
        System.out.println("Watermarking JPG image at \"" + img_in + "\" (an image of the HP logo)");
        System.out.println("  into local file \"" + img_out + "\"");
        System.out.println("  which when scanned with the LinkReader app will take you to \"" + url + "\"");
        System.out.println("  (note: downloading the watermarked image behind a proxy server may timeout!)");
        byte[] wm_bytes = lp.watermark_bytes(img_in, url);
        FileOutputStream fos2 = new FileOutputStream(img_out);
        fos2.write(wm_bytes);
        fos2.close();
      }
      System.out.println("done!");
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
