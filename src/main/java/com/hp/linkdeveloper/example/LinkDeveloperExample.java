package com.hp.linkdeveloper.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Scanner;
import com.hp.linkdeveloper.ImageStorage;
import com.hp.linkdeveloper.Link;
import com.hp.linkdeveloper.LinkDeveloperException;
import com.hp.linkdeveloper.LinkDeveloperSession;
import com.hp.linkdeveloper.Payoff;
import com.hp.linkdeveloper.QrTrigger;
import com.hp.linkdeveloper.ShortTrigger;
import com.hp.linkdeveloper.Trigger;
import com.hp.linkdeveloper.WmTrigger;

public class LinkDeveloperExample {
  /*
   * When main() is run, this example client will run a number of tests
   * against the Link Developer service.  The test will expect to find your "client
   * id" and "secret id" credentials (and nothing else) on successive lines
   * of a "mykeys.txt" file in the local directory.
   *
   * To obtain your credentials, you will need to create an account at the
   * LinkDeveloper API site (https://link.LinkDeveloperdeveloper.com), which will
   * then show the credentials to you which you can then copy intto your local
   * "mykeys.txt" file.
   */
  public static void main(String[] args) {
    try {
      System.out.println("Authenticating with LinkDeveloperSession...");
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
        LinkDeveloperSession ld = LinkDeveloperSession.create(id, secret);
        boolean t = true;
        boolean f = false;
        if (t) testShortUrl(ld);
        if (t) testQrCode(ld);
        if (t) testWatermark(ld);
        if (t) testLists(ld);
        System.out.println("All done with tests!");
      }
    }
    catch (Exception e) {
      System.err.print("ERROR: ");
      e.printStackTrace();
      System.exit(1);
    }
  }
  private static void testShortUrl(LinkDeveloperSession ld) throws LinkDeveloperException {
    System.out.println("Creating Short URL...");
    System.out.println("  ShortTrigger.create()...");
    ShortTrigger tr = ShortTrigger.create(ld, "My ShortTrigger");
    System.out.println("    Trigger Name: \"" + tr.getName() + "\"");
    System.out.println("    Trigger Id: \"" + tr.getId() + "\"");
    System.out.println("    Short URL: \"" + tr.getShortUrl() + "\"");
    System.out.println("    Short dateCreated: \"" + tr.getDateCreated() + "\"");
    System.out.println("    Short dateModified: \"" + tr.getDateModified() + "\"");
    System.out.println("    startDate: \"" + tr.getStartDate() + "\"");
    System.out.println("    endDate: \"" + tr.getEndDate() + "\"");
    System.out.println("    Links:");
    for (String item : tr.getLinks().keySet())
      System.out.println("      " + item + ": " + tr.getLinks().get(item));
    System.out.println("  Payoff.create()...");
    Payoff po = Payoff.create(ld, "My Payoff", Payoff.Type.WEB_PAYOFF, "http://www.hp.com");
    System.out.println("    Payoff Name: \"" + po.getName() + "\"");
    System.out.println("    Payoff Id: \"" + po.getId() + "\"");
    System.out.println("    Payoff URL: \"" + po.getId() + "\"");
    System.out.println("    Short dateCreated: \"" + po.getDateCreated() + "\"");
    System.out.println("    Short dateModified: \"" + po.getDateModified() + "\"");
    System.out.println("    Links:");
    for (String item : po.getLinks().keySet())
      System.out.println("      " + item + ": " + po.getLinks().get(item));
    System.out.println("  Link.create()...");
    Link ln = Link.create(ld, "My Link", tr, po);
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
    ShortTrigger tr2 = (ShortTrigger)Trigger.get(ld, tr.getId());
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
    Payoff po2 = Payoff.get(ld, po.getId());
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
    Link ln2 = Link.get(ld, ln.getId());
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
      ln = Link.get(ld, ID);
    }
    catch (LinkDeveloperException e) {
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
  private static void testQrCode(LinkDeveloperSession ld) throws LinkDeveloperException, IOException {
    System.out.println("Creating QR Code...");
    System.out.println("  QrTrigger.create()...");
    QrTrigger tr = QrTrigger.create(ld, "My QrTrigger");
    System.out.println("    Trigger Name: \"" + tr.getName() + "\"");
    System.out.println("    Trigger Id: \"" + tr.getId() + "\"");
    System.out.println("    dateCreated: \"" + tr.getDateCreated() + "\"");
    System.out.println("    dateModified: \"" + tr.getDateModified() + "\"");
    System.out.println("    startDate: \"" + tr.getStartDate() + "\"");
    System.out.println("    endDate: \"" + tr.getEndDate() + "\"");
    System.out.println("    Links:");
    for (String item : tr.getLinks().keySet())
      System.out.println("      " + item + ": " + tr.getLinks().get(item));
    System.out.println("  Payoff.create()...");
    Payoff po = Payoff.create(ld, "My Payoff", Payoff.Type.WEB_PAYOFF, "http://www.hp.com");
    System.out.println("    Payoff Name: \"" + po.getName() + "\"");
    System.out.println("    Payoff Id: \"" + po.getId() + "\"");
    System.out.println("    Payoff URL: \"" + po.getId() + "\"");
    System.out.println("    dateCreated: \"" + po.getDateCreated() + "\"");
    System.out.println("    dateModified: \"" + po.getDateModified() + "\"");
    System.out.println("    Links:");
    for (String item : po.getLinks().keySet())
      System.out.println("      " + item + ": " + po.getLinks().get(item));
    System.out.println("  Link.create()...");
    Link ln = Link.create(ld, "My Link", tr, po);
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
  private static void testWatermark(LinkDeveloperSession ld) throws LinkDeveloperException, IOException {
    @SuppressWarnings("unused")
    String imageToBeWatermarkedLocalFile  = "Watermarks_20_Euro.jpg";
    String imageToBeWatermarkedUrl        = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a0/Ruine_Aggstein_02.JPG/1280px-Ruine_Aggstein_02.JPG";
    String imageToBeWatermarked           = imageToBeWatermarkedUrl;
    System.out.println("Creating Watermarked Image...");
    System.out.println("  Image.upload()...");
    System.out.println("    obtaining user image from:");
    System.out.println("      " + imageToBeWatermarked);
    System.out.println("      and uploading to storage service...");
    String uploaded_image_url = null;
    if (imageToBeWatermarked.contains("http"))
      uploaded_image_url = ImageStorage.uploadJpgFromUrl(ld, imageToBeWatermarked);
    else
      uploaded_image_url = ImageStorage.uploadJpgFromFile(ld, imageToBeWatermarked);
    System.out.println("    uploaded image now available at:");
    System.out.println("      " + uploaded_image_url);
    System.out.println("  WmTrigger.create()...");
    WmTrigger tr = WmTrigger.create(ld, "My WmTrigger");
    System.out.println("    Trigger Name: \"" + tr.getName() + "\"");
    System.out.println("    Trigger Id: \"" + tr.getId() + "\"");
    System.out.println("    dateCreated: \"" + tr.getDateCreated() + "\"");
    System.out.println("    dateModified: \"" + tr.getDateModified() + "\"");
    System.out.println("    startDate: \"" + tr.getStartDate() + "\"");
    System.out.println("    endDate: \"" + tr.getEndDate() + "\"");
    System.out.println("    Links:");
    for (String item : tr.getLinks().keySet())
      System.out.println("      " + item + ": " + tr.getLinks().get(item));
    System.out.println("  Payoff.create()...");
    Payoff po = Payoff.create(ld, "My Payoff", Payoff.Type.WEB_PAYOFF, "http://www.hp.com");
    System.out.println("    Payoff Name: \"" + po.getName() + "\"");
    System.out.println("    Payoff Id: \"" + po.getId() + "\"");
    System.out.println("    Payoff URL: \"" + po.getId() + "\"");
    System.out.println("    dateCreated: \"" + po.getDateCreated() + "\"");
    System.out.println("    dateModified: \"" + po.getDateModified() + "\"");
    System.out.println("    Links:");
    for (String item : po.getLinks().keySet())
      System.out.println("      " + item + ": " + po.getLinks().get(item));
    System.out.println("  Link.create()...");
    Link ln = Link.create(ld, "My Link", tr, po);
    System.out.println("    [Link Name: \"" + ln.getName() + "\"]");
    System.out.println("    [Trigger Id: \"" + ln.getTrigger().getId() + "\"]");
    System.out.println("    Payoff Id: \"" + ln.getPayoff().getId() + "\"");
    System.out.println("    dateCreated: \"" + ln.getDateCreated() + "\"");
    System.out.println("    dateModified: \"" + ln.getDateModified() + "\"");
    System.out.println("    Links:");
    for (String item : ln.getLinks().keySet())
      System.out.println("      " + item + ": " + ln.getLinks().get(item));
    System.out.println("  Adding watermark to image...");
    WmTrigger.Resolution resolution = new WmTrigger.Resolution(75);
    WmTrigger.Strength strength = new WmTrigger.Strength(10);
    byte[] wmbytes = tr.watermarkImage(uploaded_image_url, resolution, strength);
    System.out.println("    strength: \"" + resolution.toString() + "\"");
    System.out.println("    resolution: \"" + strength.toString() + "\"");
    System.out.println("    imageUrl: \"" + uploaded_image_url + "\"");
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
  private static void testLists(LinkDeveloperSession ld) throws LinkDeveloperException {
    System.out.println("Testing list() methods...");
    System.out.println("  (note: previously created objects were already deleted so you won't see them here)");
    System.out.println("  Getting List of Link objects...");
    Map<String, Link> links = Link.list(ld);
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
    Map<String, Trigger> triggers = Trigger.list(ld);
    System.out.println("    Found " + triggers.keySet().size() + " triggers.");
    for (String triggerId : triggers.keySet()) {
      Trigger tr = triggers.get(triggerId);
      System.out.println("      id: \"" + tr.getId()+"\"");
      System.out.println("        name: \"" + tr.getName() + "\", type: " + tr.getClass().getName());
    }
    System.out.println();
    System.out.println("  Getting List of Payoff objects...");
    Map<String, Payoff> payoffs = Payoff.list(ld);
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