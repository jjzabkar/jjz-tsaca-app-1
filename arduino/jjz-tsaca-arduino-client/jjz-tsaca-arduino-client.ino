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
  configureRGBOutputPins();
  setMultiLed74HC595(1,"purple");
  setMultiLed74HC595(2,"purple");
  initializeWifi();
  setStaticIpAddress();
  connectToWifiNetwork();
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  Serial << "\n*** setup() took " << elapsedLoopMillis << "ms ****\r\n"; 
}


void loop(void){
  loopCount++;
  unsigned long startLoopMillis = millis();
  //  connectToWifiNetwork();
  doWebClientTest();
  //  disconnectFromWifiNetwork();
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  Serial << "*** loop(" << loopCount << ") took " << elapsedLoopMillis << "ms ****\r\n"; 
}

