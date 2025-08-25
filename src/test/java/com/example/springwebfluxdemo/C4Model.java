package com.example.springwebfluxdemo;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClientException;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.component.ComponentFinder;
import com.structurizr.component.ComponentFinderBuilder;
import com.structurizr.component.ComponentFinderStrategyBuilder;
import com.structurizr.component.matcher.NameSuffixTypeMatcher;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class C4Model {

  private static final Long WORKSPACE_ID = 6L;
  private static final String API_KEY = "ee3af3c3-5b20-451f-837f-5f637f9419a5";
  private static final String API_SECRET = "9bd9e3e1-16e7-4e9a-83b1-61b5718b8961";
  private static final String API_URL = "http://localhost:8080/api";

  @Test
  public void test() throws StructurizrClientException {

    Workspace workspace = new Workspace("Test", "test");
    Model model = workspace.getModel();

    SoftwareSystem softwareSystem =
        model.addSoftwareSystem("Spring test", "test component scanning");
    Container webApplication = softwareSystem.addContainer("Web Application");

    ComponentFinder componentFinder =
        new ComponentFinderBuilder()
            .forContainer(webApplication)
            .fromClasses(new File("target/spring-webflux-demo-0.0.1-SNAPSHOT.jar"))
            .withStrategy(
                new ComponentFinderStrategyBuilder()
                    .matchedBy(new NameSuffixTypeMatcher("Controller"))
                    //                                .matchedBy(new
                    // AnnotationTypeMatcher("org.springframework.stereotype.Controller"))
                    .withTechnology("Spring MVC Controller")
                    .build())
            .withStrategy(
                new ComponentFinderStrategyBuilder()
                    .matchedBy(new NameSuffixTypeMatcher("Repository"))
                    .withTechnology("Spring Data Repository")
                    .build())
            .build();

    Set<Component> components = componentFinder.run();

    WorkspaceApiClient client = new WorkspaceApiClient(API_URL, API_KEY, API_SECRET);
    client.putWorkspace(WORKSPACE_ID, workspace);
  }
}
