/*
 * This module contains all business logic.
 * 
 */

void processHttpContentString(String s){
//    Serial << "\n  s='" << s << "'";
  if (s.startsWith("STATION")){
    Serial << "\n  STATION='" << s << "'";
    int comma1Index = s.indexOf(',');
    //  Search for the next comma just after the first
    int comma2Index = s.indexOf(',', comma1Index + 1 );
    int comma3Index = s.indexOf(',', comma2Index + 1 );
    int comma4Index = s.indexOf(',', comma3Index + 1 );
    // 1st field (STATION) ignore
    // 2nd field (stationId) ignore
    // get 3rd field LED-1
    setMultiLed74HC595(1, s.substring(comma2Index + 1,comma3Index) );
    // get 4rd field LED-2
    setMultiLed74HC595(2, s.substring(comma3Index + 1,comma4Index) );
  }
}

