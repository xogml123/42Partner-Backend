package partner42.modulecommon.repository.opinion;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.opinion.Opinion;
import partner42.modulecommon.repository.article.ArticleRepository;

@SpringBootTest
//custom db를 사용하기 위해 필요, 없으면 embeded h2db사용하려고 함.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OpinionRepositoryTest {

    @Autowired
    private OpinionRepository opinionRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired


    @BeforeEach
    void setUp() {
        //given

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByApiId() {

    }

    @Test
    void findAllByArticleApiIdAndIsDeletedIsFalse() {
    }
}