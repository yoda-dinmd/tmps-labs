# Lab 3 — Structural Design Patterns (Moodle Lab 2)

TMPS Shop is a Java console application that demonstrates Facade, Decorator,
and Proxy during ordinary shopping operations. See [report.md](report.md) for
the design rationale, UML, and test results.

## Build and run

Install JDK 17 or newer. Run from the repository root:

```sh
./mvnw -pl lab3 -am clean verify
java -jar lab3/target/lab3-1.0-SNAPSHOT.jar
```

On Windows, use `mvnw.cmd` in place of `./mvnw`. The first build downloads Maven
and its dependencies.

The verification command builds the executable JAR and runs the six JUnit tests.
To run just the tests, use `./mvnw -pl lab3 -am test`. To verify all labs, use
`./mvnw clean verify`.

## Demonstration

1. Choose **1** to list products. The first request prints
   `Loading product catalog...`. Choose **1** again and confirm the products
   appear again without another load message.
2. Choose **2**, enter product ID **P3**, and enter quantity **2**.
3. Choose **5**. Checkout prints a **240.00 MDL** subtotal, a **24.00 MDL**
   discount, and a **216.00 MDL** total. The receipt ends with
   `Items purchased: 2` and `Keep this receipt for returns.`.
4. Choose **3** to confirm the cart was cleared, then **0** to exit.

Run with `--plain-receipt` to compare the base receipt with the decorated version.
Use `--regular` to select checkout without the 10% discount. The options can be
combined:

```sh
java -jar lab3/target/lab3-1.0-SNAPSHOT.jar --regular --plain-receipt
```
