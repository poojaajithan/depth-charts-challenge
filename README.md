# FanDuel - NFL Depth Charts

This project implements an in-memory Depth Chart Manager for sports teams. It provides a robust, strictly typed, and encapsulated API to add players, remove players, and query backups across various positions.

## How to Build and Run

### Prerequisites
* Java 17 or higher
* Maven 

### Running the Application
To see the application execute the sample inputs provided in the requirements:
1. Navigate to the root directory.
2. Compile and run the `Main.java` class located at `src/main/java/com/trading/depthcharts/Main.java`.
   ```bash
   javac src/main/java/com/trading/depthcharts/**/*.java
   java -cp src/main/java com.trading.depthcharts.Main

Running the Tests
Automated unit tests have been implemented using JUnit 5 to verify edge cases, out-of-bounds insertions, and encapsulation security.

Run your IDE's built-in test runner on DepthChartManagerTest.java

Or via Maven: mvn test

Assumptions & Design Decisions
1. The Domain Model (Java 17 Records)
The requirements state: "Assume that a number within the team uniquely identifies that player." To enforce this, Player is implemented as an immutable Java 17 record. The default equals() and hashCode() methods were explicitly overridden to evaluate only the player's number. This allows the system to easily identify and remove existing players from lists without relying on identical object references in memory.

2. Internal Data Structure
The core state is managed via a LinkedHashMap<String, List<Player>>.

O(1) Lookups: Provides instant access to any position's depth chart.

Insertion Order Preservation: LinkedHashMap guarantees that when getFullDepthChart() is called, the positions are printed in the exact order they were initially added to the system.

3. Strict Typing for Removal (Method Signature Adjustment)
The prompt requested a removal method that returns the removed player on success, but an empty list on failure. Returning a single object on success and a Collection on failure breaks Java's strict typing and requires returning null or Object. To maintain type safety, removePlayerFromDepthChart returns a List<Player>. It securely returns List.of(removedPlayer) on success, and Collections.emptyList() if the player is not found.

4. Encapsulation & Memory Safety
When querying backups via getBackups, returning a direct List.subList() exposes a mutable view of the internal Depth Chart. If a client clears or modifies that returned list, it corrupts the internal state. To prevent this encapsulation leak, the sublist is safely wrapped in a new ArrayList before being returned.

5. Design Decision: Console Output vs. Logging Frameworks
To ensure this project is lightweight, dependency-free, and easy for the reviewing panel to compile and run out of the box, standard System.out was used for the requested print statements. In a production environment (such as a Spring Boot application), this would be replaced with a standard logging facade like SLF4J.

Scaling & Architecture
The "Important Notes" section asked how this solution would scale to handle all NFL teams and other sports (MLB, NHL, NBA).

1. Composition Over Modification
The DepthChartManager is currently designed to be perfectly sport-agnostic. It purely handles the mathematics of arrays, insertions, and shifting. To scale this system to the entire league, I would use Composition:

A LeagueManager contains a Map<String, Team>.

A Team entity contains its own independent instance of DepthChartManager.
This adheres to the Single Responsibility Principle, ensuring the Depth Chart logic remains completely isolated from League or Roster constraints.

2. Primitive Obsession vs. Contract Strictness
To strictly adhere to the requested sample API (e.g., addPlayerToDepthChart("QB", player, 0)), the position parameter is kept as a raw String. However, relying on Strings introduces the risk of runtime typos (e.g., "QXB").

In a production environment, I would refactor this to use interface-driven Enums to enforce compile-time safety across different sports:

Java
public interface Position { String name(); }
public enum NFLPosition implements Position { QB, LWR, RB }
public enum NBAPosition implements Position { PG, SG, PF }

Notes on Sample Data Corrections
While implementing the provided test cases from the requirements document, a few minor typos in the sample data were corrected in my Main.java execution to ensure accurate testing:

Backup Queries: Jaelon Darden and Mike Evans were added to "LWR", but the sample outputs queried their backups under "QB". The test execution maps them correctly to "LWR".

Removal Query: Mike Evans was added to "LWR", but the sample removal command attempted to remove him from "WR". This was corrected to "LWR".


***
