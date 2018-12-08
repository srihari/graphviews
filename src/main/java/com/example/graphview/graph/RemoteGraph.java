package com.example.graphview.graph;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.janusgraph.core.ConfiguredGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RemoteGraph {
    private static Logger LOGGER = LoggerFactory.getLogger(RemoteGraph.class);

    private PropertiesConfiguration conf;
    private Cluster cluster;
    private Client client;

    private Graph graph;
    private GraphTraversalSource g;

    public GraphTraversalSource openGraph(String confFilePath) throws ConfigurationException {
        LOGGER.info("Opening graph");
        conf = new PropertiesConfiguration(confFilePath);

        // using the remote driver for schema
        try {
            cluster = Cluster.open(conf.getString("gremlin.remote.driver.clusterFile"));
            client = cluster.connect();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }

        // using the remote graph for queries
        graph = EmptyGraph.instance();
        g = graph.traversal().withRemote(conf);
        return g;
    }

    public Result executeGremlinCommand(String command, Map<String, Object> params) {
        LOGGER.info("Executing Gremlin command: " + command);
        CompletableFuture<List<Result>> results = client.submit(command, params).all();

        Result rawResult = null;
        try {
            rawResult = results.get().get(0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(" Returning Result Object >>>>>>> " + rawResult);
        return rawResult;
    }

    public void closeGraph() throws Exception {
        LOGGER.info("Closing graph");

        try {
            if (g != null) {
                g.close();
            }
            if (cluster != null) {
                cluster.close();
            }
        } finally {
            g = null;
            graph = null;
            client = null;
            cluster = null;

        }

    }
}
