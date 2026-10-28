# Boekenwinkel

Dit project is een webapplicatie voor een fictieve boekenwinkel. Klanten kunnen boeken bekijken en reserveren. Een beheerder beoordeelt de reserveringen. De applicatie is opgebouwd uit twee Spring Boot-apps en een aparte frontend.

## Functionaliteit

- De catalogus toont boeken, auteurs, ISBN, omschrijving, prijs en voorraad.
- Klanten kunnen een profiel aanmaken, inloggen en boeken reserveren.
- De voorraad daalt bij een reservering. Als de beheerder de reservering afwijst, wordt de voorraad teruggezet.
- De beheerder kan reserveringen goedkeuren of afwijzen.
- Na goedkeuring krijgt de klant op de website een bericht met de afhaaldatum. De website verstuurt geen echte e-mail.
- In het profiel staan de reserveringen en gekochte boeken. Klanten kunnen een review met score en tekst plaatsen voor een gekocht boek.
- Reviews zijn zichtbaar op de detailpagina van het boek.

## Technologieën

| Onderdeel | Technologie |
| --- | --- |
| Frontend | HTML, CSS en JavaScript |
| Backend | Java 17, Spring Boot en Maven |
| REST API | Spring Web MVC |
| Databasekoppeling | Spring Data JPA en Hibernate |
| Validatie | Jakarta Bean Validation |
| Database | MySQL |
| Tests | JUnit en Mockito |

## Projectstructuur

```text
Examen_AA_Yassine/
├── catalog-service/  Boeken en voorraad, Spring Boot-project (poort 8081)
├── backend/          Gebruikers, reserveringen en reviews (poort 8080)
│   └── src/test/     Unit tests
├── frontend/         HTML, CSS en JavaScript
└── documentation/    API- en architectuurdocumentatie
```

De frontend praat met de reserveringsservice op poort 8080. Die service praat via REST met de catalogusservice op poort 8081. Beide services gebruiken voor deze demo de MySQL-database `boekenwinkel` en beheren hun eigen tabellen.

## Demo-inlog en beperkingen

De admin-demo gebruikt `admin@boek.com` met wachtwoord `test1234`. Klantwachtwoorden worden in deze eenvoudige cursusdemo als gewone tekst bewaard. Er is geen sessie- of tokenbeheer en er wordt geen echte e-mail verstuurd. Gebruik deze demo-inlog niet voor echte accounts.

## Benodigdheden

- Java 17
- MySQL Server
- IntelliJ IDEA of een andere Java-ontwikkelomgeving

Voor de frontend is geen aparte installatie van Node.js nodig; Spring Boot serveert de bestanden uit de map `frontend`.

## Installatie en starten

### 1. Database instellen

Start MySQL en maak de database aan:

```sql
CREATE DATABASE boekenwinkel;
```

De database-instellingen staan in `application.properties` van `backend` en `catalog-service`. Pas de gebruikersnaam en het wachtwoord aan jouw MySQL-installatie aan. De demo gebruikt standaard `root` met wachtwoord `test1234`; je kunt ook de omgevingsvariabele `DB_PASSWORD` instellen.

### 2. Catalogusservice starten

Open `catalog-service/pom.xml` als Maven-project in IntelliJ en start `CatalogusApplication`. Deze service draait op `http://localhost:8081`.

### 3. Backend en website starten

Open `backend/pom.xml` als Maven-project in IntelliJ en start `ExamenAaYassineApplication`. De backend en website draaien op `http://localhost:8080`.

Start eerst de catalogusservice en daarna de backend. MySQL moet tijdens het starten actief zijn.

### 4. Tests uitvoeren

Op Windows kun je vanuit de hoofdmap de backendtests uitvoeren met:

```powershell
backend\mvnw.cmd test
```

Er staan vijf testmethodes in `backend/src/test/java`: drie voor registratie en login en twee voor reserveringsafhandeling. Voer de tests uit om hun actuele resultaat te zien.

## API en architectuur

De REST-endpoints en voorbeeldrequests staan in [documentation/api.md](documentation/api.md). Een schema en uitleg over de services staan in [documentation/architectuur.md](documentation/architectuur.md).

## Planning

| Stap | Werk | Status |
| --- | --- | --- |
| 1 | Cursusmateriaal en Davids voorbeelden bekijken | Klaar |
| 2 | Spring Boot Maven-backend opzetten met de mapindeling van `demoSnackbar` | Klaar |
| 3 | Boeken, gebruikersprofielen, reviews en reserveringen modelleren met JPA | Klaar |
| 4 | REST-endpoints voor catalogus, profielen, reserveringen en reviews toevoegen | Klaar |
| 5 | Voorraad verlagen bij reserveren; admin laat reserveringen goedkeuren of afwijzen; klant krijgt na goedkeuring een websitebericht | Klaar |
| 6 | Minstens drie unit tests schrijven en uitvoeren | Vijf tests aanwezig; opnieuw uitvoeren na de laatste wijziging |
| 7 | Gebruikersregistratie en logincontrole toevoegen | Klaar |
| 8 | Frontend als apart project uitwerken | Klaar |
| 9 | Catalogus- en reserveringsservice via REST laten communiceren | Uitgewerkt |
| 10 | Project testen en documentatie afronden | Tests opnieuw uitvoeren; README, API- en architectuurdocumentatie aanwezig |

## Geraadpleegde cursusmaterialen en voorbeelden

- Application Architecture PRG2-cursusmateriaal: `00_Intro.pptx`, `01_Maven.pptx`, `02_ORM.pptx`, `REST_Spring.pptx`, `theorie_testing.pptx` en `design_principles.pptx`.
- [demoSpring](https://github.com/David-VS/demoSpring): controllers, modellen en constructor-injectie.
- [demoSnackbar](https://github.com/David-VS/demoSnackbar): REST-controllers, entiteiten, repositories, relaties en validatie.
- [demoTesting](https://github.com/David-VS/demoTesting): JUnit-teststijl.
- [oef_module2](https://github.com/David-VS/oef_module2) en [oef_module3](https://github.com/David-VS/oef_module3): cursusrepositories.
