/*
 * This module contains all Wifi-related functions
 * for the CC 3000 chip.
 * 
 */

#include "utility/debug.h"
#include <Adafruit_CC3000.h>
#include <ccspi.h>
#include <SPI.h>
#include <string.h>

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins.  On an UNO, SCK = 13, MISO = 12, and MOSI = 11
Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed but DI
Adafruit_CC3000_Client www;

#define WLAN_SSID       "ZabkarN"        // cannot be longer than 32 characters!
#define WLAN_PASS       "echo2009zzzzz"
#define WLAN_SECURITY   WLAN_SEC_WPA // Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define IDLE_TIMEOUT_MS  10000      // Amount of time to wait (in milliseconds) with no data received before closing the connection.
#define WEBSITE      "jjztsacaapp1.cfapps.io" // What page to grab!
#define WEBSITE_PORT 80
#define WEBPAGE      "/arrivals/csv"
#define NEWLINE      '\n'
#define STATION_PFX  "STATION"
uint32_t ip = 0;
#define DASHES       "\n---\n"

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Adafruit_CC3000_Client &obj, T arg) { obj.fastrprint(arg); return obj; }

char httpContent[200]; // buffer array for data recieve over serial port
unsigned int contentLength = 0; // MAX= 32,767 (2^15 -1 )
unsigned int charsRead = 0; 
unsigned long lastRead;
const unsigned long CONNECT_TIMEOUT_MS = 7000;  //want it to be less than the 8s watchdog timeout


void connectToWebSite(unsigned long loopCounter, unsigned long lastRequestMillis){

//  setOnePixel(0, 255, 0, 255 ); //purple
//  connectToWifiNetwork();

  /* Try connecting to the website.  Note: HTTP/1.1 protocol is used 
     to keep the server from closing the connection before all data is read.   */
  if(Serial) Serial << F("Connecting to server via TCP...       CONNECT_TIMEOUT_MS=") << CONNECT_TIMEOUT_MS;
  //www = cc3000.connectTCP(ip, WEBSITE_PORT);

  // Wait for connection, per: http://arduino.stackexchange.com/a/1755/11505
  lastRead = millis();
  do {
      www = cc3000.connectTCP(ip, WEBSITE_PORT);
  } while((!www.connected()) && ((millis() - lastRead) < CONNECT_TIMEOUT_MS));

  wdt_reset(); 

  if (www.connected()) {
    if(Serial) Serial.println(F(" ... Connected. \nSend HTTP GET request..."));

    //Since operations on Adafruit_CC3000.print* are asynchronous, do delays.
    www.fastrprint(F("GET "));
    delay(5);
    www.fastrprint(WEBPAGE);
    delay(5);
    www.fastrprint(F(" HTTP/1.1\r\nHost: "));
    delay(5);
    www.fastrprint(WEBSITE);
    delay(5);
    www.fastrprint(F("\r\n\r\n"));
    delay(5);
    
    if(Serial) Serial.println(F(" ..sent."));
  } else {
    if(Serial) Serial.println(F("Connection failed\n"));
    disconnectFromWifiNetwork();
    return;
  }

  wdt_reset(); 

  if(Serial) Serial.println(F("Reading data..."));
  setOnePixel(0, 0, 255, 0 ); //green
  contentLength = 0; // reset!
  charsRead = 0;
  lastRead = millis();
  boolean doContinue = true ; 
  /* Read data until either the connection is closed, or the idle timeout is reached. */ 
  while (doContinue && www.connected() && (millis() - lastRead < IDLE_TIMEOUT_MS)) {
    while (doContinue && www.available()) {
      char c = www.read();
      charsRead++;
      wdt_reset(); 
      if(c == NEWLINE){
        String s(httpContent);
        if (s.startsWith(STATION_PFX)){
          processHttpContentString(s);
          // pre-emptively close the stream to see if we can beat 10500ms (falls to ~680ms)
          // PROBLEM: When pre-emptively calling 'www.close()', Arduino stops responding after 1 hour 
          www.close();
          doContinue = false ;
        }
        // reset httpContent to null char
        for(int j = 0; j < contentLength; j++){
          httpContent[j]='\0';  
        }
        contentLength=0;
      }else{
        httpContent[contentLength++] = c;
      }
      lastRead = millis();
    }
  }
  if( Serial && (millis() - lastRead) > IDLE_TIMEOUT_MS ){
    Serial.println(F("lastRead timed out"));
  }
  wdt_reset(); 

}


