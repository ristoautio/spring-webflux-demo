package com.example.springwebfluxdemo;

import static com.tngtech.archunit.lang.ConditionEvent.createMessage;
import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;

@AnalyzeClasses(
    packages = "com.example.springwebfluxdemo",
    importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

  @ArchTest
  public static final ArchRule myRule =
      classes()
          .that()
          .resideInAPackage("..service..")
          .should()
          .onlyBeAccessed()
          .byAnyPackage("..controller..", "..service..");

  @ArchTest
  public static final ArchRule layerRule =
      layeredArchitecture()
          .consideringAllDependencies()
          .layer("Controller")
          .definedBy("..controller..")
          .layer("Service")
          .definedBy("..service..")
          .layer("Persistence")
          .definedBy("..repository..")
          .whereLayer("Controller")
          .mayNotBeAccessedByAnyLayer()
          .whereLayer("Service")
          .mayOnlyBeAccessedByLayers("Controller")
          .whereLayer("Persistence")
          .mayOnlyBeAccessedByLayers("Service");

  @ArchTest
  public static final ArchRule noCycles =
      slices().matching("com.example.(*)..").should().beFreeOfCycles();

  @ArchTest
  ArchRule logging = freeze(methods().that().areAnnotatedWith(PostMapping.class).should(log()));

  private ArchCondition<? super JavaMethod> log() {
    return new ArchCondition<JavaMethod>("log") {
      @Override
      public void check(JavaMethod method, ConditionEvents events) {
        boolean logCalled =
            method.getMethodCallsFromSelf().stream()
                .anyMatch(javaMethod -> javaMethod.getTargetOwner().isEquivalentTo(Logger.class));
        if (!logCalled) {
          events.add(violated(method, createMessage(method, "Method should call Logger")));
        }
      }
    };
  }
}
