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
  setOnePixel(0, 255, 0, 0 );  //red
  initializeWifi();
  setOnePixel(0, 218, 218, 0 ); //yellow
  setStaticIpAddress();
  setOnePixel(0, 255, 0, 255 ); //purple
  connectToWifiNetwork();
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  Serial << "\n*** setup() took " << elapsedLoopMillis << "ms ****\r\n"; 
}


void loop(void){
  loopCount++;
  unsigned long startLoopMillis = millis();
  //  connectToWifiNetwork();
  setOnePixel(0, 0, 255, 0 ); //green
  doWebClientTest();
  ledLoop();
  //  disconnectFromWifiNetwork();
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  Serial << "*** loop(" << loopCount << ") took " << elapsedLoopMillis << "ms ****\r\n"; 
}

