# Link Developer JAR

Provides a Java interface to the "Link" service by HP for creating
watermarked images, QR codes, and mobile-friendly shortened URLs.

## Building

This git repo contains the source for the Link Developer JAR.  You can
build the JAR with "mvn package".  Maven is configured (in pom.xml) to
use Java 1.8.

If you are not using an earlier version of Java than 1.8, you will get
an error "invalid target release: 1.8" from maven.  If you have Java
1.8 installed, and need to tell maven where it is, use the JAVA_HOME
variable.  (edit the script script/set_JAVA_HOME and then load
it with "source script/set_JAVA_HOME").

The latest Maven build of the jar is stored in the target/ directory.
(You may also download the latest released version of the JAR from
www.linkcreationstudio.com's ["Link Developer
JAR"](https://www.linkcreationstudio.com/api/libraries/java/) page).

## Usage As Documented on Link Developer API Site

For an example of using the latest released version of the JAR, you
can refer to the sample code snippets on the ["Link Developer
JAR"](https://www.linkcreationstudio.com/api/libraries/java/) page.

## Example Code In the JAR

The JAR itself includes an example class that you can inspect or run
to see how to create Trigger, Payoff and Link objects to create short
urls, qr codes and watermarked images.  To run it, just download
LinkDeveloper's dependent jars as shown, and run the main() method of
com.hp.linkdeveloper.example.LinkDeveloperExample, as shown:

```shell
mvn dependency:resolve
cp="$HOME/.m2/repository/com/sun/jersey/jersey-client/1.18.3/jersey-client-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/com/sun/jersey/jersey-core/1.18.3/jersey-core-1.18.3.jar"
cp="$cp:$HOME/.m2/repository/io/fastjson/boon/0.24/boon-0.24.jar"
java -cp $cp:./target/linkdeveloper-X.Y.Z.jar com.hp.linkdeveloper.example.LinkDeveloperExample
```

## Usage Overview

### Authenticate

The Link Developer JAR requires authentication with two keys: your id key
and secret key.  These are shown in the green box on the [Link Developer Authentication](https://www.linkcreationstudio.com/developer/doc/auth/) page.


```java
LinkDeveloperSession ld = LinkDeveloperSession.create("your client id", "your client secret");
```

### Shortening URLs

```java
String short_url = ld.createShortUrl("Short URL to Google", "http://www.google.com");
```

### Generating QR Codes

```java
byte[] qrBytes = ld.createQrCode("QR Code for Amazon", "http://www.amazon.com", 200 /* width in pixels */);
FileOutputStream fos = new FileOutputStream("qrcode.png");
fos.write(qrBytes);
fos.close();
```

> Note: Version 1 of the API only supports returning QR Code
> bytes. A future version may host publicly accessible QR images.

### Watermarking Images
#### Applying watermark to a remotely hosted image file
```java
byte[] wm_bytes = ld.createWatermarkedJpgImage("Watermark link to HP", 
                                               new WmTrigger.Strength(7),
                                               new WmTrigger.Resolution(72),
                                               "http://www.letsstartsmall.com/ITSE2313_WebAuthoring/images/unit3/jpg_example1.jpg","http://www.hp.com",
                                               "http://www.hp.com");
FileOutputStream fos = new FileOutputStream("wm.jpg");
fos.write(wm_bytes);
fos.close();
```
#### Applying watermark to a local image file
```java
byte[] wm_bytes = ld.createWatermarkedJpgImage("Watermark link to HP", 
                                               new WmTrigger.Strength(7),
                                               new WmTrigger.Resolution(72),
                                               "/path/to/local/image/file",
                                               "http://www.hp.com");
FileOutputStream fos = new FileOutputStream("wm.jpg");
fos.write(wm_bytes);
fos.close();
```

> Note: Version 1 of the API only supports returning image
> bytes. A future version may host publicly accessible images.

## Development

This Java project can be built using Maven.  Install maven and run
"mvn package" to build the library.  The version number is stored in
the Maven pom.xml.

### Working in Eclipse

To make this project visible in Eclipse, open the Eclipse workspace in
which you will do development with Link Developer.  If the project is not
visible, add it with:

* Eclipse menu: 'File=>Import=>Maven/Existing Maven Projects'
  * for the 'root directory', select this directory (containing __this__ project)

You can then develop and debug the JAR.  This is made easier if you
have an 'Run/Debug configurations' in Eclipse's menus.  Add one with:

* Eclipse menu 'Run => Debug Configurations'
  * Add this 'Java Application' configuration with 
    * add one named 'LinkDeveloperExample.main', with main class of 'com.hp.linkdeveloper.example.LinkDeveloperExample'