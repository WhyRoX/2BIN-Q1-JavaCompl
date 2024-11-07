package be.vinci.api;

import be.vinci.classes.User;
import be.vinci.instances.InstanceGraph1;
import be.vinci.services.ClassAnalyzer;
import be.vinci.services.InstancesAnalyzer;
import jakarta.json.JsonStructure;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.lang.reflect.Method;

/**
 * Send instances graph data to make object diagrams
 *
 * The instances graphs are initialized by a class containing the "initInstanceGraph" method,
 * building the instance graph, and returning it.
 *
 * The "instance builder class name" must be given and present into the "instances" package
 */
@Path("instances")
public class Instances {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonStructure getInstanceGraphInfo(@QueryParam("builderclassname") String builderClassname) {
        if (builderClassname == null || builderClassname.isEmpty()) {
            throw new WebApplicationException(404);
        }
        try {
            Class<?> builderClass = Class.forName("be.vinci.instances." + builderClassname);
            Method initInstanceGraph = builderClass.getMethod("initInstanceGraph");
            Object instanceGraph = initInstanceGraph.invoke(builderClass.getDeclaredConstructor().newInstance());
            InstancesAnalyzer analyzer = new InstancesAnalyzer(instanceGraph);
            return analyzer.getFullInfo();
        } catch (Exception e) {
            throw new InternalError();
        }
    }
}