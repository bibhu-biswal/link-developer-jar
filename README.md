# LivePaper

Provides a Java interface to the Live Paper service by HP for creating watermarked images, QR codes, and mobile-friendly shortened URLs.

## Installation

This git repo contains the source for the LivePaper JAR.  You can
build the JAR with "mvn package".  The latest build of the jar is also
stored (by Maven) in the target/ directory.  You may also download the
latest released version of the JAR from link.livepaper.com ["LivePaper
JAR"](https://www.linkcreationstudio.com/api/libraries/java/) page.

## Usage As Documented on LivePaper API Site

For an example of using the JAR, you can refer to the ["LivePaper
JAR"](https://www.linkcreationstudio.com/api/libraries/java/) page
which describes how to use the JAR.

## Example Code In the JAR

The JAR itself includes a main() that you can run which will
create some links.  Just build the JAR with maven (which will
download the required dependent JARs to your ~/.m2 maven repository
directory) and run the main() method of com.hp.LivePaperExample, like
this:

```shell
mvn package
cp="$HOME/.m2/repository/com/sun/jersey/jersey-client/1.18.3/jersey-client-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/com/sun/jersey/jersey-core/1.18.3/jersey-core-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/io/fastjson/boon/0.24/boon-0.24.jar"
java -cp $cp:./target/LivePaper-0.0.4.jar com.hp.LivePaperExample
```

## Usage Overview

### Authenticate

The Live Paper JAR requires authentication with id and secret. Obtain your credentials from the registration link above.

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
FileOutputStream fos = new FileOutputStream("wm.jpg");
fos.write(wm_bytes);
fos.close();
```

> Note: Version 1 of the API only supports returning image bytes. Version 2 may host publicly accessible images.

## Development

This Java project can be built using Maven.  Install maven and run "mvn
package" to build the library.   The version number is stored in the Maven
pom.xml file (and also, historically, in the LivePaper.java file, in the
comments).

### Working in Eclipse

To make this project visible in Eclipse, open the Eclipse workspace in which
you will do development with Live Paper.  If the project is not visible, add
it with:

* Eclipse menu: 'File=>Import=>Maven/Existing Maven Projects'
  * then for the 'root directory', select this directory (containing __this__ project)

To make the 'Maven Goals' visible in the 'Run/Debug configurations' of Eclipse's menus:

* Eclipse menu 'Run => Debug Configurations'
  * Then add these 'Maven Build' configurations with a
    'Base directory' of '${workspace_loc:/LivePaper}'
    * add one named 'jar - compile', with goal 'compile'
    * add one named 'jar - package', with goal 'package'
