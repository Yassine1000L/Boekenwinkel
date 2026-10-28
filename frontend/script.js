// De frontend wordt door Spring Boot vanaf hetzelfde adres geladen.
const apiAdres = window.location.origin;
const catalogusApiAdres = "http://localhost:8081";

let alleBoeken = [];
let huidigBoek = null;
let gebruiker = JSON.parse(localStorage.getItem("gebruiker"));
let admin = JSON.parse(sessionStorage.getItem("admin") || "null");
let mijnReserveringen = JSON.parse(localStorage.getItem("mijnReserveringen") || "[]");

const boekLijst = document.getElementById("book-list");
const melding = document.getElementById("notice");
let meldingTimer = null;

function toonMelding(tekst, fout = false, knopTekst = null, knopActie = null) {
    clearTimeout(meldingTimer);
    melding.replaceChildren();
    const bericht = document.createElement("span");
    bericht.textContent = tekst;
    melding.appendChild(bericht);
    melding.hidden = false;
    melding.classList.toggle("error", fout);
    if (knopTekst && knopActie) {
        const knop = document.createElement("button");
        knop.type = "button";
        knop.className = "notice-button";
        knop.textContent = knopTekst;
        knop.addEventListener("click", async function () {
            melding.hidden = true;
            await knopActie();
        });
        melding.appendChild(knop);
    }
    meldingTimer = setTimeout(function () {
        melding.hidden = true;
    }, knopTekst ? 8000 : 3500);
}

function toonPagina(id) {
    // Een ingelogde gebruiker krijgt het login- en registratieformulier niet te zien.
    if (id === "account" && gebruiker) {
        id = "profile";
    }
    if (id === "admin-dashboard" && !admin) {
        id = "admin-login";
    }
    document.querySelectorAll("main > .page").forEach(function (pagina) {
        const isHuidigePagina = pagina.id === id;
        pagina.hidden = !isHuidigePagina;
        pagina.style.display = isHuidigePagina ? "" : "none";
    });
    window.scrollTo(0, 0);
}

function toonNavigatie() {
    document.getElementById("profile-button").hidden = !gebruiker || Boolean(admin);
    document.getElementById("notification-button").hidden = !gebruiker || Boolean(admin);
    document.getElementById("logout-button").hidden = !gebruiker && !admin;
    document.getElementById("login-button").hidden = Boolean(gebruiker) || Boolean(admin);
    document.getElementById("admin-button").hidden = Boolean(admin);
    document.getElementById("admin-dashboard-button").hidden = !admin;
}

function maakDatumTekst(datum) {
    if (!datum) {
        const morgen = new Date();
        morgen.setDate(morgen.getDate() + 1);
        datum = [morgen.getFullYear(), String(morgen.getMonth() + 1).padStart(2, "0"), String(morgen.getDate()).padStart(2, "0")].join("-");
    }
    return new Date(datum + "T12:00:00").toLocaleDateString("nl-BE", { day: "numeric", month: "long", year: "numeric" });
}

function isMorgen(datum) {
    const morgen = new Date();
    morgen.setDate(morgen.getDate() + 1);
    const morgenTekst = [morgen.getFullYear(), String(morgen.getMonth() + 1).padStart(2, "0"), String(morgen.getDate()).padStart(2, "0")].join("-");
    return !datum || datum === morgenTekst;
}

function toonMeldingen(reserveringen) {
    const berichten = (reserveringen || []).filter(function (reservering) {
        return reservering.ready && !reservering.rejected && !reservering.confirmed;
    });
    const lijst = document.getElementById("notification-list");
    lijst.replaceChildren();
    document.getElementById("empty-notifications").hidden = berichten.length !== 0;
    const teller = document.getElementById("notification-count");
    teller.textContent = berichten.length > 9 ? "9+" : String(berichten.length);
    teller.hidden = berichten.length === 0;

    berichten.forEach(function (reservering) {
        const kaart = document.createElement("article");
        kaart.className = "notification-card";
        const bel = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        bel.setAttribute("viewBox", "0 0 24 24");
        bel.setAttribute("aria-hidden", "true");
        const pad = document.createElementNS("http://www.w3.org/2000/svg", "path");
        pad.setAttribute("d", "M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4");
        bel.appendChild(pad);

        const tekst = document.createElement("div");
        const titel = document.createElement("strong");
        titel.textContent = reservering.bookTitle || "Je gereserveerde boek";
        const bericht = document.createElement("p");
        bericht.textContent = "Je mag " + (isMorgen(reservering.pickupDate) ? "morgen, " : "op ") + maakDatumTekst(reservering.pickupDate) + " komen afhalen en betalen.";
        tekst.append(titel, bericht);
        kaart.append(bel, tekst);
        lijst.appendChild(kaart);
    });
}

