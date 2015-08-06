/*
 * This module contains setup() and loop().
 * 
 */

const unsigned long DEFAULT_LOOP_TIME_MILLIS = 29500;
const unsigned long MIN_LOOP_TIME_MILLIS = 5000;
unsigned long loopCount = 0;

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Print &obj, T arg) { obj.print(arg); return obj; }


void setup(void)
{
//  unsigned long startLoopMillis = millis();
//  Serial.begin(115200);
  ledSetup();
  // BEGIN facilitate LED parse testing
//  processHttpContentString("STATION,000000,000000,000000,000000,000000,ff00ff,0000ff,00ff00,ff0000,000000,000000,000000,000000,ff00ff,0000ff,000000,000000,ff00ff,0000ff,daca00,ff0000,000000,000000,000000,000000,");
//  delay(600000);
  // END facilitate LED parse testing
    
  setOnePixel(0, 255, 0, 0 );  //red
  initializeWifi();
  setOnePixel(0, 200, 170, 0 ); //yellow3
  setStaticIpAddress();
  setOnePixel(0, 255, 0, 255 ); //purple
  connectToWifiNetwork();
  setOnePixel(0, 0, 255, 0 ); //green
//  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
//  Serial << "\n*** setup() took " << elapsedLoopMillis << "ms ****\r\n"; 
}

unsigned long elapsedLoopMillis = -1;
void loop(void){  
  loopCount++;
  unsigned long startLoopMillis = millis();
  //  connectToWifiNetwork();
  setOnePixel(0, 200, 170, 0 ); //yellow3; reset to green within connectToWebSite()
  doWebClientTest(loopCount, elapsedLoopMillis);
  //  disconnectFromWifiNetwork();
  elapsedLoopMillis = millis() - startLoopMillis;
  unsigned long sleepTime = min(max(MIN_LOOP_TIME_MILLIS, DEFAULT_LOOP_TIME_MILLIS - elapsedLoopMillis), DEFAULT_LOOP_TIME_MILLIS);
  delay(sleepTime);
//  Serial << "\n*** loop(" << loopCount << ") took " << elapsedLoopMillis << "ms ****"; 
}


