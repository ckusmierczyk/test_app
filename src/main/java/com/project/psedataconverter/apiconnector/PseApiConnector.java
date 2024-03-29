package com.project.psedataconverter.apiconnector;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Log4j
@Component
@Qualifier("pseApiConnector")
public class PseApiConnector implements ApiConnector {

    @Override
    public List<String> getDataFromUrl(String startDateUnix, String endDateUnix) {
        List<String> dataFromUrl = new LinkedList<>();
        try {
            String urlString;
            if (endDateUnix == null) {
                urlString = "https://www.pse.pl/obszary-dzialalnosci/krajowy-system-elektroenergetyczny/zapotrzebowanie-kse?" +
                        "p_p_id=danekse_WAR_danekserbportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_cacheability=cacheLevelPage&" +
                        "p_p_col_id=column-2&p_p_col_count=1&_danekse_WAR_danekserbportlet_type=kse&_danekse_WAR_danekserbportlet_target=csv" +
                        "&_danekse_WAR_danekserbportlet_from=" + startDateUnix;
            } else {
//            String urlString = "https://www.pse.pl/dane-systemowe/funkcjonowanie-kse/raporty-dobowe-z-pracy-kse/zapotrzebowanie-mocy-kse?" +
//                    "p_p_id=danekse_WAR_danekserbportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_cacheability=cacheLevelPage&" +
//                    "p_p_col_id=column-2&p_p_col_count=1&_danekse_WAR_danekserbportlet_type=kse&_danekse_WAR_danekserbportlet_target=csv&" +
//                    "_danekse_WAR_danekserbportlet_from=1548975600000&_danekse_WAR_danekserbportlet_to=1551308400000";
                urlString = "https://www.pse.pl/obszary-dzialalnosci/krajowy-system-elektroenergetyczny/zapotrzebowanie-kse?" +
                        "p_p_id=danekse_WAR_danekserbportlet&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_cacheability=cacheLevelPage&" +
                        "p_p_col_id=column-2&p_p_col_count=1&_danekse_WAR_danekserbportlet_type=kse&_danekse_WAR_danekserbportlet_target=csv" +
                        "&_danekse_WAR_danekserbportlet_from=" + startDateUnix + "&_danekse_WAR_danekserbportlet_to=" + endDateUnix;
            }
            URL url = new URL(urlString);
            BufferedReader csv = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = csv.readLine()) != null) {
                dataFromUrl.add(line);
            }
            writeFile(dataFromUrl);
            log.info("Connection to PSE API was successful");
        } catch (MalformedURLException e) {
            log.error("URL is malformed!" + e.getMessage());
        } catch (IOException e) {
            log.error("Open stream exception occured" + e.getMessage());
        }
        return dataFromUrl;
    }

    @Override
    public List<String> getDataFromUrl(String dayUnix) {
        return this.getDataFromUrl(dayUnix, null);
    }

    public void writeFile(List<String> dataFromUrl) {
        try {
            String data = "";
            for (String line : dataFromUrl) {
                data += line + "\n";
            }
            Files.write(Paths.get("actualProcessingData.txt"), data.getBytes());
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