async function openBerichten() {
    if (!gebruiker) return;
    try {
        const antwoord = await fetch(apiAdres + "/users/" + gebruiker.id);
        if (!antwoord.ok) throw new Error();
        const profiel = await antwoord.json();
        toonMeldingen(profiel.reservations || []);
        toonPagina("notifications");
    } catch (fout) {
        toonMelding("Je berichten konden niet geladen worden.", true);
    }
}

async function laadMeldingsteller() {
    if (!gebruiker) return;
    try {
        const antwoord = await fetch(apiAdres + "/users/" + gebruiker.id);
        if (!antwoord.ok) return;
        const profiel = await antwoord.json();
        toonMeldingen(profiel.reservations || []);
    } catch (fout) {
        // De berichten worden opnieuw geladen zodra de klant het belletje opent.
    }
}

function maakKnop(tekst, actie, extraClass = "") {
    const knop = document.createElement("button");
    knop.type = "button";
    knop.textContent = tekst;
    knop.className = extraClass;
    knop.addEventListener("click", actie);
    return knop;
}

function toonBoeken(boeken) {
    boekLijst.replaceChildren();
    document.getElementById("book-count").textContent = boeken.length + (boeken.length === 1 ? " boek gevonden" : " boeken gevonden");
    document.getElementById("empty-books").hidden = boeken.length !== 0;

    boeken.forEach(function (boek) {
        const kaart = document.createElement("article");
        kaart.className = "book-card";

        const omslag = document.createElement("div");
        omslag.className = "cover";
        const omslagTitel = document.createElement("span");
        omslagTitel.className = "cover-title";
        omslagTitel.textContent = boek.title;
        omslag.appendChild(omslagTitel);

        const info = document.createElement("div");
        info.className = "book-info";
        const titel = document.createElement("h3");
        titel.textContent = boek.title;
        const auteur = document.createElement("p");
        auteur.className = "book-author";
        auteur.textContent = boek.authors || "Auteur onbekend";
        const meta = document.createElement("div");
        meta.className = "book-meta";
        const prijs = document.createElement("strong");
        prijs.textContent = "€ " + Number(boek.price).toFixed(2);
        const voorraad = document.createElement("span");
        voorraad.className = boek.stock > 0 ? "stock" : "stock out";
        voorraad.textContent = boek.stock > 0 ? boek.stock + " op voorraad" : "Niet op voorraad";
        meta.append(prijs, voorraad);

        const knoppen = document.createElement("div");
        knoppen.className = "card-buttons";
        knoppen.appendChild(maakKnop("Details", function () { laadDetails(boek.isbn); }));
        if (!admin) {
            const reserveerKnop = maakKnop("Reserveer", function () { reserveerBoek(boek); });
            reserveerKnop.disabled = boek.stock < 1;
            knoppen.appendChild(reserveerKnop);
        }

        info.append(titel, auteur, meta, knoppen);
        kaart.append(omslag, info);
        boekLijst.appendChild(kaart);
    });
}

async function laadBoeken() {
    try {
        const antwoord = await fetch(catalogusApiAdres + "/books");
        if (!antwoord.ok) throw new Error();
        alleBoeken = await antwoord.json();
        toonBoeken(alleBoeken);
    } catch (fout) {
        toonMelding("De boeken konden niet geladen worden. Staat de catalogusservice op poort 8081 aan?", true);
    }
}

async function laadDetails(isbn) {
    try {
        const antwoord = await fetch(catalogusApiAdres + "/books/" + encodeURIComponent(isbn));
        if (!antwoord.ok) throw new Error();
        huidigBoek = await antwoord.json();
        const reviewAntwoord = await fetch(apiAdres + "/reviews?isbn=" + encodeURIComponent(isbn));
        huidigBoek.reviews = reviewAntwoord.ok ? await reviewAntwoord.json() : [];
        toonBoekDetails();
        toonPagina("details");
    } catch (fout) {
        toonMelding("De boekdetails konden niet geladen worden.", true);
    }
}

