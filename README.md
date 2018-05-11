# flutter-nfc-app

flutter demo application to connect IsoDep iso 14443-4 cards via platform specific android nfc code

only android nfc functionality is added. 

Mifare Desfire free getVersion command reading is implemented. 

Some experiments:
At each call to MainActivity via method channel, onResume is called at Mainactivity side, so no new activity created( and onNewIntent is not called) so contactless card connection state is preserved, this is very good. so you can call as much as you want the MainActivity side and send/receive apdu commands with card without loosing the card connection and card's state. For example send/receive some commands via card then send this command responses to host via dart code web service and send the returned commands to card again. So the card can be managed remotely from host without problem with flutter. This is good to know.

## Getting Started

For help getting started with Flutter, view our online
[documentation](https://flutter.io/).
