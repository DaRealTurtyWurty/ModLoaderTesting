package dev.turtywurty.testgradleplugin.piston.version;

import com.google.gson.JsonObject;

public record Downloads(Download client, Download client_mappings, Download server, Download server_mappings) {
    public static Downloads fromJson(JsonObject json) {
        JsonObject clientJson = json.getAsJsonObject("client");
        Download client = Download.fromJson(clientJson);

        JsonObject clientMappingsJson = json.getAsJsonObject("client_mappings");
        Download clientMappings = Download.fromJson(clientMappingsJson);

        JsonObject serverJson = json.getAsJsonObject("server");
        Download server = Download.fromJson(serverJson);

        JsonObject serverMappingsJson = json.getAsJsonObject("server_mappings");
        Download serverMappings = Download.fromJson(serverMappingsJson);

        return new Downloads(client, clientMappings, server, serverMappings);
    }
}
