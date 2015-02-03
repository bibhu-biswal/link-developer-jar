package com.hp.livepaper.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
   * When main() is run, this example client will run a number of tests
   * against the Live Paper service.  The test will expect to find your "client
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
      System.out.println("Authenticating with LivePaperSession...");
      String id = "";
      String secret = "";
      if ( args.length == 2 ) {
        System.out.println("  using keys from command line...");
        id = args[0];
        secret = args[1];
      } else {
        String key_file = "mykeys.txt";
        System.out.println("  reading keys from \""+key_file+"\" (since not found on command line)...");
        try {
          Scanner scan = new Scanner(new File(key_file));
          id = scan.nextLine();
          secret = scan.nextLine();
          scan.close();
        }
        catch (IOException e) {
          System.err.println("    ERROR trying to read keys from \""+key_file+"\"...");
          throw e;
        }
      }
      if (true) {
        LivePaperSession lp = LivePaperSession.create(id, secret);
        boolean t = true;
        boolean f = false;
        if (t) testShortUrl(lp);
        if (t) testQrCode(lp);
        if (t) testWatermark(lp);
        if (t) testLists(lp);
        System.out.println("All done with tests!");
      }
    }
    catch (Exception e) {
      System.err.print("ERROR: ");
      e.printStackTrace();
      System.exit(1);
    }
  }
  private static void testShortUrl(LivePaperSession lp) throws LivePaperException {
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
    System.out.println("    Confirming update() call...");
    System.out.println("      ShortTrigger.get()...");
    ShortTrigger tr2 = (ShortTrigger)Trigger.get(lp, tr.getId());
    if (tr.getName() .equals (tr2.getName()))
      System.out.println("        yes, update() did work.");
    else
      System.out.println("        NO! update() did NOT work!");
    System.out.println("      post-get update()...");
    tr.setState(Trigger.State.DISABLED);
    tr.update();
    
    System.out.println("    Payoff.setName()...");
    po.setName(po.getName() + " (renamed)");
    System.out.println("    Payoff.update()...");
    po.update();
    System.out.println("    Confirming update() call...");
    System.out.println("      Payoff.get()...");
    Payoff po2 = Payoff.get(lp, po.getId());
    if (po.getName() .equals (po2.getName()))
      System.out.println("        yes, update() did work.");
    else
      System.out.println("        NO! update() did NOT work!");
    System.out.println("      post-get update()...");
    po2.setUrl("http://shopping.hp.com");
    po2.update();
    
    System.out.println("    Link.setName()...");
    ln.setName(ln.getName() + " (renamed)");
    System.out.println("    Link.update()...");
    ln.update();
    System.out.println("    Confirming update() call...");
    System.out.println("      Link.get()...");
    Link ln2 = Link.get(lp, ln.getId());
    if (ln.getName() .equals (ln2.getName()))
      System.out.println("        yes, update() did work.");
    else
      System.out.println("        NO! update() did NOT work!");
    System.out.println("      post-get update()...");
    ln.setName(ln.getName() + " (renamed again)");
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
  private static void testQrCode(LivePaperSession lp) throws LivePaperException, IOException {
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
  private static void testWatermark(LivePaperSession lp) throws LivePaperException, IOException {
    @SuppressWarnings("unused")
    String imageToBeWatermarkedLocalFile  = "Watermarks_20_Euro.jpg";
    String imageToBeWatermarkedUrl        = "http://upload.wikimedia.org/wikipedia/commons/8/82/Watermarks_20_Euro.jpg";
    String imageToBeWatermarked           = imageToBeWatermarkedUrl;
    System.out.println("Creating Watermarked Image...");
    System.out.println("  Image.upload()...");
    System.out.println("    obtaining user image from:");
    System.out.println("      " + imageToBeWatermarked);
    System.out.println("      and uploading to storage service...");
    String uploaded_image_url = null;
    if (imageToBeWatermarked.contains("http"))
      uploaded_image_url = ImageStorage.uploadJpgFromUrl(lp, imageToBeWatermarked);
    else
      uploaded_image_url = ImageStorage.uploadJpgFromFile(lp, imageToBeWatermarked);
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
  private static void testLists(LivePaperSession lp) throws LivePaperException {
    System.out.println("Testing list() methods...");
    System.out.println("  (note: previously created objects were already deleted so you won't see them here)");
    System.out.println("  Getting List of Link objects...");
    Map<String, Link> links = Link.list(lp);
    System.out.println("    Found " + links.keySet().size() + " links.");
    for (String linkId : links.keySet()) {
      Link ln = links.get(linkId);
      System.out.println("      id: \"" + ln.getId()+"\"");
      System.out.println("        name: \"" + ln.getName() + "\"");
      System.out.println("          linked Trigger Id: \"" + ln.getTriggerId() + "\"");
      System.out.println("          linked Payoff Id: \"" + ln.getPayoffId() + "\"");
    }
    System.out.println();
    System.out.println("  Getting List of Trigger objects...");
    Map<String, Trigger> triggers = Trigger.list(lp);
    System.out.println("    Found " + triggers.keySet().size() + " triggers.");
    for (String triggerId : triggers.keySet()) {
      Trigger tr = triggers.get(triggerId);
      System.out.println("      id: \"" + tr.getId()+"\"");
      System.out.println("        name: \"" + tr.getName() + "\", type: " + tr.getClass().getName());
    }
    System.out.println();
    System.out.println("  Getting List of Payoff objects...");
    Map<String, Payoff> payoffs = Payoff.list(lp);
    System.out.println("    Found " + payoffs.keySet().size() + " payoffs.");
    for (String payoffId : payoffs.keySet()) {
      Payoff po = payoffs.get(payoffId);
      System.out.println("      id: \"" + po.getId()+"\"");
      System.out.println("        name: \"" + po.getName() + "\"");
      System.out.println("        type: \"" + po.getType()+"\"");
    }
    System.out.println();
  }
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
}