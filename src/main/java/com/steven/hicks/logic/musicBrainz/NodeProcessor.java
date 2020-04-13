package com.steven.hicks.logic.musicBrainz;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface NodeProcessor {
    JsonNode processNode(JsonNode node);
}
