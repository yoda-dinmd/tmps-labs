# TMPS — Java laboratory work

Course: **Tehnici si mecanisme de proiectare software (TMPS)**.

A small ecommerce console project for practicing course concepts. Each laboratory
is a separate Maven module with its own runnable application. The root `pom.xml`
aggregates the modules and shares Java, dependency, and build-plugin versions.

## Requirements and running

Install **JDK 17 or newer**. Maven 3.9.9 is provided through the Maven Wrapper;
the first build needs internet access to download Maven and build dependencies.

From the repository root on Linux/macOS:

```sh
./mvnw clean verify
java -jar lab1/target/lab1-1.0-SNAPSHOT.jar
java -jar lab2/target/lab2-1.0-SNAPSHOT.jar
```

On Windows, use `mvnw.cmd clean verify`, followed by the same `java -jar` command.
If Maven is already installed, `mvn` can replace `./mvnw`.

Build only a specific lab (e.g., `lab1` or `lab2`):

```sh
./mvnw -pl lab2 -am package
```

## Modules

| Module | Topic | Application |
| --- | --- | --- |
| [lab1](lab1/README.md) | SRP, OCP, ISP | Product catalog, cart, discounts, checkout, receipts |
| [lab2](lab2/README.md) | Singleton, Factory Method, Builder | Shared catalog, regular/promotional checkout creators, order builder |

```text
tmps-labs/
├── pom.xml                  # Parent and module list
├── mvnw / mvnw.cmd          # Maven Wrapper
├── .mvn/wrapper/
├── lab1/
│   ├── README.md
│   ├── pom.xml              # Runnable JAR configuration
│   └── src/main/java/md/tmps/shop/
└── lab2/
    ├── README.md
    ├── pom.xml              # Runnable JAR configuration
    └── src/main/java/md/tmps/shop/
```