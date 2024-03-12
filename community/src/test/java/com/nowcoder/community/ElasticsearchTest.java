package com.nowcoder.community;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.RescorerQuery;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQuery;
import org.springframework.data.elasticsearch.core.query.SearchTemplateQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/8  23:13
 * @description :
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private ElasticsearchClient elasticsearchClient;


    @Test
    public void testAdd() throws IOException {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(230);

        IndexResponse response = elasticsearchClient.index(i -> i
                .index("discuss_post")
                .id(String.valueOf(discussPost.getId()))
                .document(discussPost)
        );
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(230));
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(231));
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(232));
    }

    @Test
    public void testAddMore() {
        List<DiscussPost> discussPostList = new ArrayList<>();
        discussPostList.addAll(discussPostMapper.selectDiscussPost(101, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(102, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(103, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(111, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(112, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(131, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(132, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(133, 0, 100, 0));
        discussPostList.addAll(discussPostMapper.selectDiscussPost(134, 0, 100, 0));

        BulkRequest.Builder builder = new BulkRequest.Builder();
        for (DiscussPost discussPost : discussPostList) {
            builder.operations(b -> b.
                    index(op -> op
                        .index("discuss_post")
                        .id(String.valueOf(discussPost.getId()))
                        .document(discussPost)
                )
            );
        }

        BulkResponse response = null;
        try {
            response = elasticsearchClient.bulk(builder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(response.errors()){
            System.out.println("请求出错");
            for (BulkResponseItem item : response.items()) {
                if(item.error() != null){
                    System.out.println(item.error().reason());
                }
            }
        }
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(101, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(102, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(103, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(111, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(112, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(131, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(132, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(133, 0, 100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPost(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost discussPost3 = new DiscussPost(1, 123, "招聘", "互联网招聘", 0, 0, new Date(), 10, 4);
        DiscussPost discussPost2 = new DiscussPost(2, 123, "互联网寒冬", "网住幸福", 0, 0, new Date(), 10, 5);
        DiscussPost discussPost1 = new DiscussPost(3, 123, "冬天吃什么", "寒冬吃饺子", 1, 0, new Date(), 10, 3.2);

        try {
            elasticsearchClient.index(op -> op
                    .index("discuss_post")
                    .id(String.valueOf(discussPost1.getId()))
                    .document(discussPost1)
            );

            elasticsearchClient.index(op -> op
                    .index("discuss_post")
                    .id(String.valueOf(discussPost2.getId()))
                    .document(discussPost2)
            );

            elasticsearchClient.index(op -> op
                    .index("discuss_post")
                    .id(String.valueOf(discussPost3.getId()))
                    .document(discussPost3)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        discussPostRepository.save(discussPost);
    }

    @Test
    public void testDelete() throws IOException {
        elasticsearchClient.indices()
                .delete(builder -> builder.index("discuss_post")
                );
//        discussPostRepository.deleteById(231);
//        discussPostRepository.deleteAll();
    }

    @Test
    public void testSearch() throws IOException {
        Query matchTitleQuery = MatchQuery.of(m -> m
                .field("title")
                .query("互联网寒冬")
//                .analyzer("ik_smart")
        )._toQuery();

        MatchQuery matchContentQuery = MatchQuery.of(m -> m
                .field("content")
                .query("互联网寒冬")
        );

        SearchResponse<DiscussPost> response = elasticsearchClient.search(
                op -> op.index("discuss_post")
                        .query(q -> q
                                .match(matchContentQuery)
                        )
                        .sort(sort -> sort
                                .field(builder -> builder
                                        .field("type")
                                        .order(SortOrder.Desc)
                                        .field("score")
                                        .order(SortOrder.Desc)
                                        .field("createTime")
                                        .order(SortOrder.Desc)
                                )
                        )
                        .from(0)
                        .size(10)
                        .highlight(highlight -> highlight
                                .fields("title", form -> form
                                        .preTags("<em>")
                                        .postTags("</em>")
                                )
                                .fields("content", form -> form
                                        .preTags("<em>")
                                        .postTags("</em>")
                                )
                        )
                , DiscussPost.class);

        List<DiscussPost> discussPostList = new ArrayList<>();
        for (Hit<DiscussPost> hit : response.hits().hits()) {
            System.out.println(hit.source());
        }
    }

}
