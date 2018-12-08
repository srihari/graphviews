package com.example.graphview.models.graph;

public class GraphQueryRequest {
    private String query;

    public GraphQueryRequest() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
