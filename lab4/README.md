# Lab 4 — Behavioral Design Patterns (Moodle Lab 3)

TMPS Shop is a Java console application that extends the structural patterns
lab with **Strategy**, **Command**, and **Observer** in the shopping workflow.
See [REPORT.md](REPORT.md) for the design rationale, UML diagrams, code
snippets, and test results.

## Build and run

Install JDK 17 or newer. Run from the repository root:

```sh
./mvnw -pl lab4 -am clean verify
java -jar lab4/target/lab4-1.0-SNAPSHOT.jar
```

On Windows, use `mvnw.cmd` in place of `./mvnw`. The first build downloads Maven
and its dependencies.

The verification command builds the executable JAR and runs all 13 JUnit tests.
To run just the tests, use `./mvnw -pl lab4 -am test`. To verify all labs, use
`./mvnw clean verify`.

## Demonstration

1. Choose **1** to list products. The first request triggers the lazy catalog
   proxy (`Loading product catalog...`).
2. Choose **2**, enter product ID **P1** and quantity **1** (`Mechanical keyboard`,
   `850.00 MDL`), then choose **2** again and add **P3** with quantity **2**.
3. Choose **5** to undo. The command history reverts the last action
   (`Undone: Add USB-C cable x2`), leaving only the keyboard in the cart.
4. Choose **6** to switch the discount strategy at runtime from **10% off** to
   **100.00 MDL off orders >= 500.00 MDL** (option **2**). Choosing **5** here
   would revert the discount strategy back to **10% off**.
5. Choose **8** to check out. The receipt shows a **850.00 MDL** subtotal,
   **100.00 MDL** discount, **750.00 MDL** total, decorated footer lines, and
   notifications from all three subscribed observers (`[Loyalty]`, `[Audit]`,
   and `[Alert]`).
6. Choose **7** and enter **3** to unsubscribe **High-value order alert** at
   runtime. Add **P1** (quantity **1**) and choose **8** again: `[Loyalty]` and
   `[Audit]` fire while `[Alert]` stays silent.
7. Choose **0** to exit.

Startup options from the previous lab still work:

```sh
java -jar lab4/target/lab4-1.0-SNAPSHOT.jar --regular --plain-receipt
```
