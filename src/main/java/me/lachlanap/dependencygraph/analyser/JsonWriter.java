package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Writes the raw analysis to a JSON file.
 */
public class JsonWriter implements AnalysisWriter {
    @Override
    public void write(Analysis a, PrintWriter out) throws IOException {
        Map<String, Integer> entityIds = new HashMap<>();
        int nextId = 0;
        out.println("{\"entities\": [");

        for (Entity entity : a.getEntities()) {
            if (entity.hasParent())
                out.println("  {\"name\":\"" + entity.getName() + "\"," +
                                    "\"parent\":\"" + entity.getParent().getName() + "\"},");
            else
                out.println("  {\"name\":\"" + entity.getName() + "\"},");
            entityIds.put(entity.getName(), nextId++);
        }

        out.println(" ], dependencies: [");

        for (Dependency d : a.getDependencies()) {
            Entity from = d.getFrom();
            if (!entityIds.containsKey(from.getName()))
                continue;
            int fromId = entityIds.get(from.getName());

            if (entityIds.containsKey(d.getTo().getName())) {
                int toId = entityIds.get(d.getTo().getName());
                out.println("  {\"from\":" + fromId + "," +
                                    "\"to\":" + toId +
                                    ",\"strength\":\"" + (d.getStrength() == CouplingStrength.Public ? "pub" : "imp") + "\"},");

            } else {
                out.println("  {\"from\":" + fromId + "," +
                                    "\"to\":\"" + d.getTo().getName() + "\"" +
                                    ",\"strength\":\"" + (d.getStrength() == CouplingStrength.Public ? "pub" : "imp") + "\"},");
            }
        }

        out.println(" ]\n}");
    }
}
