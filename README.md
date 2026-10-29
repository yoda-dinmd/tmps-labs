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
```

On Windows, use `mvnw.cmd clean verify`, followed by the same `java -jar` command.
If Maven is already installed, `mvn` can replace `./mvnw`.

Build or test only lab 1:

```sh
./mvnw -pl lab1 -am package
./mvnw -pl lab1 test
```

## Modules

| Module | Topic | Application |
| --- | --- | --- |
| [lab1](lab1/README.md) | SRP, OCP, ISP | Product catalog, cart, discounts, checkout, receipts |

```text
tmps-labs/
├── pom.xml                  # Parent and module list
├── mvnw / mvnw.cmd          # Maven Wrapper
├── .mvn/wrapper/
└── lab1/
    ├── pom.xml              # Runnable JAR configuration
    └── src/
        ├── main/java/md/tmps/shop/
        └── test/java/md/tmps/shop/
```

## IntelliJ IDEA

Open the root `pom.xml` as a Maven project, or use **Add as Maven Project** on it
in the already-open folder. Set the project SDK to JDK 17 or newer and select the
Maven Wrapper in Maven settings. Reload Maven, then run `md.tmps.md.tmps.shop.Main` from
`lab1`. IntelliJ metadata stays local through `.gitignore`.

## Adding the next laboratory

1. Create `lab2/pom.xml` with the same parent coordinates as `lab1/pom.xml` and
   set its artifact ID to `lab2`.
2. Add `<module>lab2</module>` to the root POM's `<modules>` list.
3. Add code in `lab2/src/main/java` and tests in `lab2/src/test/java`.
4. Configure its `maven-jar-plugin` main class, following lab 1, so it has its own
   runnable JAR. Add the JUnit dependency if the module has tests.
5. Run `./mvnw clean verify` to build all laboratories, or
   `./mvnw -pl lab2 -am package` to build the new one and its required modules.

Keep each lab's implementation within its module so earlier submissions remain
runnable as the micro project develops.
