package com.example.graphview.controllers;

import org.janusgraph.core.JanusGraph;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private static final String url = "";
    private JanusGraph airroutes;


    @RequestMapping("/airroutes/{airportCode}")
    public ModelAndView viewRoutes(ModelAndView modelAndView, @PathVariable String airportCode) {
        connect();

        modelAndView.setViewName("airroutes");

        return modelAndView;
    }

    private void connect() {


    }


}