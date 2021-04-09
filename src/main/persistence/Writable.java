package persistence;

import org.json.JSONObject;

/* Class for writing JSON
   Made with reference: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
