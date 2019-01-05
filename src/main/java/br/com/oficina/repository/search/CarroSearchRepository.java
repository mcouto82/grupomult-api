package br.com.oficina.repository.search;

import br.com.oficina.domain.Carro;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Carro entity.
 */
public interface CarroSearchRepository extends ElasticsearchRepository<Carro, Long> {
}
