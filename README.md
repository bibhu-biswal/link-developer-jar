# LivePaper

Provides a Java interface to the Live Paper service by HP for creating watermarked images, QR codes, and mobile-friendly shortened URLs.

## Installation

?? - Will look into this 

## Register with the Live Paper Service

In order to obtain access credentials register here:  https://link.livepaperdeveloper.com

## Usage

### Authenticate

Live Paper JAR requires an authentication  with id and secret. Obtain your credentials from the registration link above.

```java
LivePaper lp = LivePaper.auth("your client id", "your client secret");
```


### Shortening URLs

```java
String short_url = lp.shorten("http://www.google.com");
```


### Generating QR Codes

```java
byte[] qrbytes = lp.qr_bytes("http://www.amazon.com");
FileOutputStream fos = new FileOutputStream("qrcode.png");
fos.write(qrbytes);
fos.close();
```

> Note: Version 1 of the API only supports returning QR Code bytes. Version 2 may host publicly accessible QR images.

### Watermarking Images

```java
byte[] wm_bytes = lp.watermark_bytes("http://www.letsstartsmall.com/ITSE2313_WebAuthoring/images/unit3/jpg_example1.jpg","http://www.hp.com");
FileOutputStream fos = new FileOutputStream("watermark.jpg");
fos.write(wm_bytes);
fos.close();
```

> Note: Version 1 of the API only supports returning image bytes. Version 2 may host publicly accessible images.
