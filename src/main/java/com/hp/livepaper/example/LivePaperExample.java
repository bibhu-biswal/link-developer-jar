package com.hp.livepaper.example;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Scanner;
import com.hp.livepaper.ImageStorage;
import com.hp.livepaper.Link;
import com.hp.livepaper.LivePaperException;
import com.hp.livepaper.LivePaperSession;
import com.hp.livepaper.Payoff;
import com.hp.livepaper.QrTrigger;
import com.hp.livepaper.ShortTrigger;
import com.hp.livepaper.Trigger;
import com.hp.livepaper.WmTrigger;

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
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
      if (true) {
        System.out.println("Authenticating with LivePaperSession...");
        LivePaperSession lp = LivePaperSession.create(id, secret);
        System.out.println();
        boolean testShortUrl  = true;
        boolean testQrCode    = true;
        boolean testWatermark = true;
        boolean testLists     = true;
        if (testShortUrl) {
          System.out.println("Creating Short URL...");
          System.out.println("  ShortTrigger.create()...");
          ShortTrigger tr = ShortTrigger.create(lp, "My ShortTrigger");
          System.out.println("    Trigger Name: \"" + tr.getName() + "\"");
          System.out.println("    Trigger Id: \"" + tr.getId() + "\"");
          System.out.println("    Short URL: \"" + tr.getShortUrl() + "\"");
          System.out.println("    Short dateCreated: \"" + tr.getDateCreated() + "\"");
          System.out.println("    Short dateModified: \"" + tr.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : tr.getLinks().keySet())
            System.out.println("      " + item + ": " + tr.getLinks().get(item));
          System.out.println("  Payoff.create()...");
          Payoff po = Payoff.create(lp, "My Payoff", Payoff.Type.WEB_PAYOFF, "http://www.hp.com");
          System.out.println("    Payoff Name: \"" + po.getName() + "\"");
          System.out.println("    Payoff Id: \"" + po.getId() + "\"");
          System.out.println("    Payoff URL: \"" + po.getId() + "\"");
          System.out.println("    Short dateCreated: \"" + po.getDateCreated() + "\"");
          System.out.println("    Short dateModified: \"" + po.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : po.getLinks().keySet())
            System.out.println("      " + item + ": " + po.getLinks().get(item));
          System.out.println("  Link.create()...");
          Link ln = Link.create(lp, "My Link", tr, po);
          System.out.println("    Link Name: \"" + ln.getName() + "\"");
          System.out.println("    [Trigger Id: \"" + ln.getTrigger().getId() + "\"]");
          System.out.println("    [Payoff Id: \"" + ln.getPayoff().getId() + "\"]");
          System.out.println("    dateCreated: \"" + ln.getDateCreated() + "\"");
          System.out.println("    dateModified: \"" + ln.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : ln.getLinks().keySet())
            System.out.println("      " + item + ": " + ln.getLinks().get(item));
          System.out.println("  Done creating Short URL");
          System.out.println("  Updating Short URL");
          System.out.println("    ShortTrigger.setName()...");
          tr.setName(tr.getName() + " (renamed)");
          System.out.println("    ShortTrigger.update()...");
          tr.update();
          System.out.println("    Payoff.setName()...");
          po.setName(po.getName() + " (renamed)");
          System.out.println("    Payoff.update()...");
          po.update();
          System.out.println("    Link.setName()...");
          ln.setName(ln.getName() + " (renamed)");
          System.out.println("    Link.update()...");
          ln.update();
          System.out.println("  Deleting Link...");
          String ID = ln.getId();
          ln.delete();
          ln = null;
          try {
            System.out.println("  Testing Link.get() on deleted object id...");
            // should not be able to get the deleted object now
            ln = Link.get(lp, ID);
          }
          catch (LivePaperException e) {
            if (!e.getMessage().contains("HTTP Status 404 - Not Found")) {
              System.err.println("rethrowing");
              throw e;
            }
            System.out.println("    yep... it correctly failed...");
          }
          System.out.println("  Deleting Payoff...");
          po.delete();
          po = null;
          System.out.println("  Deleting Trigger...");
          tr.delete();
          tr = null;
          System.out.println();
        }
        if (testQrCode) {
          System.out.println("Creating QR Code...");
          System.out.println("  QrTrigger.create()...");
          QrTrigger tr = QrTrigger.create(lp, "My QrTrigger");
          System.out.println("    Trigger Name: \"" + tr.getName() + "\"");
          System.out.println("    Trigger Id: \"" + tr.getId() + "\"");
          System.out.println("    dateCreated: \"" + tr.getDateCreated() + "\"");
          System.out.println("    dateModified: \"" + tr.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : tr.getLinks().keySet())
            System.out.println("      " + item + ": " + tr.getLinks().get(item));
          System.out.println("  Payoff.create()...");
          Payoff po = Payoff.create(lp, "My Payoff", Payoff.Type.WEB_PAYOFF, "http://www.hp.com");
          System.out.println("    Payoff Name: \"" + po.getName() + "\"");
          System.out.println("    Payoff Id: \"" + po.getId() + "\"");
          System.out.println("    Payoff URL: \"" + po.getId() + "\"");
          System.out.println("    dateCreated: \"" + po.getDateCreated() + "\"");
          System.out.println("    dateModified: \"" + po.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : po.getLinks().keySet())
            System.out.println("      " + item + ": " + po.getLinks().get(item));
          System.out.println("  Link.create()...");
          Link ln = Link.create(lp, "My Link", tr, po);
          System.out.println("    [Link Name: \"" + ln.getName() + "\"]");
          System.out.println("    [Trigger Id: \"" + ln.getTrigger().getId() + "\"]");
          System.out.println("    Payoff Id: \"" + ln.getPayoff().getId() + "\"");
          System.out.println("    dateCreated: \"" + ln.getDateCreated() + "\"");
          System.out.println("    dateModified: \"" + ln.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : ln.getLinks().keySet())
            System.out.println("      " + item + ": " + ln.getLinks().get(item));
          System.out.println("  Downloading QR code...(at size of 250px)");
          byte[] qrbytes = tr.downloadQrCode(250);
          String img_out = "image_qr_code_" + sdf.format(Calendar.getInstance().getTime()) + ".png";
          FileOutputStream fos = new FileOutputStream(img_out);
          fos.write(qrbytes);
          fos.close();
          System.out.println("    into local file \"" + img_out + '"');
          System.out.println("  Done creating QR Code.");
          System.out.println("  Deleting Link...");
          ln.delete();
          ln = null;
          System.out.println("  Deleting Payoff...");
          po.delete();
          po = null;
          System.out.println("  Deleting Trigger...");
          tr.delete();
          tr = null;
          System.out.println();
        }
        if (testWatermark) {
          System.out.println("Creating Watermarked Image...");
          String urlOfImageToBeWatermarked = "http://upload.wikimedia.org/wikipedia/commons/8/82/Watermarks_20_Euro.jpg";
          System.out.println("  Image.upload()...");
          System.out.println("    downloading user image from:");
          System.out.println("      " + urlOfImageToBeWatermarked);
          System.out.println("      and uploading to storage service...");
          String uploaded_image_url = ImageStorage.uploadJpg(lp, urlOfImageToBeWatermarked);
          System.out.println("    uploaded image now available at:");
          System.out.println("      " + uploaded_image_url);
          System.out.println("  WmTrigger.create()...");
          WmTrigger tr = WmTrigger.create(lp, "My WmTrigger", new WmTrigger.Strength(10), new WmTrigger.Resolution(75), uploaded_image_url);
          System.out.println("    Trigger Name: \"" + tr.getName() + "\"");
          System.out.println("    Trigger Id: \"" + tr.getId() + "\"");
          System.out.println("    dateCreated: \"" + tr.getDateCreated() + "\"");
          System.out.println("    dateModified: \"" + tr.getDateModified() + "\"");
          System.out.println("    strength: \"" + tr.getStrength().toString() + "\"");
          System.out.println("    resolution: \"" + tr.getResolution().toString() + "\"");
          System.out.println("    imageUrl: \"" + tr.getImageUrl() + "\"");
          System.out.println("    Links:");
          for (String item : tr.getLinks().keySet())
            System.out.println("      " + item + ": " + tr.getLinks().get(item));
          System.out.println("  Payoff.create()...");
          Payoff po = Payoff.create(lp, "My Payoff", Payoff.Type.WEB_PAYOFF, "http://www.hp.com");
          System.out.println("    Payoff Name: \"" + po.getName() + "\"");
          System.out.println("    Payoff Id: \"" + po.getId() + "\"");
          System.out.println("    Payoff URL: \"" + po.getId() + "\"");
          System.out.println("    dateCreated: \"" + po.getDateCreated() + "\"");
          System.out.println("    dateModified: \"" + po.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : po.getLinks().keySet())
            System.out.println("      " + item + ": " + po.getLinks().get(item));
          System.out.println("  Link.create()...");
          Link ln = Link.create(lp, "My Link", tr, po);
          System.out.println("    [Link Name: \"" + ln.getName() + "\"]");
          System.out.println("    [Trigger Id: \"" + ln.getTrigger().getId() + "\"]");
          System.out.println("    Payoff Id: \"" + ln.getPayoff().getId() + "\"");
          System.out.println("    dateCreated: \"" + ln.getDateCreated() + "\"");
          System.out.println("    dateModified: \"" + ln.getDateModified() + "\"");
          System.out.println("    Links:");
          for (String item : ln.getLinks().keySet())
            System.out.println("      " + item + ": " + ln.getLinks().get(item));
          System.out.println("  Downloading watermarked image...");
          byte[] wmbytes = tr.downloadWatermarkedJpgImage();
          String wm_img_out = "image_watermark_" + sdf.format(Calendar.getInstance().getTime()) + ".jpg";
          FileOutputStream fos = new FileOutputStream(wm_img_out);
          fos.write(wmbytes);
          fos.close();
          System.out.println("    into local file \"" + wm_img_out + '"');
          System.out.println("  Done creating Watermarked Image...");
          System.out.println("  Deleting Link...");
          ln.delete();
          ln = null;
          System.out.println("  Deleting Payoff...");
          po.delete();
          po = null;
          System.out.println("  Deleting Trigger...");
          tr.delete();
          tr = null;
          System.out.println();
        }
        if (testLists) {
          System.out.println("Getting List of Trigger objects...");
          Map<String, Trigger> triggers = Trigger.list(lp);
          System.out.println("  Found " + triggers.keySet().size() + " triggers!");
          for (String triggerId : triggers.keySet()) {
            Trigger tr = triggers.get(triggerId);
            System.out.println("    \"" + tr.getName() + "\" [" + tr.getClass().getName() + "]");
          }
          System.out.println();
          System.out.println("Getting List of Payoff objects...");
          Map<String, Payoff> payoffs = Payoff.list(lp);
          System.out.println("  Found " + payoffs.keySet().size() + " payoffs!");
          for (String payoffId : payoffs.keySet()) {
            Payoff po = payoffs.get(payoffId);
            System.out.println("    \"" + po.getName() + "\"");
          }
          System.out.println();
          System.out.println("Getting List of Link objects...");
          Map<String, Link> links = Link.list(lp);
          System.out.println("  Found " + links.keySet().size() + " links!");
          for (String linkId : links.keySet()) {
            Link ln = links.get(linkId);
            System.out.println("    \"" + ln.getName() + "\"");
          }
          System.out.println();
        }
        System.out.println("All done with example!");
      }
    }
    catch (Exception e) {
      System.err.print("ERROR: ");
      e.printStackTrace();
      System.exit(1);
    }
  }
}