package com.example.graphview.graph;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RemoteGraph {
    private static Logger LOGGER = LoggerFactory.getLogger(RemoteGraph.class);

    private PropertiesConfiguration conf;
    private Cluster cluster;
    private Client client;

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
        Graph graph = EmptyGraph.instance();
        g = graph.traversal().withRemote(conf);

        return g;
    }

    public CompletableFuture<List<Result>> executeGremlin(String command) {
        return client.submit(command).all();
    }
}
