package partner42.modulecommon.repository.article;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArticleRepositoryCustomImplTest {

    @Autowired
    ArticleRepository articleRepositoryCustom;
    @Test
    void findSliceByCondition() {
        articleRepositoryCustom.toString();
    }
}