function toonBoekDetails() {
    const details = document.getElementById("book-details");
    details.replaceChildren();

    const layout = document.createElement("div");
    layout.className = "detail-layout";
    const omslag = document.createElement("div");
    omslag.className = "cover";
    const omslagTitel = document.createElement("span");
    omslagTitel.className = "cover-title";
    omslagTitel.textContent = huidigBoek.title;
    omslag.appendChild(omslagTitel);

    const tekst = document.createElement("div");
    tekst.className = "detail-copy";
    const label = document.createElement("p");
    label.className = "eyebrow";
    label.textContent = "BOEKDETAILS";
    const titel = document.createElement("h1");
    titel.textContent = huidigBoek.title;
    const auteur = document.createElement("p");
    auteur.className = "detail-author";
    auteur.textContent = huidigBoek.authors || "Auteur onbekend";
    const omschrijving = document.createElement("p");
    omschrijving.className = "detail-description";
    omschrijving.textContent = huidigBoek.description || "Voor dit boek is nog geen omschrijving toegevoegd.";
    const isbn = document.createElement("p");
    isbn.className = "muted";
    isbn.textContent = "ISBN: " + huidigBoek.isbn;
    const prijs = document.createElement("p");
    prijs.className = "detail-price";
    prijs.textContent = "€ " + Number(huidigBoek.price).toFixed(2);

    const formulier = document.createElement("form");
    formulier.className = "reserve-form";
    const aantalLabel = document.createElement("label");
    aantalLabel.textContent = "Aantal";
    const aantal = document.createElement("input");
    aantal.type = "number";
    aantal.name = "quantity";
    aantal.min = "1";
    aantal.max = String(huidigBoek.stock);
    aantal.value = "1";
    aantal.required = true;
    aantalLabel.appendChild(aantal);
    const reserveer = document.createElement("button");
    reserveer.className = "button button-dark";
    reserveer.type = "submit";
    reserveer.textContent = "Reserveer dit boek";
    reserveer.disabled = huidigBoek.stock < 1;
    formulier.append(aantalLabel, reserveer);
    formulier.addEventListener("submit", function (event) {
        event.preventDefault();
        reserveerBoek(huidigBoek, Number(aantal.value));
    });

    tekst.append(label, titel, auteur, omschrijving, isbn, prijs);
    if (!admin) tekst.appendChild(formulier);
    layout.append(omslag, tekst);
    details.appendChild(layout);

    const reviews = huidigBoek.reviews || [];
    const reviewLijst = document.getElementById("review-list");
    reviewLijst.replaceChildren();
    document.getElementById("empty-reviews").hidden = reviews.length !== 0;
    reviews.forEach(function (review) {
        const kaart = document.createElement("article");
        kaart.className = "review-card";
        const score = document.createElement("p");
        score.className = "review-stars";
        score.textContent = "★".repeat(review.score) + "☆".repeat(5 - review.score) + "  " + review.score + "/5";
        const beschrijving = document.createElement("p");
        beschrijving.textContent = review.description;
        kaart.append(score, beschrijving);
        reviewLijst.appendChild(kaart);
    });
}

async function reserveerBoek(boek, aantal = 1) {
    if (admin) {
        toonMelding("Log uit als admin om als klant een boek te reserveren.", true);
        return;
    }
    if (!gebruiker) {
        toonMelding("Log eerst in om een boek te reserveren.", true);
        toonPagina("account");
        return;
    }
    if (aantal < 1 || aantal > boek.stock) {
        toonMelding("Kies een aantal dat op voorraad is.", true);
        return;
    }

    const bevestiging = await vraagBevestiging(boek, aantal);
    if (!bevestiging) return;

    const gegevens = new URLSearchParams();
    gegevens.append("userId", gebruiker.id);
    gegevens.append("isbn", boek.isbn);
    gegevens.append("quantity", aantal);
    try {
        const antwoord = await fetch(apiAdres + "/reservations", { method: "POST", body: gegevens });
        if (!antwoord.ok) throw new Error();
        const reservering = await antwoord.json();
        mijnReserveringen.push({ id: reservering.id, isbn: boek.isbn, title: boek.title, quantity: aantal, confirmed: false, ready: false, rejected: false });
        localStorage.setItem("mijnReserveringen", JSON.stringify(mijnReserveringen));
        toonMelding("Je reservering is geplaatst in Mijn reserveringen.", false, "Mijn reserveringen", async function () {
            await openProfiel();
            document.getElementById("reservation-list").scrollIntoView({ behavior: "smooth", block: "start" });
        });
        await laadBoeken();
        if (huidigBoek && huidigBoek.isbn === boek.isbn) await laadDetails(boek.isbn);
    } catch (fout) {
        toonMelding("Reserveren lukte niet. Controleer de voorraad en probeer opnieuw.", true);
    }
}

