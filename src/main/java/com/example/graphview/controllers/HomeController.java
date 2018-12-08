package com.example.graphview.controllers;

import com.example.graphview.graph.RemoteGraph;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
public class HomeController {
    private String JANUS_CONF_FILE_PATH = "conf/jgex-remote.properties";
    RemoteGraph remoteGraph = new RemoteGraph();
    private static final String url = "";

    @RequestMapping("/airroutes/{airportCode}")
    public ModelAndView viewRoutes(ModelAndView modelAndView, @PathVariable String airportCode) throws Exception {
        executeQuery(airportCode);
        modelAndView.setViewName("airroutes");
        return modelAndView;
    }

    private void executeQuery(String airportCode) throws Exception {
        GraphTraversalSource graphTraversalSource = remoteGraph.openGraph(JANUS_CONF_FILE_PATH);
        String city = (String) graphTraversalSource.V().limit(1).values("city").next();
        System.out.println("Here is your >>>>>>>> " + city);

        Result result = remoteGraph.executeGremlinCommand(traversalScript(airportCode), new HashMap<>());
        remoteGraph.closeGraph();
    }

    private String traversalScript(String airportCode) {
        String script = "graph = ConfiguredGraphFactory.open(\"airroutes\");" +
                "g = graph.traversal();" +
                "g.V().has('code', '%s').properties();" +
                "";
        return String.format(script, airportCode);
    }

}