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
LivePaper lp = LivePaper.auth({id: "your client id", secret: "your client secret"})
```


### Shortening URLs

```java
String short_url = lp.shorten('http://www.google.com')
```


### Generating QR Codes

```java
qr_bytes = lp.qr_bytes('http://www.amazon.com')
```

> Note: Version 1 of the API only supports returning QR Code bytes. Version 2 may host publicly accessible QR images.

### Watermarking Images

```java
wm_bytes = lp.watermark_bytes("http://www.hp.com",
                              "http://www.letsstartsmall.com/ITSE2313_WebAuthoring/images/unit3/jpg_example1.jpg")
```

> Note: Version 1 of the API only supports returning image bytes. Version 2 may host publicly accessible images.