function vraagBevestiging(boek, aantal) {
    const venster = document.getElementById("reservation-confirmation");
    const boodschap = document.getElementById("confirmation-message");
    const jaKnop = document.getElementById("confirm-reservation");
    const neeKnop = document.getElementById("cancel-reservation");
    boodschap.textContent = "Wil je " + aantal + (aantal === 1 ? " exemplaar van \"" : " exemplaren van \"") + boek.title + "\" reserveren?";
    venster.hidden = false;
    venster.style.display = "grid";

    return new Promise(function (resolve) {
        jaKnop.onclick = function () {
            venster.hidden = true;
            venster.style.display = "none";
            resolve(true);
        };
        neeKnop.onclick = function () {
            venster.hidden = true;
            venster.style.display = "none";
            resolve(false);
        };
    });
}

async function openProfiel(naInloggen = false) {
    if (!gebruiker) return;
    // Toon meteen het profiel, ook wanneer het laden van profielgegevens even duurt.
    toonPagina("profile");
    try {
        const antwoord = await fetch(apiAdres + "/users/" + gebruiker.id);
        if (!antwoord.ok) throw new Error();
        const profiel = await antwoord.json();
        toonMeldingen(profiel.reservations || []);
        document.getElementById("profile-name").textContent = "Dag, " + profiel.name;
        document.getElementById("profile-email").textContent = profiel.email;

        const gekocht = await Promise.all((profiel.purchasedBookIsbns || []).map(async function (isbn) {
            const boekInLijst = alleBoeken.find(function (boek) { return boek.isbn === isbn; });
            if (boekInLijst) return boekInLijst;
            try {
                const boekAntwoord = await fetch(catalogusApiAdres + "/books/" + encodeURIComponent(isbn));
                if (boekAntwoord.ok) return await boekAntwoord.json();
            } catch (fout) {
                // Toon het ISBN als de catalogus tijdelijk niet bereikbaar is.
            }
            return { isbn: isbn, title: "ISBN " + isbn };
        }));
        const gekochtLijst = document.getElementById("purchased-list");
        gekochtLijst.replaceChildren();
        document.getElementById("empty-purchases").hidden = gekocht.length !== 0;
        gekocht.forEach(function (boek) {
            const chip = document.createElement("button");
            chip.type = "button";
            chip.className = "purchased-chip";
            chip.textContent = boek.title;
            chip.addEventListener("click", function () { laadDetails(boek.isbn); });
            gekochtLijst.appendChild(chip);
        });

        const reserveringenUitProfiel = profiel.reservations || [];
        const reserveringenLijst = document.getElementById("reservation-list");
        reserveringenLijst.replaceChildren();
        document.getElementById("empty-reservations").hidden = reserveringenUitProfiel.length !== 0;
        reserveringenUitProfiel.forEach(function (reservering) {
            const bewaardeInfo = mijnReserveringen.find(function (item) { return item.id === reservering.id; });
            const rij = document.createElement("div");
            rij.className = "reservation-row";
            const tekst = document.createElement("div");
            const titel = document.createElement("strong");
            titel.textContent = reservering.bookTitle || (bewaardeInfo ? bewaardeInfo.title : "Reservering #" + reservering.id);
            const status = document.createElement("p");
            if (reservering.rejected) {
                status.textContent = "Afgewezen door de winkel. De voorraad is teruggezet.";
            } else if (reservering.confirmed) {
                status.textContent = "Afgehaald en betaald.";
            } else if (reservering.ready) {
                status.textContent = "Goedgekeurd · je mag " + (isMorgen(reservering.pickupDate) ? "morgen, " : "op ") + maakDatumTekst(reservering.pickupDate) + " afhalen en betalen.";
            } else {
                status.textContent = "Je reservering wacht op beoordeling door de winkel.";
            }
            tekst.append(titel, status);
            rij.appendChild(tekst);
            reserveringenLijst.appendChild(rij);
        });

        const klaarstaandeBoeken = reserveringenUitProfiel.filter(function (reservering) {
            return reservering.ready && !reservering.confirmed && !reservering.rejected;
        });
        if (naInloggen && klaarstaandeBoeken.length > 0) {
            const eersteMelding = klaarstaandeBoeken[0];
            const datumTekst = maakDatumTekst(eersteMelding.pickupDate);
            toonMelding("Je mag je boek " + (isMorgen(eersteMelding.pickupDate) ? "morgen, " : "op ") + datumTekst + " komen afhalen en betalen.", false, "Bekijk bericht", async function () {
                await openBerichten();
            });
        }

        const reviewForm = document.getElementById("review-form");
        const boekKeuze = document.getElementById("review-book");
        boekKeuze.replaceChildren();
        gekocht.forEach(function (boek) {
            const optie = document.createElement("option");
            optie.value = boek.isbn;
            optie.textContent = boek.title;
            boekKeuze.appendChild(optie);
        });
        reviewForm.hidden = gekocht.length === 0;
        toonPagina("profile");
    } catch (fout) {
        toonMelding("Je profiel kon niet geladen worden. Log opnieuw in.", true);
    }
}

