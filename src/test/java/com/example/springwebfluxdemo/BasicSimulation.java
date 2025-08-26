package com.example.springwebfluxdemo;

import static io.gatling.javaapi.core.Choice.withWeight;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.time.Duration;
import java.util.Map;

public class BasicSimulation extends Simulation {

  private static final HttpProtocolBuilder HTTP_PROTOCOL_BUILDER = setupProtocolForSimulation();

  private static final ScenarioBuilder POST_SCENARIO_BUILDER = buildPostScenario();

  public BasicSimulation() {
    this.setUp(
            POST_SCENARIO_BUILDER
                .pause(Duration.ofSeconds(1))
                .injectOpen(
                    rampUsers(100).during(Duration.ofSeconds(10)),
                    stressPeakUsers(3000).during(Duration.ofSeconds(10)),
                    constantUsersPerSec(100).during(Duration.ofSeconds(30)),
                    OpenInjectionStep.atOnceUsers(1000)
                    //                OpenInjectionStep.atOnceUsers(5)

                    ))
        .assertions(
            global().responseTime().max().lt(200),
            global().responseTime().mean().lt(50),
            global().successfulRequests().percent().gt(95.0))
        .protocols(HTTP_PROTOCOL_BUILDER);
  }

  private static HttpProtocolBuilder setupProtocolForSimulation() {
    return http.baseUrl("http://localhost:8085")
        .acceptHeader("application/json")
        .maxConnectionsPerHost(10)
        .userAgentHeader("Gatling/Performance Test");
  }

  private static ScenarioBuilder buildPostScenario() {

    return CoreDsl.scenario("Load Test Add and Get Songs")
        .feed(
            arrayFeeder(
                    new Map[] {
                      Map.of("name", "foo1"), Map.of("name", "foo2"), Map.of("name", "foo3")
                    })
                .random())
        .randomSwitch()
        .on(
            withWeight(
                30,
                exec(
                    http("create-song-request")
                        .post("/")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/stream+json")
                        .body(
                            CoreDsl.StringBody(
                                "{ \"spotifyId\": \"1\", \"name\": \"${name}\", \"artists\": \"Test Artist\" }"))
                        .check(status().is(200)))),
            withWeight(
                70,
                exec(
                    http("flux-get-request")
                        .get("/search?search=${name}")
                        .header("Accept", "application/x-ndjson")
                        .check(status().is(200))
                        .check(jsonPath("$.id").ofInt())
                        .check(jsonPath("$.spotifyId").ofString())
                        .check(jsonPath("$.name").ofString())
                        .check(jsonPath("$.artists").ofString()))));
  }
}
