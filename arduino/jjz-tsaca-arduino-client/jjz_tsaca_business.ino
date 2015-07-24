/*
 * This module contains all business logic.
 * 
 */
void processHttpContent(char* c, int i){
      String s(c);
    Serial << "\n  s='" << s << "'";
}

void processHttpContentString(String s){
    Serial << "\n  s='" << s << "'";
  if (s.startsWith("STATION")){
    Serial << "\n  STATION TRUE";
  }
}

