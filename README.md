# xis-end-to-end-tests

End-to-end tests for the XIS web framework.  
XIS is loaded as a **real Maven dependency** (from `mavenLocal()`), not as a project reference.

## Voraussetzungen

1. XIS lokal gebaut und ins lokale Maven-Repo installiert:
   ```bash
   cd ../xis && ./gradlew publishToMavenLocal
   ```
2. Java 17+
3. Beim ersten Playwright-Lauf muss der passende Browser heruntergeladen werden.

## Modulstruktur

```
e2e-app-shared/      Gemeinsame XIS-App (Pages, Frontlets, Formulare, Templates)
e2e-app-boot/        Schlanker XIS-Boot Runner für die gemeinsame App
e2e-app-spring/      Schlanker Spring-Boot Runner für die gemeinsame App

e2e-tests-core/      Playwright-Tests gegen die Plattform-Runner
```

## Tests ausführen

Alle Suiten:
```bash
./gradlew test
```

Einzelne Suite:
```bash
./gradlew :e2e-tests-core:test
```

Dieselbe Suite gegen Spring:
```bash
./gradlew :e2e-tests-core:springTest
```

## Wie es funktioniert

1. Gradle baut zuerst das fat-jar des jeweiligen Plattform-Runners.
2. Die Test-Suite startet das jar als lokalen Prozess auf einem freien Port.
3. Playwright öffnet einen headless Chromium-Browser gegen diesen Prozess.
4. Nach den Tests wird der Prozess automatisch gestoppt.

## Neue Test-Suite hinzufügen

1. Neues Modul `e2e-tests-<feature>/` anlegen.
2. In `settings.gradle` eintragen: `include 'e2e-tests-<feature>'`.
3. `build.gradle` nach dem Muster von `e2e-tests-core/build.gradle` anlegen.
4. Basisklasse von `BootAppE2ETest` ableiten.
