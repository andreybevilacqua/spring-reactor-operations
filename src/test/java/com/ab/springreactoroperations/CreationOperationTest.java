package com.ab.springreactoroperations;

import org.assertj.core.api.ClassAssert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
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

  @Test
  public void createFluxInterval() {
    Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1))
        .take(5); //  Take operation defines the the limit of results.

    StepVerifier.create(intervalFlux)
        .expectNext(0L)
        .expectNext(1L)
        .expectNext(2L)
        .expectNext(3L)
        .expectNext(4L)
        .verifyComplete();
  }

  @Test
  public void mergeFluxes() {
    Flux<String> characterFlux = Flux
        .just("Garfield", "Kojak", "Barbossa")
        .delayElements(Duration.ofMillis(500));

    Flux<String> foodFlux = Flux
        .just("Lasagna", "Lollipops", "Apples")
        .delaySubscription(Duration.ofMillis(250))
        .delayElements(Duration.ofMillis(500));

    Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);

    StepVerifier.create(mergedFlux)
        .expectNext("Garfield")
        .expectNext("Lasagna")
        .expectNext("Kojak")
        .expectNext("Lollipops")
        .expectNext("Barbossa")
        .expectNext("Apples")
        .verifyComplete();
  }

  @Test
  public void zipFluxes() {
    Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
    Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

    Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);
    StepVerifier.create(zippedFlux)
        .expectNextMatches(p ->
            p.getT1().equals("Garfield") && p.getT2().equals("Lasagna"))
        .expectNextMatches(p ->
            p.getT1().equals("Kojak") && p.getT2().equals("Lollipops"))
        .expectNextMatches(p ->
            p.getT1().equals("Barbossa") && p.getT2().equals("Apples"))
        .verifyComplete();
  }

  @Test
  public void zipFluxesToObject() {
    Flux<String> characterFlux = Flux.just("Garfield", "Kojak", "Barbossa");
    Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples");

    Flux<String> zippedFlux = Flux.zip(characterFlux, foodFlux, (character, food) -> character + " eats " + food);

    StepVerifier.create(zippedFlux)
        .expectNext("Garfield eats Lasagna")
        .expectNext("Kojak eats Lollipops")
        .expectNext("Barbossa eats Apples")
        .verifyComplete();
  }

  @Test
  public void firstFlux() {
    Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth").delaySubscription(Duration.ofMillis(100));
    Flux<String> fastFlux = Flux.just("hare", "cheetah", "squirrel");

    Flux<String> firstFlux = Flux.first(slowFlux, fastFlux);

    StepVerifier.create(firstFlux)
        .expectNext("hare")
        .expectNext("cheetah")
        .expectNext("squirrel")
        .verifyComplete();
  }

  @Test
  public void skipFew() {
    Flux<String> skipFlux = Flux
        .just("one", "two", "skip a few", "ninety nine", "one hundred")
        .skip(3);
    StepVerifier.create(skipFlux)
        .expectNext("ninety nine")
        .expectNext("one hundred")
        .verifyComplete();
  }

  @Test
  public void skipAFewSeconds() {
    Flux<String> skipFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
        .delayElements(Duration.ofSeconds(1))
        .skip(Duration.ofSeconds(4));
    StepVerifier.create(skipFlux)
        .expectNext("ninety nine", "one hundred")
        .verifyComplete();
  }

  @Test
  public void takeFlux() {
    Flux<String> nationalParkFlux = Flux
        .just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
        .take(3);
    StepVerifier.create(nationalParkFlux)
        .expectNext("Yellowstone")
        .expectNext("Yosemite")
        .expectNext("Grand Canyon")
        .verifyComplete();
  }

  @Test
  public void filterFlux() {
    Flux<String> nationalParkFlux = Flux
        .just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
        .filter(park -> !park.contains(" "));
    StepVerifier.create(nationalParkFlux)
        .expectNext("Yellowstone")
        .expectNext("Yosemite")
        .expectNext("Zion")
        .verifyComplete();
  }

  @Test
  public void distinctFlux() {
    Flux<String> animalFlux = Flux
        .just("dog", "cat", "bird", "dog", "bird", "anteater")
        .distinct();
    StepVerifier.create(animalFlux)
        .expectNext("dog")
        .expectNext("cat")
        .expectNext("bird")
        .expectNext("anteater")
        .verifyComplete();
  }

  @Test
  public void mapFlux() {
    Flux<Player> playerFlux = Flux
        .just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
        .map(n -> {
          String[] split = n.split("\\s");
          return new Player(split[0], split[1]);
        });
    StepVerifier.create(playerFlux)
        .expectNext(new Player("Michael", "Jordan"))
        .expectNext(new Player("Scottie", "Pippen"))
        .expectNext(new Player("Steve", "Kerr"))
        .verifyComplete();
  }

  @Test
  public void flatMap() {
    class Player {private Player(String firstName, String lastName){}}

    Flux<Player> playerFlux = Flux
        .just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
        .flatMap(n ->
            Mono.just(n)
                .map(p -> {
                  String[] split = p.split("\\s");
                  return new Player(split[0], split[1]);
                }))
        .subscribeOn(Schedulers.parallel());

    List<Player> playerList = Arrays.asList(
        new Player("Michael", "Jordan"),
        new Player("Scottie", "Pippen"),
        new Player("Steve", "Kerr"));

    StepVerifier.create(playerFlux)
        .expectNextMatches(playerList::contains)
        .expectNextMatches(playerList::contains)
        .expectNextMatches(playerList::contains)
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

  private static class Player {
    Player(String firstName, String lastName){ }
    @Override
    public boolean equals(Object object) {
      return this == object;
    }
  }
}