async function openAdminDashboard() {
    if (!admin) {
        toonPagina("admin-login");
        return;
    }

    toonPagina("admin-dashboard");
    try {
        const antwoord = await fetch(apiAdres + "/reservations", {
            headers: {
                "X-Admin-Email": admin.email,
                "X-Admin-Password": admin.password
            }
        });
        if (!antwoord.ok) throw new Error();

        const reserveringen = await antwoord.json();
        const lijst = document.getElementById("admin-reservation-list");
        lijst.replaceChildren();
        document.getElementById("empty-admin-reservations").hidden = reserveringen.length !== 0;
        const heeftOpenReserveringen = reserveringen.some(function (reservering) {
            return !reservering.confirmed && !reservering.ready && !reservering.rejected;
        });
        document.getElementById("no-pending-admin-reservations").hidden = heeftOpenReserveringen;

        reserveringen.forEach(function (reservering) {
            const rij = document.createElement("div");
            rij.className = "reservation-row";
            const gegevens = document.createElement("div");
            const titel = document.createElement("strong");
            titel.textContent = (reservering.bookTitle || ("Boek ISBN " + reservering.bookIsbn)) + " · reservering #" + reservering.id;
            const klant = document.createElement("p");
            klant.textContent = reservering.quantity + " exemplaar/exemplaren voor " + reservering.userName + " (" + reservering.userEmail + ")";
            const status = document.createElement("p");
            if (reservering.rejected) {
                status.textContent = "Afgewezen";
            } else if (reservering.confirmed) {
                status.textContent = "Afgehaald en betaald";
            } else if (reservering.ready) {
                status.textContent = "Goedgekeurd · afhalen op " + maakDatumTekst(reservering.pickupDate);
            } else {
                status.textContent = "Wacht op beoordeling";
            }
            gegevens.append(titel, klant, status);
            rij.appendChild(gegevens);

            if (!reservering.ready && !reservering.rejected && !reservering.confirmed) {
                const acties = document.createElement("div");
                acties.className = "admin-reservation-actions";
                acties.append(
                    maakKnop("Goedkeuren", async function () {
                        await wijzigAdminReservering(reservering.id, "approve");
                    }, "button button-dark"),
                    maakKnop("Afwijzen", async function () {
                        await wijzigAdminReservering(reservering.id, "reject");
                    }, "button button-coral")
                );
                rij.appendChild(acties);
            }
            lijst.appendChild(rij);
        });
    } catch (fout) {
        toonMelding("De reserveringen konden niet geladen worden. Log opnieuw in als admin.", true);
    }
}

async function wijzigAdminReservering(id, actie) {
    try {
        const antwoord = await fetch(apiAdres + "/reservations/" + id + "/" + actie, {
            method: "PUT",
            headers: {
                "X-Admin-Email": admin.email,
                "X-Admin-Password": admin.password
            }
        });
        if (!antwoord.ok) throw new Error();

        if (actie === "approve") {
            toonMelding("Reservering goedgekeurd. De klant ziet dit na het inloggen.");
        } else {
            toonMelding("Reservering afgewezen. De voorraad is teruggezet.");
        }
        await laadBoeken();
        await openAdminDashboard();
    } catch (fout) {
        toonMelding("De reservering kon niet bijgewerkt worden.", true);
    }
}

document.getElementById("search").addEventListener("input", function (event) {
    const zoekterm = event.target.value.toLowerCase();
    const resultaat = alleBoeken.filter(function (boek) {
        return boek.title.toLowerCase().includes(zoekterm) || (boek.authors || "").toLowerCase().includes(zoekterm);
    });
    toonBoeken(resultaat);
});

