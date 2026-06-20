package com.mtganalytics.lab.svc;

import java.io.IOException;
import java.util.Map;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.GetMappingResponse;
import org.opensearch.client.opensearch.indices.get_mapping.IndexMappingRecord;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mtganalytics.lab.exception.GameServiceException;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "game-service")
public class IndexService {

    private final OpenSearchClient openSearchClient;
    private String mtgGameEntriesIndexName;

    public Map<String, Property> getIndexMapping() throws IOException {
        try {
            GetMappingResponse response = openSearchClient.indices()
                    .getMapping(gm -> gm.index(mtgGameEntriesIndexName));

            IndexMappingRecord record = response.result().get(mtgGameEntriesIndexName);

            TypeMapping typeMapping = record.mappings();

            return typeMapping.properties();
        } catch (IOException e) {
            throw new GameServiceException("Error retrieving index mapping", e);
        }
    }
}
