# LivePaper

Provides a Java interface to the Live Paper service by HP for creating
watermarked images, QR codes, and mobile-friendly shortened URLs.

## Installation

This git repo contains the source for the LivePaper JAR.  You can
build the JAR with "mvn package".  Maven is configured (in pom.xml) to
use Java 1.8 and you will get an error "invalid target release: 1.8"
if maven cannot find Java 1.8.  To fix that problem, you can use the
JAVA_HOME variable to tell Maven where your Java 1.8 is installed (see
script/create_jar_with_maven.sh and script/set_JAVA_HOME).

The latest Maven build of the jar is stored in the target/ directory.
(You may also download the latest released version of the JAR from
link.livepaper.com ["LivePaper
JAR"](https://www.linkcreationstudio.com/api/libraries/java/) page).

## Usage As Documented on LivePaper API Site

For an example of using the latest released version of the JAR, you
can refer to the ["LivePaper
JAR"](https://www.linkcreationstudio.com/api/libraries/java/) page.

## Example Code In the JAR

The JAR itself includes a main() that you can run which will create a
short url, qr code and watermarked image, and output information about
them as they are created.  To run it, just download LivePaper's
dependent jars as shown, and run the main() method of
com.hp.livepaper.LivePaperExample, as shown:

```shell
mvn dependency:resolve
cp="$HOME/.m2/repository/com/sun/jersey/jersey-client/1.18.3/jersey-client-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/com/sun/jersey/jersey-core/1.18.3/jersey-core-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/io/fastjson/boon/0.24/boon-0.24.jar"
java -cp $cp:./target/LivePaper-X.Y.Z.jar com.hp.livepaper.LivePaperExample
```

## Usage Overview

### Authenticate

The Live Paper JAR requires authentication with id and secret. Obtain
your credentials from the registration link above.

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

> Note: Version 1 of the API only supports returning QR Code
> bytes. Version 2 may host publicly accessible QR images.

### Watermarking Images

```java
byte[] wm_bytes = lp.watermark_bytes("http://www.letsstartsmall.com/ITSE2313_WebAuthoring/images/unit3/jpg_example1.jpg","http://www.hp.com");
FileOutputStream fos = new FileOutputStream("wm.jpg");
fos.write(wm_bytes);
fos.close();
```

> Note: Version 1 of the API only supports returning image
> bytes. Version 2 may host publicly accessible images.

## Development

This Java project can be built using Maven.  Install maven and run
"mvn package" to build the library.  The version number is stored in
the Maven pom.xml.

### Working in Eclipse

To make this project visible in Eclipse, open the Eclipse workspace in
which you will do development with Live Paper.  If the project is not
visible, add it with:

* Eclipse menu: 'File=>Import=>Maven/Existing Maven Projects'
  * for the 'root directory', select this directory (containing __this__ project)

You can then develop and debug the JAR.  This is made easier if you
have an 'Run/Debug configurations' in Eclipse's menus.  Add one with:

* Eclipse menu 'Run => Debug Configurations'
  * Add this 'Java Application' configuration with 
    * add one named 'LivePaperExample.main', with main class of 'com.hp.livepaper.LivePaperExample'
