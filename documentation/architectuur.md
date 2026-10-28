# Architectuur

Dit document toont hoe de frontend, catalogusservice en reserveringsservice samenwerken.

## Projectmappen

```text
Examen_AA_Yassine/
├── catalog-service/  Spring Boot Maven-project (poort 8081)
├── backend/          Spring Boot Maven-project (poort 8080)
├── frontend/         HTML-, CSS- en JavaScript-project
└── documentation/    API en architectuur
```

## Services en communicatie

```text
HTML/CSS/JavaScript frontend
    ├── HTTP/REST ──> Catalogusservice (8081)
    │                  └── boeken en voorraad
    └── HTTP/REST ──> Reserveringsservice (8080)
                       ├── gebruikers, reserveringen, aankopen en reviews
                       └── REST-client roept catalogusservice aan

Beide services gebruiken MySQL database `boekenwinkel`.
```

De catalogusservice beheert boeken en voorraad. De reserveringsservice beheert profielen, reserveringen, gekochte boeken en reviews. Bij een reservering vraagt de reserveringsservice via HTTP/REST aan de catalogusservice om de voorraad te verlagen. Bij afwijzing vraagt hij de catalogusservice om de voorraad terug te zetten. De frontend vraagt boeken en voorraad rechtstreeks op bij de catalogusservice.

De services zijn aparte Maven/Spring Boot-projecten en draaien op verschillende poorten. Voor deze cursusdemo gebruiken ze dezelfde MySQL-database om de bestaande boeken en database te behouden. Elke service beheert eigen tabellen; wijzigingen aan boeken en voorraad gaan via REST.

De REST-aanvragen gebruiken de HTTP-methoden, URI's en responsecodes uit `REST_Spring.pptx`. De Java REST-client is toegevoegd om de twee Spring-apps met elkaar te laten communiceren.

## Entiteiten

- `Book`: in de catalogusservice; ISBN als unieke sleutel, titel, auteur(s), omschrijving, prijs en voorraad.
- `User`: in de reserveringsservice; profielgegevens en reserveringen.
- `Reservation`: in de reserveringsservice; gebruiker, ISBN en titel van het boek, aantal en status.
- Gekochte boeken: ISBN-nummers in de bestaande `user_purchased_books`-collectie van een gebruiker.
- `Review`: in de reserveringsservice; score en tekst gekoppeld aan een gebruiker en ISBN.

De voorraad daalt meteen wanneer de klant een reservering plaatst. Een admin kan de reservering goedkeuren of afwijzen. Bij afwijzing wordt de voorraad teruggezet via een REST-aanvraag. Bij goedkeuring wordt de afhaaldatum op de volgende dag gezet en komt er een bericht op de website. Een belletje in de navigatie toont hoeveel afhaalberichten de klant heeft. De klant hoeft geen afhaling meer te bevestigen in de website. Omdat de opdracht ervan uitgaat dat gereserveerde boeken worden afgehaald, komt een goedgekeurd boek in de lijst met gekochte boeken. Een review kan alleen worden opgeslagen als het boek in het profiel als gekocht staat. Er wordt geen echte e-mail verstuurd.

## Loginbeperking

De klantlogin controleert de ingegeven e-mail en het wachtwoord via de gebruikersrepository. Het admin-dashboard gebruikt de vaste demo-inlog `admin@boek.com` / `test1234`; admin-endpoints controleren de adminheaders. Het wachtwoord van klantprofielen wordt in deze eenvoudige demonstratie als gewone tekst opgeslagen en er is geen sessie- of tokenbeheer. De reserverings- en profiel-endpoints gebruiken een `userId`; deze basislogin is alleen geschikt als demonstratie en niet als beveiliging voor een publiek systeem.

## Startvolgorde

Start eerst MySQL, daarna `CatalogusApplication` op poort 8081 en vervolgens `ExamenAaYassineApplication` op poort 8080. De reserveringsservice kan geen reservering verwerken als de catalogusservice niet draait.
