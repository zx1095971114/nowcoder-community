package com.nowcoder.community;

import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.search.HighlightBase;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.client.erhlc.HighlightQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/10  23:02
 * @description :
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ATest {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testDelete(){
//        discussPostRepository.deleteById(231);
        discussPostRepository.deleteAll();
    }


    @Test
    public void testSave(){
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(-1, 0, 10000, 0));
//        discussPostRepository.save(null);
//        discussPostRepository.saveAll(new ArrayList<>());
        System.out.println(elasticsearchTemplate.getClusterVersion());
    }
    @Test
    public void test(){

        List<HighlightField> list = new ArrayList<>();
        HighlightField highlightFieldTitle = new HighlightField("title", HighlightFieldParameters.builder()
//                .withMatchedFields("title")
                .withPreTags("<em>")
                .withPostTags("</em>")
                .build()
        );
        HighlightField highlightFieldContent = new HighlightField("content", HighlightFieldParameters.builder()
//                .withMatchedFields("content")
                .withPreTags("<em>")
                .withPostTags("</em>")
                .build()
        );
        list.add(highlightFieldTitle);
        list.add(highlightFieldContent);

        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        NativeQuery nativeQuery = nativeQueryBuilder
                .withQuery(QueryBuilders
                            .multiMatch(op -> op.query("互联网寒冬")
                                                .fields("title", "content")
                            )
                        )
                .withSort(sort -> sort.field(field -> field.field("type").order(SortOrder.Desc)))
                .withSort(sort -> sort.field(field -> field.field("score").order(SortOrder.Desc)))
                .withSort(sort -> sort.field(field -> field.field("createTime").order(SortOrder.Desc)))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightQuery(new HighlightQuery(new Highlight(list), DiscussPost.class))
                .build();

//                .build();

        SearchHits<DiscussPost> posts = elasticsearchTemplate.search(nativeQuery, DiscussPost.class);
        System.out.println(posts.getTotalHits());
//        Iterator<SearchHit<DiscussPost>> iterators = posts.get().iterator();
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
                System.out.println(post);
            }
        });
    }
}
