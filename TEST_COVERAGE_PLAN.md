# Basic Test Coverage Plan

## Goals

- Add basic, confidence-building automated test coverage without changing production code.
- Prefer deterministic tests over brittle snapshot-style assertions.
- Keep each phase independently shippable and useful on its own.
- Limit any non-test code change to the minimum needed for testability, and only if a test seam cannot be introduced another way.

## Current State

- Build tool: Maven
- Test framework already present: JUnit 4
- Existing tests:
  - `SunPositionCalculatorTest`
  - `DateTimeUtilsTest`
- Main code areas:
  - Utility helpers in `utils`
  - Domain/data classes in `domain`
  - Catalog lookups for planets and stars
  - Position calculators for sun, moon, planets, and stars

## Testing Principles

- Test public behavior first.
- Favor fixed dates, times, and observatory locations.
- Assert stable invariants where exact astronomical values are hard to verify safely.
- Avoid coupling tests to internal implementation details.
- Add test-only helpers/fixtures under `src/test/java` rather than modifying production code.

## Phase 1: Utility And Domain Baseline

### Scope

- `DateTimeUtils`
- `FormatUtils`
- `MathUtils`
- `CollectionUtils`
- `Place`
- `Observatory`

### What This Phase Covers

- Date/time conversions and timezone-sensitive behavior.
- Formatting and parsing helpers for angles, times, and site names.
- Core trig and coordinate conversion helpers using deterministic assertions.
- Domain object sign handling for latitude/longitude and copy behavior.
- Singleton collector success and failure behavior.

### Test Style

- Mostly pure unit tests.
- Fixed inputs with exact or tolerance-based numeric assertions.
- No calculator-level integration needed.

### Exit Criteria

- Utility methods with branching logic have direct tests.
- `Place` and `Observatory` construction/copy behavior is covered.
- Existing `DateTimeUtilsTest` remains and is expanded rather than replaced.

### Why This Phase Is Self-Contained

- It adds value immediately by locking down deterministic behavior used across the whole library.
- It can ship without depending on any later calculator tests.

## Phase 2: Catalog And Lookup Coverage

### Scope

- `PlanetCatalog`
- `StarCatalog`

### What This Phase Covers

- Successful lookup by canonical and case-insensitive names/ids.
- Expected failure behavior when no item matches.
- Expected failure behavior when lookup is ambiguous.
- Basic sanity checks for returned catalog objects.

### Test Style

- Unit tests against public static lookup APIs.
- Assertions focus on lookup contracts, not the full astronomical dataset.

### Exit Criteria

- Common lookup paths are covered for at least one planet and several representative stars.
- Error behavior from `singletonCollector()`-backed lookups is explicitly documented in tests.

### Why This Phase Is Self-Contained

- It validates a complete public surface area that other code depends on.
- It can land independently without touching calculator tests.

## Phase 3: Sun And Moon Calculator Behavioral Coverage

### Scope

- `SunPositionCalculator`
- `MoonPositionCalculator`

### What This Phase Covers

- Returned objects are populated with expected core fields.
- Rise/set results are expressed in the observatory timezone.
- Rise/set status behavior for normal and edge-case locations/dates where practical.
- `getPosition(obs, time)` updates behavior correctly.
- `getEphemeris(...)` includes both endpoints and respects interval stepping.

### Test Style

- Small integration-style tests using fixed observatories and dates.
- Assert invariants such as:
  - non-null fields
  - valid status values
  - rise before set for normal cases
  - ephemeris list size and boundary timestamps
- Only use exact astronomical numeric assertions where the expected value is already well-established and stable.

### Exit Criteria

- Sun coverage expands beyond the current timezone regression test.
- Moon calculator has basic confidence tests for position output and rise/set contracts.
- No production mocks required unless a hard test seam issue is discovered.

### Why This Phase Is Self-Contained

- It fully covers two public calculators that users can consume directly.
- It does not rely on planet or star coverage work.

## Phase 4: Planet And Star Calculator Behavioral Coverage

### Scope

- `PlanetPositionCalculator`
- `StarPositionCalculator`

### What This Phase Covers

- Happy-path position generation for representative objects.
- Field population and formatting contracts.
- Rise/transit/set behavior and status handling for planets.
- Ephemeris generation boundaries and interval handling.
- Coordinate conversion smoke coverage through public calculator entry points.

### Test Style

- Integration-style tests with fixed catalog objects and observatories.
- Prefer contract assertions over tightly coupled exact numeric expectations.

### Exit Criteria

- At least one representative planet and one representative star have end-to-end calculator tests.
- Planet rise/set/transit outputs are covered for a normal case.
- Ephemeris generation behavior is covered for both calculators.

### Why This Phase Is Self-Contained

- It completes baseline coverage for the remaining public calculators.
- It provides standalone value even if no further tooling work is done.

## Phase 5: Coverage Visibility And Maintenance Guardrails

### Scope

- Test execution ergonomics
- Optional reporting setup in Maven
- Basic contributor guidance

### What This Phase Covers

- Add or document a standard local command for running all tests.
- Optionally add a lightweight coverage report plugin such as JaCoCo.
- Document the intended testing strategy in a short contributor note or README section.
- Optionally add a modest coverage threshold only if the new suite is stable enough to avoid churn.

### Test Style

- No new production tests required.
- Build/reporting configuration only.

### Exit Criteria

- Contributors can run the suite and inspect coverage consistently.
- Any threshold, if added, is intentionally low and meant to prevent regression rather than force broad rewrites.

### Why This Phase Is Self-Contained

- It improves sustainability of the test suite without depending on future production work.
- It can be skipped if the project only wants tests and not coverage reporting.

## Recommended Order

1. Phase 1
2. Phase 2
3. Phase 3
4. Phase 4
5. Phase 5

## Suggested Initial Coverage Targets

- Start with breadth, not deep numerical verification.
- Prioritize public APIs and branching behavior over raw line count.
- Treat these as guidance, not strict gates:
  - Phase 1: strong coverage on utility branches
  - Phases 3-4: basic happy-path and boundary coverage for each calculator

## Risks And Watchouts

- Some astronomical outputs may be sensitive to rounding and timezone assumptions.
- A few methods appear to have legacy formatting/parsing behavior; tests should lock in current behavior before any cleanup.
- Catalog lookups may intentionally throw on zero-or-multiple matches; tests should document that contract rather than mask it.
- If a calculator is hard to test without production changes, prefer a very small seam over broad refactoring.

## Out Of Scope For This Plan

- Refactoring production code for style or architecture.
- Rewriting calculation algorithms.
- Introducing heavyweight mocking frameworks.
- Raising strict coverage gates before the suite proves stable.
