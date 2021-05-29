package moviereservationreport;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "movieMngs", path = "movieMngs")
public interface MovieMngRepository extends PagingAndSortingRepository<MovieMng, Long> {

    Optional<MovieMng> findByName(String name);
}
