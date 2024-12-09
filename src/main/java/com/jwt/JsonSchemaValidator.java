package com.jwt;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;

public class JsonSchemaValidator {

    private final Schema schema;

    public JsonSchemaValidator(String schemaFilePath) throws Exception {
        String schemaContent = Files.readString(Path.of(schemaFilePath));
        JSONObject jsonSchema = new JSONObject(schemaContent);
        this.schema = SchemaLoader.load(jsonSchema);
    }

    public void validate(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        schema.validate(jsonObject);
    }
}
