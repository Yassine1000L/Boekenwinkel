package be.ehb.filali.catalogus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Start de service die boeken en voorraad beheert. */
@SpringBootApplication
public class CatalogusApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogusApplication.class, args);
    }
}
