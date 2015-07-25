/*
 * This module contains all Wifi-related functions
 * for the CC 3000 chip.
 * 
 */
 
#include <Adafruit_CC3000.h>
#include <ccspi.h>
#include <SPI.h>
#include <string.h>
//#include "utility/debug.h"

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins.  On an UNO, SCK = 13, MISO = 12, and MOSI = 11
Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed but DI

#define WLAN_SSID       "ZabkarN"        // cannot be longer than 32 characters!
#define WLAN_PASS       "echo2009zzzzz"
#define WLAN_SECURITY   WLAN_SEC_WPA // Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define IDLE_TIMEOUT_MS  3000      // Amount of time to wait (in milliseconds) with no data received before closing the connection.
#define WEBSITE      "jjztsacaapp1.cfapps.io" // What page to grab!
#define WEBSITE_PORT 80
#define WEBPAGE      "/arrivals/csv"
uint32_t ip = 0;
//#define WEBSITE      "192.168.1.12"
//#define WEBSITE_PORT   8080
//#define WEBPAGE      "/arrivals/test"
#define DASHES       "\n----------------------------------------------\n"

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Adafruit_CC3000_Client &obj, T arg) { obj.fastrprint(arg); return obj; }


void connectToWebSite(void){
  /* Try connecting to the website.  Note: HTTP/1.1 protocol is used 
     to keep the server from closing the connection before all data is read.   */
  Serial << DASHES << "Connecting...\n";
  Adafruit_CC3000_Client www = cc3000.connectTCP(ip, WEBSITE_PORT);
  Serial << "GET http://"  << WEBSITE << WEBPAGE << "\n";
  if (www.connected()) {
    www << "GET " << WEBPAGE << " HTTP/1.1\r\n" << "Host: " << WEBSITE << "\r\n\r\n";
    www.println();
  } else {
    Serial << "Connection failed\n";
    return;
  }
  Serial << DASHES << "Reading data...\n";
  unsigned long lastRead = millis();
  /* Read data until either the connection is closed, or the idle timeout is reached. */ 
  while (www.connected() && (millis() - lastRead < IDLE_TIMEOUT_MS)) {
    while (www.available()) {
      //https://www.arduino.cc/en/Reference/StreamReadStringUntil
      String str = www.readStringUntil('\n');
      //TODO: hold off on actioning LEDs until stream read complete
      processHttpContentString(str);
      lastRead = millis();
    }
  }
  www.close();
  Serial << "\nDone Reading data." << DASHES;
}


void doWebClientTest(void){
  if ((loopCount % 20) == 0){
    ip = 0;
    Serial << "Try looking up the website's IP address by Host Name\n";
  }
  Serial << WEBSITE << " -> ";
  while (ip == 0) {
    if (! cc3000.getHostByName(WEBSITE, &ip)) {
      //Serial.println(F("Couldn't resolve!"));
    }
    delay(500);
  }
  cc3000.printIPdotsRev(ip);

  connectToWebSite();

}


void disconnectFromWifiNetwork(void){
  Serial << "\nDisconnecting... ";
  /* You need to make sure to clean up after yourself or the CC3000 can freak out */
  /* the next time your try to connect ... */
  cc3000.disconnect();
  Serial << " ...Disconnected.\n";
}


void connectToWifiNetwork(void){
  char *ssid = WLAN_SSID;             /* Max 32 chars */
  Serial << "\nAttempting to connect to " << ssid << "... ";
  unsigned long time1 = millis();
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY)) {
    Serial << "Failed!\n";
    while(1);
  }
  Serial << " ... Requesting DHCP... ";
  while (!cc3000.checkDHCP())
  {
    Serial << ".";
    delay(100); // ToDo: Insert a DHCP timeout!
  }  
  Serial << " ... Connected! (" << ( millis() - time1 ) << "ms)\n";
}


void initializeWifi(void){
  Serial << "\nInitialising the CC3000 ... ";
  if (!cc3000.begin())
  {
    Serial << "\nUnable to initialise the CC3000! Check your wiring?\n";
    while(1);
  }
  Serial << " ...done.\n";
}


void setStaticIpAddress(void){
  uint32_t ipAddress = cc3000.IP2U32(192, 168, 1, 19);
  uint32_t netMask = cc3000.IP2U32(255, 255, 255, 0);
  uint32_t defaultGateway = cc3000.IP2U32(192, 168, 1, 1);
  uint32_t dns = cc3000.IP2U32(8, 8, 4, 4);
  if (!cc3000.setStaticIPAddress(ipAddress, netMask, defaultGateway, dns)) {
    Serial << "\nFailed to set static IP!\n";
    while(1);
  }
  Serial << "\nSet Static IP Address to 192.168.1.19";
}