document.getElementById("login-form").addEventListener("submit", async function (event) {
    event.preventDefault();
    const gegevens = new URLSearchParams(new FormData(event.target));
    try {
        const antwoord = await fetch(apiAdres + "/users/login", { method: "POST", body: gegevens });
        if (!antwoord.ok) throw new Error();
        gebruiker = await antwoord.json();
        admin = null;
        sessionStorage.removeItem("admin");
        localStorage.setItem("gebruiker", JSON.stringify(gebruiker));
        toonNavigatie();
        toonMelding("Welkom terug, " + gebruiker.name + "!");
        event.target.reset();
        await laadBoeken();
        toonPagina("catalogus");
        await openProfiel(true);
    } catch (fout) {
        toonMelding("Inloggen lukte niet. Controleer je e-mailadres en wachtwoord.", true);
    }
});

document.getElementById("admin-login-form").addEventListener("submit", async function (event) {
    event.preventDefault();
    const gegevens = new URLSearchParams(new FormData(event.target));
    try {
        const antwoord = await fetch(apiAdres + "/users/admin/login", { method: "POST", body: gegevens });
        if (!antwoord.ok) throw new Error();
        admin = { email: gegevens.get("email"), password: gegevens.get("password") };
        sessionStorage.setItem("admin", JSON.stringify(admin));
        gebruiker = null;
        localStorage.removeItem("gebruiker");
        toonNavigatie();
        event.target.reset();
        toonMelding("Admin is ingelogd.");
        await laadBoeken();
        await openAdminDashboard();
    } catch (fout) {
        toonMelding("Adminlogin mislukt. Controleer de gegevens.", true);
    }
});

document.getElementById("register-form").addEventListener("submit", async function (event) {
    event.preventDefault();
    const gegevens = new URLSearchParams(new FormData(event.target));
    try {
        const antwoord = await fetch(apiAdres + "/users/register", { method: "POST", body: gegevens });
        if (!antwoord.ok) throw new Error();
        toonMelding("Je profiel is aangemaakt. Je kan nu inloggen.");
        document.getElementById("login-form").elements.email.value = gegevens.get("email");
        event.target.reset();
    } catch (fout) {
        toonMelding("Registreren lukte niet. Misschien bestaat dit e-mailadres al.", true);
    }
});

document.getElementById("review-form").addEventListener("submit", async function (event) {
    event.preventDefault();
    const formulier = new FormData(event.target);
    const isbn = formulier.get("isbn");
    const gegevens = new URLSearchParams();
    gegevens.append("userId", gebruiker.id);
    gegevens.append("isbn", isbn);
    gegevens.append("score", formulier.get("score"));
    gegevens.append("description", formulier.get("description"));
    try {
        const antwoord = await fetch(apiAdres + "/reviews", { method: "POST", body: gegevens });
        if (!antwoord.ok) throw new Error();
        event.target.reset();
        await laadDetails(isbn);
        document.querySelector(".reviews-section").scrollIntoView({ behavior: "smooth", block: "start" });
        toonMelding("Bedankt voor je review! Je review staat nu bij dit boek.");
    } catch (fout) {
        toonMelding("Review plaatsen lukte niet.", true);
    }
});

document.getElementById("shop-button").addEventListener("click", function () { toonPagina("catalogus"); });
document.getElementById("home-link").addEventListener("click", function () { toonPagina("catalogus"); });
document.getElementById("back-button").addEventListener("click", function () { toonPagina("catalogus"); });
document.getElementById("login-button").addEventListener("click", function () { toonPagina("account"); });
document.getElementById("profile-button").addEventListener("click", openProfiel);
document.getElementById("notification-button").addEventListener("click", openBerichten);
document.getElementById("admin-button").addEventListener("click", function () {
    if (admin) openAdminDashboard();
    else toonPagina("admin-login");
});
document.getElementById("admin-dashboard-button").addEventListener("click", openAdminDashboard);
document.getElementById("logout-button").addEventListener("click", async function () {
    gebruiker = null;
    admin = null;
    sessionStorage.removeItem("admin");
    localStorage.removeItem("gebruiker");
    toonNavigatie();
    toonPagina("catalogus");
    await laadBoeken();
    toonMelding("Je bent uitgelogd.");
});

toonNavigatie();
laadBoeken();
laadMeldingsteller();
