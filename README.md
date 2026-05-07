# xis-end-to-end-tests

End-to-end tests for the XIS web framework.  
XIS is loaded as a **real Maven dependency** (from `mavenLocal()`), not as a project reference.

## Voraussetzungen

1. XIS lokal gebaut und ins lokale Maven-Repo installiert:
   ```bash
   cd ../xis && ./gradlew publishToMavenLocal
   ```
2. Java 17+
3. Docker/Colima für Tests mit echten externen Diensten wie Keycloak.
4. Beim ersten Playwright-Lauf muss der passende Browser heruntergeladen werden.

Colima genügt als Docker-Laufzeit:
```bash
colima start
docker info
```

Die Keycloak-Tests verwenden die Docker-CLI. Unter Colima öffnen sie bei Bedarf automatisch einen lokalen SSH-Tunnel zum
Container-Port. Wenn eine andere Docker-Laufzeit verwendet wird, muss sie für normale Docker-CLI-Aufrufe sichtbar sein.

## Modulstruktur

```
e2e-app-shared/      Gemeinsame XIS-App (Pages, Frontlets, Formulare, Templates)
e2e-app-boot/        Schlanker XIS-Boot Runner für die gemeinsame App
e2e-app-spring/      Schlanker Spring-Boot Runner für die gemeinsame App
e2e-app-distributed-shared/  Gemeinsame App-Klassen für verteilte Laufzeit-Tests
e2e-app-distributed-page/    Shell/Page-Prozess für verteilte Laufzeit-Tests
e2e-app-distributed-remote/  Remote-Prozess für verteilte Pages und Frontlets
e2e-app-distributed-sso-shared/   Gemeinsame App-Klassen für verteilte SSO-Tests
e2e-app-distributed-sso-shell/    Geschützte Shell-App für verteilte SSO-Tests
e2e-app-distributed-sso-remote/   Geschützte Remote-App für verteilte SSO-Tests

e2e-tests-core/      Playwright-Tests gegen die Plattform-Runner
e2e-tests-security/  Playwright-Tests für Authentifizierung, IDP und SSO
```

## Tests ausführen

Alle Suiten:
```bash
./gradlew test
```

Vollständige Verifikation inklusive Spring, verteilter Laufzeit, verteilter SSO-Suite und Keycloak:
```bash
./gradlew check
```

Einzelne Suite:
```bash
./gradlew :e2e-tests-core:test
```

Dieselbe Suite gegen Spring:
```bash
./gradlew :e2e-tests-core:springTest
```

Verteilte Page/Frontlet-Suite mit zwei Boot-Prozessen:
```bash
./gradlew :e2e-tests-core:distributedTest
```

Verteilte SSO-Suite mit XIS-IDP, Shell-Prozess und Remote-Prozess:
```bash
./gradlew :e2e-tests-security:distributedSsoTest
```

Keycloak-Suite mit echtem Keycloak-Container:
```bash
./gradlew :e2e-tests-security:keycloakTest
```

Diese Suite prüft den realen externen OpenID-Connect-Flow ohne lokales Loginformular: XIS leitet direkt zu Keycloak
weiter, verarbeitet den Callback und rendert danach die geschützte Seite mit dem `sub`-Claim als `@UserId`.

Google ist als manueller Test vorbereitet, bleibt aber disabled, weil dafür private OAuth-Credentials und ein
interaktiver Login nötig sind. Dafür wird kein API-Key verwendet, sondern ein Google OAuth/OpenID-Connect Client mit
Client-ID und Client-Secret. In Google Cloud muss die lokale XIS-Callback-URL als Redirect-URI zugelassen werden:

```text
http://localhost:58080/xis/auth/callback/google
```

Der manuelle Google-Test verwendet standardmäßig Port `58080`, damit die Redirect-URI in Google fest eingetragen werden
kann. Bei Bedarf kann der Port mit `-De2e.google.app.port=<port>` geändert werden.

Start lokal:
```bash
./gradlew :e2e-tests-security:googleManualTest \
  -De2e.google.enabled=true \
  -De2e.google.client.id=<client-id> \
  -De2e.google.client.secret=<client-secret>
```

Der Test startet standardmäßig einen sichtbaren Browser, damit der Google-Login manuell abgeschlossen werden kann. Er ist
absichtlich nicht Teil von `check`, weil er echte Zugangsdaten und Benutzerinteraktion braucht.

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
