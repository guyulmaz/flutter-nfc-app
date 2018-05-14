import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';

void main() {
  //runApp(new MyApp());
  runApp(new MaterialApp(home: new PlatformChannel()));
}

class PlatformChannel extends StatefulWidget {
  @override
  _PlatformChannelState createState() => new _PlatformChannelState();
}

class _PlatformChannelState extends State<PlatformChannel> {
  static const MethodChannel methodChannel =
//  const MethodChannel('samples.flutter.io/battery');
      const MethodChannel('nfc');

  //static const EventChannel eventChannel =
  //const EventChannel('samples.flutter.io/charging');

  String _cardUid = 'card uid unknown.';
  String _getVersion1 = 'getVersion1 init';
  String _getVersion2 = 'getVersion2 init';
  String _getVersion3 = 'getVersion3 init';
  String _getVersionAll = 'getVersionAll init';

  //String _chargingStatus = 'Battery status: unknown.';

  Future<Null> _getCardUID() async {
    String response;
    try {
//      final int result = await methodChannel.invokeMethod('getBatteryLevel');
      final String result = await methodChannel.invokeMethod('getCardUID');
      if (result != null) {
        response = 'Get UID: $result';
      } else {
        response = 'Get UID failed';
      }
    } on PlatformException {
      response = 'Get UID failed, PlatformException';
    }
    setState(() {
      _cardUid = response;
    });
  }

  Future<Null> _getVersionMethod(String cmd, int turn) async {
    String response;
    try {
//      final int result = await methodChannel.invokeMethod('getBatteryLevel');
      final String result =
          await methodChannel.invokeMethod('getVersion', <String, dynamic>{
        'commands': cmd,
      });
      if (result != null) {
        response = 'getVersion: $result';
        print(response);
      } else {
        response = 'getVersion failed';
      }
    } on PlatformException {
      response = 'getVersion failed, PlatformException';
    }
    setState(() {
      if (turn == 1) {
        _getVersion1 = response;
      } else if (turn == 2) {
        _getVersion2 = response;
      } else if (turn == 3) {
        _getVersion3 = response;
      } else if (turn == 4) {
        _getVersionAll = response;
      }
    });
  }

//  @override
//  void initState() {
//    super.initState();
//    //eventChannel.receiveBroadcastStream().listen(_onEvent, onError: _onError);
//  }
//
//  void _onEvent(Object event) {
//    setState(() {
//      //_chargingStatus =
//      //"Battery status: ${event == 'charging' ? '' : 'dis'}charging.";
//    });
//  }
//
//  void _onError(Object error) {
//    setState(() {
//      //_chargingStatus = 'Battery status: unknown.';
//    });
//  }

  String getVersionArray = "9060000000#90AF000000#90AF000000";

  @override
  Widget build(BuildContext context) {
    return new Material(
      child: new Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: <Widget>[
          new Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              new Text(_cardUid, key: const Key('get uid label')),
              new Padding(
                padding: const EdgeInsets.all(16.0),
                child: new RaisedButton(
                  child: const Text('get uid'),
                  onPressed: _getCardUID,
                ),
              ),
              new Text(_getVersion1, key: const Key('getversion1 label')),
              new Padding(
                padding: const EdgeInsets.all(16.0),
                child: new RaisedButton(
                  child: const Text('getVersionPart1'),
                  onPressed: () => _getVersionMethod("9060000000", 1),
                ),
              ),
              new Text(_getVersion2, key: const Key('getversion2 label')),
              new Padding(
                padding: const EdgeInsets.all(16.0),
                child: new RaisedButton(
                  child: const Text('getVersionPart2'),
                  onPressed: () => _getVersionMethod("90AF000000", 2),
                ),
              ),
              new Text(_getVersion3, key: const Key('getversion3 label')),
              new Padding(
                padding: const EdgeInsets.all(16.0),
                child: new RaisedButton(
                  child: const Text('getVersionPart3'),
                  onPressed: () => _getVersionMethod("90AF000000", 3),
                ),
              ),
              new Text(_getVersionAll, key: const Key('getversionAll label')),
              new Padding(
                padding: const EdgeInsets.all(16.0),
                child: new RaisedButton(
                  child: const Text('getVersionAll'),
                  onPressed: () => _getVersionMethod(getVersionArray, 4),
                ),
              )
            ],
          ),
          //new Text(_chargingStatus),
        ],
      ),
    );
  }
}
