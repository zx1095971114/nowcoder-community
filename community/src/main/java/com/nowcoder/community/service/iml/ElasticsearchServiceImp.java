package com.nowcoder.community.service.iml;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.joint.ElasticsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/11  20:11
 * @description :
 **/
@Service
public class ElasticsearchServiceImp implements ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void saveDiscussPost(DiscussPost... discussPost) {
        discussPostRepository.saveAll(Arrays.stream(discussPost).toList());
    }

    @Override
    public void deleteDiscussPost(int discussPostId) {
        discussPostRepository.deleteById(discussPostId);
    }

    @Override
    public List<DiscussPost> searchDiscussPost(String keyword, int currentPage, int limit) {
        //这里是高亮条件设置，因为版本迁移，新版的spring-data-elasticsearch-starter还没有实现HighlightQuery.Builder方法
        //所以使用这个原始的笨办法
        List<HighlightField> list = new ArrayList<>();
        HighlightField highlightFieldTitle = new HighlightField("title", HighlightFieldParameters.builder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .build()
        );
        HighlightField highlightFieldContent = new HighlightField("content", HighlightFieldParameters.builder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .build()
        );
        list.add(highlightFieldTitle);
        list.add(highlightFieldContent);

        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        NativeQuery nativeQuery = nativeQueryBuilder
                .withQuery(QueryBuilders
                        .multiMatch(op -> op.query(keyword)
                                .fields("title", "content")
                        )
                )
                .withSort(sort -> sort.field(field -> field.field("type").order(SortOrder.Desc)))
                .withSort(sort -> sort.field(field -> field.field("score").order(SortOrder.Desc)))
                .withSort(sort -> sort.field(field -> field.field("createTime").order(SortOrder.Desc)))
                //这里注意，elasticsearch的pageNumber是从0开始的
                .withPageable(PageRequest.of(currentPage - 1, limit))
                .withHighlightQuery(new HighlightQuery(new Highlight(list), DiscussPost.class))
                .build();

        SearchHits<DiscussPost> posts = elasticsearchTemplate.search(nativeQuery, DiscussPost.class);
        List<DiscussPost> result = new ArrayList<>();
        posts.get().iterator().forEachRemaining(new Consumer<SearchHit<DiscussPost>>() {
            @Override
            public void accept(SearchHit<DiscussPost> discussPostSearchHit) {
                //这里是原始的post，并没有加入highlight的内容，要手动加入
                DiscussPost post = discussPostSearchHit.getContent();

                List<String> highlightTitle = discussPostSearchHit.getHighlightField("title");
                List<String> highlightContent = discussPostSearchHit.getHighlightField("content");

                if(!highlightTitle.isEmpty()){
                    post.setTitle(highlightTitle.get(0));
                }
                if(!highlightContent.isEmpty()){
                    post.setContent(highlightContent.get(0));
                }
                result.add(post);
            }
        });

        return result;
    }
}