void doWebClientTest(unsigned long loopCounter, unsigned long lastRequestMillis){
  if(Serial) Serial.println(F("doWebClientTest"));

  connectToWifiNetwork(); 

  wdt_enable(WDTO_8S); // enable watchdog
  
  resolveIPAddress();

  wdt_reset();  // reset watchdog timer
  
  connectToWebSite(loopCounter, lastRequestMillis);

  wdt_reset(); // reset watchdog timer
  
  disconnectFromWifiNetwork();

  wdt_disable(); // reset watchdog timer
  
}


void resolveIPAddress(void){
  if(Serial) Serial.println(F("resolveIpAddress()"));
//  if ((loopCounter % 20) == 0){
//    ip = 0;
//    if(Serial) Serial.println(F("Try looking up the website's IP address by Host Name"));
//  }
//  Serial << WEBSITE << " -> ";
  while (ip == 0) {
    if(Serial) Serial.println(F("\nTry looking up the website's IP address by Host Name"));
    // getHostByName returns positive value on success and negative on failure
    if (! cc3000.getHostByName(WEBSITE, &ip)) {
      if(Serial) Serial << F("\nCouldn't resolve host name ") << WEBSITE;
    }
    wdt_reset(); // reset watchdog timer
    delay(500);
  }
}


void disconnectFromWifiNetwork(void){
  if(Serial) Serial.println(F("disconnectFromWifiNetwork() -- SKIP THAT SHIT"));
  return ;
  
//  if(Serial) Serial.println(F("disconnectFromWifiNetwork()"));
//
//  /* You need to make sure to clean up after yourself or the CC3000 can freak out */
//  /* the next time your try to connect ... */
//  if (www != NULL && www.connected()) {
//    if(Serial) Serial.println(F("close TCP connection..."));
//    www.close();
//    if(Serial) Serial.println(F(" ... TCP socket closed."));   printFreeMemory();
//  } else {
//    if(Serial) Serial.println(F("TCP connection already closed.")); printFreeMemory();
//  }
//
//  if(cc3000.checkConnected()){
//    if(Serial) Serial.println(F("close Wifi connection..."));
//    while(!cc3000.disconnect()){
//      if(Serial) Serial.println(F("  closing Wifi connection..."));
//      delay(500);
//    }
//    if(Serial) Serial.println(F(" ... Wifi connection closed."));   printFreeMemory();
//  } else {
//    if(Serial) Serial.println(F("Wifi connection already closed.")); printFreeMemory();
//  }

}


void connectToWifiNetwork(void){
  if(Serial) Serial.println(F("connectToWifiNetwork"));
  if(cc3000.checkConnected()){
    if(Serial) Serial.println(F("already connected."));
    return ; 
  }else{
    if(Serial) Serial << F("\nAttempting to connect to Wifi... ");
  }
//  char *ssid = WLAN_SSID;             /* Max 32 chars */
//  Serial << "\nAttempting to connect to " << ssid << "... ";
  unsigned long time1 = millis();
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY, 5)) {
    while(1);
  }

  if(Serial) Serial << F("Connected! (") << ( millis() - time1 ) << ("ms)\n");
 
}


void initializeWifi(void){
  
  if(Serial){ 
    Serial.println(F("\nInitialising the CC3000 ... "));
  }
  
  if (!cc3000.begin())
  // 'begin(boolean,boolean)' is for re-using the stored Wifi config.
//  if (!cc3000.begin(false, true))
  {
    while(1);
  }
  setStaticIpAddress();
  if(Serial) Serial.println(F(" ...wifi setup done.\n"));

}


void setStaticIpAddress(void){
  uint32_t ipAddress = cc3000.IP2U32(192, 168, 1, 19);
  uint32_t netMask = cc3000.IP2U32(255, 255, 255, 0);
  uint32_t defaultGateway = cc3000.IP2U32(192, 168, 1, 1);
  uint32_t dns = cc3000.IP2U32(8, 8, 4, 4);
  if (!cc3000.setStaticIPAddress(ipAddress, netMask, defaultGateway, dns)) {
    while(1);
  }
//  Serial << "\nSet Static IP Address to 192.168.1.19";
}



