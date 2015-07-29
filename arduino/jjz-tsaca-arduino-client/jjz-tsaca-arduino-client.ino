/*
 * This module contains setup() and loop().
 * 
 */

const unsigned long DEFAULT_LOOP_TIME_MILLIS = 60000;
const unsigned long MIN_LOOP_TIME_MILLIS = 5000;
unsigned long loopCount = 0;

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Print &obj, T arg) { obj.print(arg); return obj; }


void setup(void)
{
  unsigned long startLoopMillis = millis();
  Serial.begin(115200);
  ledSetup();
  /*
  setOnePixel(0, 255, 0, 0 );  //red
  initializeWifi();
  setOnePixel(0, 218, 218, 0 ); //yellow
  setStaticIpAddress();
  setOnePixel(0, 255, 0, 255 ); //purple
  connectToWifiNetwork();
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  Serial << "\n*** setup() took " << elapsedLoopMillis << "ms ****\r\n"; 
  */

  String s1 = "STATION,000000,000000,000000,000000,000000,000000,000000,000000,ff00ff,00ff00,00ff00,00ff00,00ff00,dada00,ff0000,ff00ff,dada00,ff0000,ff00ff,00ff00,ff0000,000000,000000,000000,000000,";
  processHttpContentString(s1);
}


void loop(void){
  delay(30000);
  
  /*
  loopCount++;
  unsigned long startLoopMillis = millis();
  //  connectToWifiNetwork();
  setOnePixel(0, 0, 255, 0 ); //green
  doWebClientTest();
  ledLoop();
  //  disconnectFromWifiNetwork();
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  Serial << "*** loop(" << loopCount << ") took " << elapsedLoopMillis << "ms ****\r\n"; 
  */
}

