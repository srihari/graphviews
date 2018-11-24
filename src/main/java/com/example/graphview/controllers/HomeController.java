package com.example.graphview.controllers;

import com.example.graphview.graph.RemoteGraph;
import org.apache.commons.configuration.ConfigurationException;
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
    private String JANUS_CONF_FILE_PATH = "conf/jgex-remote.properties";

    private static final String url = "";
    private JanusGraph airroutes;

    @RequestMapping("/airroutes/{airportCode}")
    public ModelAndView viewRoutes(ModelAndView modelAndView, @PathVariable String airportCode) throws ConfigurationException {
        connect();
        modelAndView.setViewName("airroutes");

        return modelAndView;
    }

    private void connect() throws ConfigurationException {
        RemoteGraph remoteGraph = new RemoteGraph();
        remoteGraph.openGraph(JANUS_CONF_FILE_PATH);

        // Executes 1+1 on Gremlin Server
        CompletableFuture<List<Result>> results = remoteGraph.executeGremlin("1+1");

        try {
            Result shouldBe2 = results.get().get(0);
            System.out.println(shouldBe2);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}