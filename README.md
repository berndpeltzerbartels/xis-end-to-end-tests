# xis-end-to-end-tests

End-to-end tests for the XIS web framework.  
XIS is loaded as a **real Maven dependency** (from `mavenLocal()`), not as a project reference.

## Voraussetzungen

1. XIS lokal gebaut und ins lokale Maven-Repo installiert:
   ```bash
   cd ../xis && ./gradlew publishToMavenLocal
   ```
2. Java 17+

## Modulstruktur

```
e2e-app-boot/        XIS-Boot Testanwendung (Navigation, Frontlets, Formular, Counter)
e2e-app-spring/      Spring-Boot Testanwendung (Platzhalter)

e2e-tests-core/      Tests gegen e2e-app-boot  (Playwright)
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

## Wie es funktioniert

1. Gradle baut zuerst das fat-jar der Test-App.
2. Die Test-Suite startet das jar als lokalen Prozess auf einem freien Port.
3. Playwright öffnet einen headless Chromium-Browser gegen diesen Prozess.
4. Nach den Tests wird der Prozess automatisch gestoppt.

## Neue Test-Suite hinzufügen

1. Neues Modul `e2e-tests-<feature>/` anlegen.
2. In `settings.gradle` eintragen: `include 'e2e-tests-<feature>'`.
3. `build.gradle` nach dem Muster von `e2e-tests-core/build.gradle` anlegen.
4. Basisklasse von `BootAppE2ETest` ableiten.
