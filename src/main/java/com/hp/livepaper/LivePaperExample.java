package com.hp.livepaper;

import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.File;

public class LivePaperExample {
  /*
   * When main() is run, this example client will expect to find your "client
   * id" and "secret id" credentials (and nothing else) on successive lines
   * of a "mykeys.txt" file in the local directory.
   * 
   * To obtain your credentials, you will need to create an account at the
   * LivePaper API site (https://link.livepaperdeveloper.com), which will
   * then show the credentials to you which you can then copy intto your local
   * "mykeys.txt" file.
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
        System.out.println("    Trigger Name: \"" + tr.getName() + "\"");
        System.out.println("    Trigger Id: \"" + tr.getId() + "\"");
        System.out.println("    Short URL: \"" + tr.getShortUrl() + "\"");
        System.out.println("    Short dateCreated: \"" + tr.getDateCreated() + "\"");
        System.out.println("    Short dateModified: \"" + tr.getDateModified() + "\"");
        System.out.println("    Links:");
        for (String item : tr.getLinks().keySet())
          System.out.println("      " + item + ": " + tr.getLinks().get(item));
        System.out.println(" Payoff.create()...");
        Payoff po = Payoff.create("My Payoff", Payoff.Type.WEB_PAYOFF, "http://www.hp.com");
        System.out.println("    Payoff Name: \"" + po.getName() + "\"");
        System.out.println("    Payoff Id: \"" + po.getId() + "\"");
        System.out.println("    Payoff URL: \"" + po.getId() + "\"");
        System.out.println("    Short dateCreated: \"" + po.getDateCreated() + "\"");
        System.out.println("    Short dateModified: \"" + po.getDateModified() + "\"");
        System.out.println("    Links:");
        for (String item : tr.getLinks().keySet())
          System.out.println("      " + item + ": " + tr.getLinks().get(item));
        // System.exit(0);
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
        String img_in = "http://h30499.www3.hp.com/t5/image/serverpage/image-id/55235i511F39504D83FCBA?v=mpbl-1";
        String img_out = "image_watermark.jpg";
        System.out.println("Watermarking JPG image (of HP Logo)");
        System.out.println("  (" + img_in + ")");
        System.out.println("  into local file \"" + img_out + "\"");
        System.out.println("  which, when scanned with the LinkReader app, will take you to \"" + url + "\"");
        byte[] wm_bytes = lp.watermark_bytes(img_in, url);
        FileOutputStream fos2 = new FileOutputStream(img_out);
        fos2.write(wm_bytes);
        fos2.close();
      }
      System.out.println("done!");
    }
    catch (Exception e) {
      System.err.print("ERROR: ");
      e.printStackTrace();
      System.exit(1);
    }
  }
}
