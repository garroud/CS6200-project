import org.json.simple.JSONObject;

import org.json.simple.parser.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class BusinessModifier {

  BusinessModifier(){}

  public void modify(String fileName, String outFile) throws Exception{

    // parsing file "JSONExample.json"
    BufferedReader f = new BufferedReader(new FileReader(fileName));
    JSONParser jsonParser = new JSONParser();
    String line;
    PrintWriter w = new PrintWriter(outFile);
    while ((line = f.readLine()) != null) {
      JSONObject jo = (JSONObject) jsonParser.parse(line);
      JSONObject jo_output = new JSONObject();
      String business_id = (String) jo.get("business_id");
      String name = (String) jo.get("name");
      String address = (String) jo.get("address");
      String city = (String) jo.get("city");
      String state = (String) jo.get("state");
      String postal_code = (String) jo.get("postal_code");
      Double latitude = (Double) jo.get("latitude");
      Double longitude = (Double) jo.get("longitude");
      Double stars = (Double) jo.get("stars");
      long review_count = (Long) jo.get("review_count");
      long is_open = (Long) jo.get("is_open");
      jo_output.put("business_id", business_id);
      jo_output.put("name", name);
      jo_output.put("address", address);
      jo_output.put("city", city);
      jo_output.put("state", state);
      jo_output.put("postal_code", postal_code);
      jo_output.put("latitude", latitude);
      jo_output.put("longitude", longitude);
      jo_output.put("stars", stars);
      jo_output.put("review_count", review_count);
      jo_output.put("is_open", is_open);
      w.write(jo_output.toJSONString());
      w.write("\n");
      w.flush();
    }
    w.close();
  }
}
