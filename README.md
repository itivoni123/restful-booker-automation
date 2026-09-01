# Restful Booker Automation

Automation framework for the Restful Booker Platform.

Built with Java 17, Maven, JUnit 5, Playwright and REST Assured.

## Test Coverage

### API

The API suite validates:

1. Successful authentication with valid credentials.
2. Authentication rejection with invalid credentials.
3. Booking creation followed by retrieval and meaningful field validation.
4. Booking update of multiple fields followed by GET verification.
5. Rejection of a protected booking update without an authentication token.

### UI

The UI suite validates:

1. Searching for an available room and completing a successful booking.
2. Attempting a booking with missing mandatory information and verifying rejection.

### API to UI

The integration scenario:

1. Authenticates through the API.
2. Creates a booking through the API.
3. Keeps the generated booking data within the test.
4. Searches the same date range through the UI.
5. Verifies that the booked room is no longer offered as available.

This verifies that state created through one application layer is reflected through another layer.

## Project Structure

    src/test/java/com/restfulbooker/automation
    ├── api
    │   ├── client
    │   ├── model
    │   └── tests
    ├── config
    ├── data
    ├── integration
    └── ui
        ├── base
        ├── pages
        ├── support
        └── tests

## Architecture

The framework separates test intent from implementation details:

- Test classes describe scenarios and assertions.
- API clients own HTTP request construction and endpoint interaction.
- Page Objects own UI locators and user interactions.
- BookingDataFactory owns dynamic test-data generation.
- ConfigManager owns environment configuration.
- BaseUiTest owns browser lifecycle.
- ScreenshotOnFailureExtension provides failure diagnostics.

This keeps tests readable while avoiding duplicated request and browser interaction logic.

## Running the Tests

### Prerequisites

- Java 17+
- Maven
- Git

Verify the environment with:

    java -version
    mvn -version

### Install Playwright Chromium

Compile the test classes:

    mvn test-compile

Install Chromium:

    mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium" -Dexec.classpathScope=test

### Run the complete suite

    mvn clean test

### Run API tests

    mvn test -Dtest=AuthenticationApiTest,BookingApiTest

### Run UI tests

    mvn test -Dtest=BookingUiTest

### Run API-to-UI integration

    mvn test -Dtest=ApiToUiBookingTest

### Headed browser execution

    mvn test -Dheadless=false

Default configuration is stored in:

    src/test/resources/config.properties

## Test Data and Isolation

The Restful Booker Platform is a shared public environment. Relying on fixed bookings would make tests vulnerable to collisions and external state.

Each scenario therefore creates the data it requires.

The framework uses:

- unique guest identifiers;
- dynamically generated future dates;
- test-local booking IDs;
- no shared mutable static booking state.

API booking tests omit optional email and phone values unless those fields are relevant to the scenario. Supplying them causes the application to involve messaging functionality, introducing an unrelated dependency.

UI tests also generate different future booking dates instead of repeatedly booking a fixed range.

This improves isolation and makes future parallel execution safer.

## Waiting and Locator Strategy

The UI automation does not use hard sleeps.

Playwright's built-in waiting behavior and locator waits are used instead.

Locators favor application intent. The home page contains multiple links labelled "Book Now", so room selection targets reservation links by destination pattern rather than relying only on ambiguous visible text.

Validation errors are scoped to the application's error alert rather than every generic ARIA alert on the page.

## Parallel Execution

The suite is designed with parallel execution in mind:

- every UI test owns its own BrowserContext;
- generated booking data belongs to the individual test;
- booking IDs are not stored globally;
- API-to-UI data is transferred in memory inside the same test;
- tests do not depend on execution order.

At larger scale, controlled JUnit parallelism and a dedicated test-data allocation mechanism would be added.

## Scaling to 1,000 Tests and 5 Engineers

For a larger framework I would add:

- domain-oriented test modules;
- JUnit tags for smoke, regression, API, UI and integration suites;
- parallel execution and CI sharding;
- typed test-data builders and reusable fixtures;
- dedicated test-data provisioning and cleanup;
- environment-specific configuration and secret management;
- Playwright tracing and centralized reporting;
- controlled retry policies only for identified transient failures;
- code ownership and review conventions;
- reusable component objects where genuine UI reuse exists;
- stable application test identifiers where available.

The framework should evolve through demonstrated reuse rather than adding generic abstractions prematurely.

## CI/CD

GitHub Actions executes the suite on pushes, pull requests and manual runs.

The workflow:

1. Checks out the repository.
2. Configures Java 17.
3. Caches Maven dependencies.
4. Compiles the test code.
5. Installs Chromium and browser dependencies.
6. Runs the complete suite headlessly.
7. Uploads Surefire reports.
8. Uploads screenshots on failures.

## Known Limitations and Improvements

The tests run against a public demonstration environment.

Limitations include:

- external users can modify application state;
- the service periodically resets data;
- authentication tokens have a limited lifetime;
- network latency is outside the framework's control;
- dynamically selected future dates reduce but cannot completely eliminate external booking collisions.

In a production-grade environment I would prefer deterministic test-data provisioning, cleanup APIs, controlled environments, explicit request timeouts, richer observability and CI parallelization.

## Failure Diagnostics

Maven reports:

    target/surefire-reports

UI failure screenshots:

    target/screenshots
