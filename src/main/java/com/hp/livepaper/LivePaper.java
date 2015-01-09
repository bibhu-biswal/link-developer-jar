package com.hp.livepaper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.json.JsonFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import org.boon.json.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Provides a Java interface to the Live Paper service by HP for
 * creating watermarked images, QR codes, and mobile-friendly 
 * shortened URLs. 
 */
public abstract class LivePaper {
    protected final String LP_API_HOST = "https://www.livepaperapi.com";
    /**
     * Authorize the client. 
     * @param clientID The clientID provided in the access credentials
     * @param secret The client secret provided in the access credentials
     * @return An authorized instance of LivePaper that allows access to Live Paper services 
     *         or null if authorization fails. 
     */
    public static LivePaper auth(String clientID, String secret) throws java.io.UnsupportedEncodingException {
        if (clientID == null || secret == null)
            throw new NullPointerException("Null arguments not accepted.");
        LivePaper lp = new LivePaperSession();
        lp.authorize(clientID, secret);
        return lp;
    }
    /**
     * Shortens the url passed as the argument. 
     * @param longURL The URL that needs to be shortened.
     * @return The shortened URL or null if passed string is null, or if access is unauthorized, or in case of server error.    
     */
    public abstract String shorten(String longURL);
    /**
     * Returns a byte representation of the QR code that encodes the passed URL.
     * @param url The URL that needs to be QR-coded. 
     * @return The byte representation of the QR code or null if passed string is null, or if access is unauthorized, or in case of server error.    
     */
    public abstract byte[] qr_bytes(String url);
    /**
     * Returns a byte representation of the watermarked image that encodes the passed URL.
     * @param imageLoc The the URL where the image is hosted
     * @param url The URL that needs to be encoded in the image 
     * @return The byte representation of the watermarked image or null if passed string is null, or if access is unauthorized, or in case of server error.    
     * @throws LivePaperException 
     */
    public abstract byte[] watermark_bytes(String imageLoc, String url) throws LivePaperException;
    protected abstract void authorize(String clientID, String secret) throws java.io.UnsupportedEncodingException;  
    static class LivePaperSession extends LivePaper {
        /**
         * Shortens the url passed as the argument. 
         * @param longURL The URL that needs to be shortened.
         * @return The shortened URL or null if passed string is null, or if access is unauthorized, or in case of server error.    
         */
        public String shorten(String longURL)
        {
            if (com.hp.livepaper.LivePaperSession.getLppAccessToken() == null)
                return null;
            if(longURL == null)
                return null;    

            return createLink("shorturl", longURL, "shortURL", null, null);
        }
        /**
         * Returns a byte representation of the QR code that encodes the passed URL.
         * @param url The URL that needs to be QR-coded. 
         * @return The byte representation of the QR code or null if passed string is null, or if access is unauthorized, or in case of server error.    
         */
        @GET
        public byte[] qr_bytes(String url) {
            if (com.hp.livepaper.LivePaperSession.getLppAccessToken() == null)
                return null;
            if(url == null)
                return null;
            String location = createLink("qrcode", url, "image", null, null) + "?width=200";
            WebResource webResource = com.hp.livepaper.LivePaperSession.createWebResource(location);      
            ClientResponse response =  webResource.
                accept("image/png").
                header("Authorization", com.hp.livepaper.LivePaperSession.getLppAccessToken()).
                get(ClientResponse.class);
            byte[] bytes;
            try {
                bytes = inputStreamToByteArray(response.getEntityInputStream());
            }
            catch(IOException e)
            {
                return null;
            }
            return bytes;
        }
        /**
         * Returns a byte representation of the watermarked image that encodes the passed URL.
         * @param imageLoc The the URL where the image is hosted
         * @param url The URL that needs to be encoded in the image 
         * @return The byte representation of the watermarked image or null if passed string is null, or if access is unauthorized, or in case of server error.    
         * @throws LivePaperException 
         */
        @GET
        public byte[] watermark_bytes(String imageLoc, String url) throws LivePaperException {
            if (com.hp.livepaper.LivePaperSession.getLppAccessToken() == null)
                return null;
            if(imageLoc == null || url == null)
                return null;
            String image;
            try {
                image = img_upload(imageLoc);
            }
            catch(IOException e)
            {
                return null;
            }
            Map<String, String> watermark = new HashMap<String, String>();
            watermark.put("imageURL",image);        
            String location = createLink("watermark", url, "image", watermark, "watermark");
            WebResource webResource = com.hp.livepaper.LivePaperSession.createWebResource(location);  
            ClientResponse response =  webResource.
                header("Authorization", com.hp.livepaper.LivePaperSession.getLppAccessToken()).
                accept("image/jpeg").
                get(ClientResponse.class);
            byte[] bytes;
            try {
                bytes = inputStreamToByteArray(response.getEntityInputStream());
            }
            catch(IOException e)
            {
                return null;
            }
            return bytes;
        }
        //////////////////////////////////// Private helper methods //////////////////////////////////////
        /* Upload the image to livepaper storage */
        @POST
        private String img_upload(String imageLoc) throws IOException, LivePaperException   {
            String url = "https://storage.livepaperapi.com/objects/v1/files";
            if(imageLoc.contains(url))
                return imageLoc;
            //get the image bytes from the image hosting website, and upload the image on livepaper storage
            WebResource source = com.hp.livepaper.LivePaperSession.createWebResource(imageLoc);
            ClientResponse imgResponse = null;
            try {
              imgResponse =  source.accept("image/jpg").get(ClientResponse.class);
            } catch ( com.sun.jersey.api.client.ClientHandlerException e ) {
              throw new LivePaperException("Unable to obtain image from \""+imageLoc+"\"!", e);
            }
            byte[] bytes = inputStreamToByteArray(imgResponse.getEntityInputStream());
            WebResource webResource = com.hp.livepaper.LivePaperSession.createWebResource(url);       
            ClientResponse response = webResource.
                header("Content-Type", "image/jpg").
                header("Authorization", com.hp.livepaper.LivePaperSession.getLppAccessToken()).
                post(ClientResponse.class, bytes);
            return response.getHeaders().getFirst("location");
        }
        @POST
        protected void authorize(String clientID, String secret) throws java.io.UnsupportedEncodingException {
          com.hp.livepaper.LivePaperSession.setLppBasicAuth(clientID, secret);
        }
        private byte[] inputStreamToByteArray(InputStream is) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int next = is.read();
            while (next > -1) {
                bos.write(next);
                next = is.read();
            }
            bos.flush();
            return bos.toByteArray();
        }
        private Map<String, Object> trigger(String type, Map<String, String> options, String optionName) {
            Map<String, Object> body = new HashMap<String, Object>();
            Map<String, Object> trigger = new HashMap<String, Object>();
            trigger.put("type", type);
            trigger.put("name", "trigger");
            if (options != null) {
                trigger.put(optionName, options);
            }
            body.put("trigger", trigger);
            return create_resource("trigger", body);
        }
        private Map<String, Object> url_payoff(String longURL) {
            Map<String, Object> body = new HashMap<String, Object>();
            Map<String, String> payoff = new HashMap<String, String>();
            payoff.put("URL", longURL);
            payoff.put("name", "payoff");
            body.put("payoff", payoff);
            return create_resource("payoff", body);
        }
        private Map<String, Object> link(String triggerID, String payoffID) {
            Map<String, Object> body = new HashMap<String, Object>();
            Map<String, String> link = new HashMap<String, String>();
            link.put("triggerId", triggerID);
            link.put("payoffId", payoffID);
            link.put("name", "link");
            body.put("link", link);
            return create_resource("link", body);
        }
        @POST
        @SuppressWarnings("unchecked")
        private Map<String, Object> create_resource(String resource, Map<String, Object> bodyMap) {
          int maxTries = com.hp.livepaper.LivePaperSession.getNetworkErrorRetryCount();
          int tries = 0;
          while ( true ) {
            try {
              String HostURL = LP_API_HOST + "/api/v1/" + resource+"s";
              ObjectMapper mapper = JsonFactory.create();
              String body = mapper.writeValueAsString(bodyMap);
              WebResource webResource = com.hp.livepaper.LivePaperSession.createWebResource(HostURL);
              ClientResponse response = webResource.
                  header("Content-Type", "application/json").
                  accept("application/json").
                  header("Authorization", com.hp.livepaper.LivePaperSession.getLppAccessToken()).
                  post(ClientResponse.class, body);
              int responseCode = response.getStatus();
              if(responseCode == 201) {   
                  Map<String, Object> responseMap = mapper.readValue(response.getEntity(String.class), Map.class);
                  return (Map<String, Object>)responseMap.get(resource);
              } else {
                System.out.println(responseCode);
                System.out.println(response.getEntity(String.class));
              }
              return null;
            }          
            catch ( com.sun.jersey.api.client.ClientHandlerException e ) {
              tries++;
              if ( tries > maxTries )
                throw e;
              System.err.println("Warning: Network error! retrying ("+tries+" of "+maxTries+")...");
              System.err.println("  (error was \""+e.getMessage()+"\")");
              try { Thread.sleep(com.hp.livepaper.LivePaperSession.getRetrySleepPeriod()); } catch (InterruptedException e1) { throw e; }
              continue;
            }
          }
        }   
        @SuppressWarnings("unchecked")
        private String createLink(String triggerType, String longURL, String type, Map<String, String> opts, String optName) {
            Map<String, Object> trig = trigger(triggerType, opts, optName);
            Map<String, Object> payoff = url_payoff(longURL);
            link((String)trig.get("id"), (String)payoff.get("id"));
            List<Map<String, String>> listLink = (List<Map<String,String>>) trig.get("link");
            for(Map<String, String> map: listLink) {
                if(map.get("rel").equals(type))
                    return map.get("href");
            }
            return null;
        }
    }
}
//Java.net.SocketException: Network is unreachable [LivePaper$LivePaperSession.create_resource()]