#!/bin/sh

keyfile="../playground/lpapi_exemplar_java/mykeys.txt"
script="../playground/lpapi_exemplar_java/TestLinkAPI.sh"
rm -f url qr_code.png watermark.jpg
if [ ! -f "$script" ];then
  echo "ERROR: test script not found: $script"
  exit 1
fi
$script -k $keyfile -s http://en.wikipedia.org/wiki/URL_shortening | tee url || exit 1
$script -k $keyfile -q http://en.wikipedia.org/wiki/QR_code -o qr_code.png || exit 1
$script -k $keyfile -w http://en.wikipedia.org/wiki/Watermark -o watermark.jpg \
        -i http://upload.wikimedia.org/wikipedia/commons/8/82/Watermarks_20_Euro.jpg || exit 1
open $(tail -1 url)