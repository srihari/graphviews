package com.example.graphview.controllers.graph;


import com.example.graphview.models.graph.GraphQueryRequest;
import com.example.graphview.models.graph.GraphQueryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphController {

    @PostMapping(value = "/graph/query", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> executeGraphQuery(@RequestBody GraphQueryRequest graphQueryRequest) {
        GraphQueryResponse response = new GraphQueryResponse();
        response.setResponseString("[\n" +
                "   {\n" +
                "      \"objects\":[\n" +
                "                        {\"id\":4304,\"label\":\"NodeType1\",\"type\":\"vertex\",\"properties\":{\"degree\": [ ]}},\n" +
                "                        {\"id\":\"6fws-3v4w-aad-3bk\",\"label\":\"CONNECTING_LABEL\",\"type\":\"edge\",\"inVLabel\":\"NodeType1\",\"outVLabel\":\"NodeType2\",\"inV\":4304,\"outV\":180320,\"properties\": {}},\n" +
                "                        {\"id\":4304,\"label\":\"NodeType2\",\"type\":\"vertex\",\"properties\":{ \"degree\": [ ] }}\n" +
                "                     ]\n" +
                "    },\n" +
                "    {\n" +
                "             \"objects\":[\n" +
                "                        {\"id\":4304,\"label\":\"NodeType1\",\"type\":\"vertex\",\"properties\":{ \"degree\": [ ] }},\n" +
                "                        {\"id\":\"6fws-3v4w-add-3bk\",\"label\":\"CONNECTING_LABEL\",\"type\":\"edge\",\"inVLabel\":\"NodeType1\",\"outVLabel\":\"NodeType3\",\"inV\":4304,\"outV\":59364,\"properties\": {}},\n" +
                "                        {\"id\":59364,\"label\":\"NodeType3\",\"type\":\"vertex\",\"properties\":{ \"degree\": [ ] }} \n" +
                "                     ]\n" +
                "\n" +
                "    }\n" +
                "]");
        return new ResponseEntity<>(response.getResponseString(), HttpStatus.OK);
    }

}
