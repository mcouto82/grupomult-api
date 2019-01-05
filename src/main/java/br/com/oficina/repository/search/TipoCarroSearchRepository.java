package br.com.oficina.repository.search;

import br.com.oficina.domain.TipoCarro;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TipoCarro entity.
 */
public interface TipoCarroSearchRepository extends ElasticsearchRepository<TipoCarro, Long> {
}
