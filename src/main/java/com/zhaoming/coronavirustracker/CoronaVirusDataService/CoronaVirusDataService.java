package com.zhaoming.coronavirustracker.CoronaVirusDataService;

import com.zhaoming.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";

    private ArrayList<LocationStats> allStats = new ArrayList<>();

    // After the application starts up execute this method
    // Schedule the run often method regularly * means every second
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        // Create new instance of the list for concurrency issue
        ArrayList<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse.body());

        StringReader csvBodyReader = new StringReader(httpResponse.body());
        // Adapted from Apache Commons
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
            newStats.add(locationStat);
        }

        //set the current set with the generated set in the function
        this.allStats = newStats;
    }

    public ArrayList<LocationStats> getAllStats() {
        return allStats;
    }
}
