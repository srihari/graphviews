package com.example.graphview.controllers;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.janusgraph.core.JanusGraph;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        Cluster cluster = Cluster.open();   //connects to the localhost when no config is passed
        Client client = cluster.connect();

        CompletableFuture<List<Result>> results = client.submit("1+1").all();
        try {
            Result shouldBe2 = results.get().get(0);
            System.out.println(shouldBe2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}