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
  Serial.begin(115200);
  configureRGBOutputPins();
  setMultiLed74HC595(1,"purple");
  setMultiLed74HC595(2,"purple");
  initializeWifi();
  setStaticIpAddress();
}


void loop(void){
  unsigned long startLoopMillis = millis();
  connectToWifiNetwork();
  doWebClientTest();
  disconnectFromWifiNetwork();
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  unsigned long sleepTime = min(max(MIN_LOOP_TIME_MILLIS, DEFAULT_LOOP_TIME_MILLIS - elapsedLoopMillis), DEFAULT_LOOP_TIME_MILLIS);
  Serial << "*** Sleep for " << sleepTime << "ms ****\r\n"; 
}

