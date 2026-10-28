package be.ehb.filali.examen_aa_yassine.services;

import be.ehb.filali.examen_aa_yassine.model.BookInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/** Stuurt REST-aanvragen van de reserveringsservice naar de catalogusservice. */
@Service
public class CatalogusClient {
    private final RestTemplate restTemplate;
    private final String catalogusAdres;

    public CatalogusClient(RestTemplate restTemplate,
                           @Value("${catalogus.service.url}") String catalogusAdres) {
        this.restTemplate = restTemplate;
        this.catalogusAdres = catalogusAdres;
    }

    public Optional<BookInfo> getBook(String isbn) {
        try {
            BookInfo book = restTemplate.getForObject(catalogusAdres + "/books/" + isbn, BookInfo.class);
            return Optional.ofNullable(book);
        } catch (HttpClientErrorException.NotFound fout) {
            return Optional.empty();
        }
    }

    public Optional<BookInfo> reserveStock(String isbn, int quantity) {
        try {
            BookInfo book = restTemplate.exchange(
                    catalogusAdres + "/books/" + isbn + "/reserve?quantity=" + quantity,
                    org.springframework.http.HttpMethod.PUT,
                    null,
                    BookInfo.class
            ).getBody();
            return Optional.ofNullable(book);
        } catch (HttpClientErrorException fout) {
            return Optional.empty();
        }
    }

    public boolean releaseStock(String isbn, int quantity) {
        try {
            restTemplate.put(catalogusAdres + "/books/" + isbn + "/release?quantity=" + quantity, null);
            return true;
        } catch (RestClientException fout) {
            return false;
        }
    }
}
