package com.ab.springreactoroperations;

import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

public class CreationOperationTest {

  @Test
  public void createFluxJustTest() {
    Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
    fruitFlux.subscribe(f -> System.out.println("Here is some fruit: " + f));

    executeStepVerifierOnFruitFlux(fruitFlux);
  }

  @Test
  public void createFluxFromArray() {
    String[] fruits = new String[] {"Apple", "Orange", "Grape", "Banana", "Strawberry"};
    Flux<String> fruitFlux = Flux.fromArray(fruits);

    executeStepVerifierOnFruitFlux(fruitFlux);
  }

  @Test
  public void createFluxFromIterable() {
    List<String> fruits = new ArrayList<>();
    fruits.add("Apple");
    fruits.add("Orange");
    fruits.add("Grape");
    fruits.add("Banana");
    fruits.add("Strawberry");

    Flux<String> fruitFlux = Flux.fromIterable(fruits);
    executeStepVerifierOnFruitFlux(fruitFlux);
  }

  @Test
  public void createFluxRange() {
    Flux<Integer> intervalFlux = Flux.range(1, 5);
    StepVerifier.create(intervalFlux)
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(4)
        .expectNext(5)
        .verifyComplete();
  }

  private void executeStepVerifierOnFruitFlux(Publisher<String> fruitFlux) {
    StepVerifier.create(fruitFlux)
        .expectNext("Apple")
        .expectNext("Orange")
        .expectNext("Grape")
        .expectNext("Banana")
        .expectNext("Strawberry")
        .verifyComplete();
  }
}
