package school.faang.user_service.repository.elastic_search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.elastic_search.UserDocument;

@Repository
public interface UserDocumentRepository extends ElasticsearchRepository<UserDocument, Long> {


    @Query("""
             {
             "bool": {
                    "should": [
                        {
                        "multi_match": {
                        "query": "?0",
                        "fields": ["aboutMe^4", "country^3"],
                        "type": "best_fields",
                        "operator": "or"
                        }
                    },
                    {
                        "match_phrase": {
                            "aboutMe": {
                                "query": "?0",
                                "boost": 3
                            }
                        }
                    },
                    {
                        "match_phrase": {
                            "country": {
                                "query": "?0",
                                "boost": 2
                            }
                        }
                    }
                    ]
                }
             }
            """)
    Page<UserDocument> searchByQuery(String query, Pageable pageable);

}
