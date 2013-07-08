qrcodepasswordscanner
=====================

QRCode Password Scanner

a simple android app to scan your QR codes and make sure the clipboard is automatically cleared after some seconds.

i created this because other QR Code scanner apps require INTERNET-permissions and sometimes even save a history
of scanned codes. This app just needs CAMERA-Permissions, is simple and does not store anything.

My main purpose is to scan passwords as QR codes (displayed inside KeePassX (see [1]) ) and paste them info some android app.

you will need the ZBar bar code reader libraries for android: http://zbar.sourceforge.net/
copy them into the lib/ directory before compiling

have fun

1 https://github.com/moros/keepassx
