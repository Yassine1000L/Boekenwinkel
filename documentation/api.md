# API's

Dit document beschrijft de REST-aanvragen die de frontend en services gebruiken.

De catalogusservice draait op `http://localhost:8081`. De reserveringsservice en de frontend draaien op `http://localhost:8080`.

De controllers gebruiken GET, POST, PUT en DELETE zoals in Davids REST-voorbeeld. De reserveringsservice gebruikt een REST-client om de catalogus-API aan te roepen.

## Catalogusservice (poort 8081)

| Methode | URI | Werking |
|---|---|---|
| GET | `/books` | Geef de catalogus terug |
| GET | `/books/{isbn}` | Geef één boek terug |
| GET | `/books/search?title=...` | Zoek op titel |
| POST | `/books?isbn=...&title=...&authors=...&description=...&price=...&stock=...` | Voeg een boek toe |
| PUT | `/books/{isbn}/stock?stock=...` | Stel voorraad in |
| PUT | `/books/{isbn}/reserve?quantity=...` | Verlaag voorraad bij reserveren |
| PUT | `/books/{isbn}/release?quantity=...` | Zet voorraad terug bij afwijzen |
| DELETE | `/books/{isbn}` | Verwijder een boek |

## Reserveringsservice (poort 8080)

### Gebruikersprofielen

| Methode | URI | Werking |
|---|---|---|
| GET | `/users` | Geef de profielen terug |
| GET | `/users/{id}` | Geef een profiel met reserveringen, aankopen en reviews terug |
| POST | `/users/register?name=...&email=...&password=...` | Registreer een gebruiker |
| POST | `/users/login?email=...&password=...` | Controleer gebruikersgegevens |
| POST | `/users/admin/login?email=...&password=...` | Controleer de vaste admin-demo-inloggegevens |

### Reserveringen

| Methode | URI | Werking |
|---|---|---|
| GET | `/reservations` | Geef reserveringen terug; vereist `X-Admin-Email` en `X-Admin-Password` |
| POST | `/reservations?userId=...&isbn=...&quantity=...` | Maak reservering en vraag catalogusservice om voorraad te verlagen |
| PUT | `/reservations/{id}/approve` | Admin keurt goed |
| PUT | `/reservations/{id}/reject` | Admin wijst af en laat catalogusservice voorraad terugzetten |
| PUT | `/reservations/{id}/confirm` | Oud endpoint; de huidige website vraagt de klant niet om afhalen te bevestigen |

Adminacties gebruiken de headers `X-Admin-Email: admin@boek.com` en `X-Admin-Password: test1234`.

### Reviews

| Methode | URI | Werking |
|---|---|---|
| GET | `/reviews` | Geef alle reviews terug |
| GET | `/reviews?isbn=...` | Geef reviews van een boek terug |
| POST | `/reviews?userId=...&isbn=...&score=...&description=...` | Voeg een review toe als de gebruiker het boek gekocht heeft |

Na goedkeuring bewaart de backend de afhaaldatum voor de volgende dag. De klant ziet een melding op de website en een teller op het belletje in de navigatie. Er wordt geen echte e-mail verstuurd. In deze demo wordt een goedgekeurd boek aan de gekochte boeken toegevoegd, omdat de opdracht ervan uitgaat dat gereserveerde boeken worden afgehaald.
