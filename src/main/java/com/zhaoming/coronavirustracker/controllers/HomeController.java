package com.zhaoming.coronavirustracker.controllers;

import com.zhaoming.coronavirustracker.CoronaVirusDataService.CoronaVirusDataService;
import com.zhaoming.coronavirustracker.models.LocationStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

//Render HTML UI return a name which binds to the template
@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    //Home URL
    @GetMapping("/")
    public String home(Model model) {
        ArrayList<LocationStats> allStates = coronaVirusDataService.getAllStats();

        //Add all the integer from the object and sum them up
        int totalCases = allStates.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStates.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStats", allStates);
        model.addAttribute("totalReportedCases", totalCases);
        model.addAttribute("totalNewCases", totalNewCases);
        return "home";
    }
}
