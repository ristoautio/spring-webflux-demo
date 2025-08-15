package com.example.springwebfluxdemo;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

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
}
