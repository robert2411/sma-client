package com.github.robert2411.sma.smasunnyboy15;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.robert2411.sma.smasunnyboy15.SmaSunnyBoy15Constants.PV_WATT;


public class SmaSunnyBoy15Client {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public SmaSunnyBoy15Client(String ip, String pass) {
        this.baseUrl = "http://" + ip +"/dyn/";
        this.pass = pass;
    }

    private final String baseUrl;
    private final String pass;

    private static final String USER="usr";
    private static final String SID_ENDPOINT ="login.json";
    private static final String valuesEndpoint = "getValues.json?sid={{SID}}";



    Optional<String> getPayload(){

        String body = "{\"destDev\":[],\"keys\":[\"6400_00260100\",\"6400_00262200\",\""+PV_WATT+"\"]}";
        String endpoint = getSID().map(s -> valuesEndpoint.replace("{{SID}}", s)).orElseThrow();
        var temp = post(endpoint, Collections.emptyMap(), body).map(m -> toMap(m));
        System.out.println(temp);
        return post(endpoint, Collections.emptyMap(), body);
    }

    Optional<String> getSID() {



        return post(SID_ENDPOINT, Collections.emptyMap(), "{\"pass\" : \"" + pass + "\", \"right\" : \""+ USER+"\"}")
                .map(this::toMap)
                .filter(m -> m.containsKey("result"))
                .map(m -> m.get("result"))
                .filter(o -> o instanceof Map)
                .map(o -> (Map)o)
                .filter(m -> m.containsKey("sid"))
                .map(m -> m.get("sid").toString());


    }

    private Optional<String> post(String endpoint, Map<String,String> headers, String body){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            return Optional.ofNullable(response.body());
        } catch (IOException e) {
            log.error("",e);
            return Optional.empty();
        } catch (InterruptedException e) {
            log.error("",e);
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    private Map<String, Object> toMap(String raw){
        try {
                    return new ObjectMapper().readValue(raw, HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}

//        def onHeartbeat(self):
//        Domoticz.Log("onHeartbeat called "+ str(self.lastPolled))
//        ## Read SMA Inverter ##
//        url_base="http://" + Parameters["Address"] + "/dyn/"
//        url=url_base + "login.json"
//        payload = ('{"pass" : "' + Parameters["Password"] + '", "right" : "usr"}')
//        headers = {'Content-Type': 'application/json', 'Accept-Charset': 'UTF-8'}
//
//        self.lastPolled = self.lastPolled + 1
//        if (self.lastPolled > (3*int(Parameters["Mode3"]))): self.lastPolled = 1
//        if (self.lastPolled == 1):
//        try:
//        r = requests.post(url, data=payload, headers=headers)
//        except:
//        Domoticz.Log("Error accessing SMA inverter on "+Parameters["Address"])
//        else:
//        j = json.loads(r.text)
//        try:
//        sid = j['result']['sid']
//        except:
//        Domoticz.Log("No response from SMA inverter on "+Parameters["Address"])
//        else:
//        url = url_base + "getValues.json?sid=" + sid
//        payload = ('{"destDev":[],"keys":["6400_00260100","6400_00262200","6100_40263F00"]}')
//        headers = {'Content-Type': 'application/json', 'Accept-Charset': 'UTF-8'}
//
//        try:
//        r = requests.post(url, data=payload, headers=headers)
//        except:
//        Domoticz.Log("No data from SMA inverter on "+Parameters["Address"])
//        else:
//        j = json.loads(r.text)
//
//        sma_pv_watt = j['result']['012F-730B00E6']['6100_40263F00']['1'][0]['val']
//        if sma_pv_watt is None:
//        sma_pv_watt = 0
//        sma_kwh_today = j['result']['012F-730B00E6']['6400_00262200']['1'][0]['val']
//        sma_kwh_total = j['result']['012F-730B00E6']['6400_00260100']['1'][0]['val']/1000
//
//        #              Domoticz.Log(r.text)
//        #              Domoticz.Log(str(sma_pv_watt))
//        #              Domoticz.Log(str(sma_kwh_today))
//
//        Devices[1].Update(nValue=0, sValue=str(sma_pv_watt)+";"+str(sma_kwh_today))
//        sValue="%.2f" % sma_kwh_total
//        Devices[2].Update(nValue=0, sValue=sValue.replace('.',','))
//
