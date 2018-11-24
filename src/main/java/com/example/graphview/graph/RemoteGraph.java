package com.example.graphview.graph;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    public void executeGremlin(String command) {
        LOGGER.info("Executing Gremlin command: " + command);
        CompletableFuture<List<Result>> results = client.submit(command).all();

        try {
            Result rawResult = results.get().get(0);
            System.out.println(rawResult);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void createElements() {
        // Use bindings to allow the Gremlin Server to cache traversals that
        // will be reused with different parameters. This minimizes the
        // number of scripts that need to be compiled and cached on the server.
        // http://tinkerpop.apache.org/docs/3.2.6/reference/#parameterized-scripts
        final Bindings b = Bindings.instance();

        final String NAME = "name";
        final String AGE = "age";
        final String LABEL = "label";

        g.addV(b.of(LABEL, "titan"))
                .property(NAME, b.of(NAME, "saturn"))
                .property(AGE, b.of(AGE, 10000)).next();

        executeGremlin("g.V().values(\"name\")");
    }
}
