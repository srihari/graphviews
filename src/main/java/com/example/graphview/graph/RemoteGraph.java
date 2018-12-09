package com.example.graphview.graph;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.janusgraph.core.ConfiguredGraphFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration;
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.management.ConfigurationManagementGraph;
import org.janusgraph.graphdb.management.JanusGraphManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RemoteGraph {
    private static Logger LOGGER = LoggerFactory.getLogger(RemoteGraph.class);

    private PropertiesConfiguration remoteConf;
    private Cluster cluster;
    private Client client;

    private Graph graph;
    private GraphTraversalSource g;

    public GraphTraversalSource openGraph(String remoteConfFilePath) throws ConfigurationException {
        LOGGER.info("Opening graph");
        remoteConf = new PropertiesConfiguration(remoteConfFilePath);

        // using the remote driver for schema
        try {
            cluster = Cluster.open(remoteConf.getString("gremlin.remote.driver.clusterFile"));
            client = cluster.connect();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }

        JanusGraphManager gm = new JanusGraphManager(new Settings());

        Configuration graphConf = new PropertiesConfiguration("conf/janusgraph-cassandra-configurationgraph.properties");

        StandardJanusGraph graph =
                new StandardJanusGraph(new GraphDatabaseConfiguration(new CommonsConfiguration(graphConf)));
        ConfigurationManagementGraph configurationManagementGraph = new ConfigurationManagementGraph(graph);


        if (null == ConfiguredGraphFactory.getTemplateConfiguration()) {
            Configuration templateConfiguration = ConfigurationUtils.cloneConfiguration(graphConf);
            templateConfiguration.clearProperty("graph.graphname");
            ConfiguredGraphFactory.createTemplateConfiguration(templateConfiguration);
        } else {
//            configurationManagementGraph.createConfiguration(graphConf);  // Need to handle this part
            ConfiguredGraphFactory.updateConfiguration(graphConf.getString("graph.graphname"), graphConf);
        }
        // using the remote graph for queries
//        graph = EmptyGraph.instance();

        JanusGraph airroutes = ConfiguredGraphFactory.open("airroutes");

        g = airroutes.traversal();
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
