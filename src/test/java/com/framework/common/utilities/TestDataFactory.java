package com.framework.common.utilities;

import com.framework.backend.model.Article;
import com.framework.backend.model.User;
import com.framework.configuration.UserCredentials;
import net.datafaker.Faker;

import java.util.UUID;

import static com.framework.common.utilities.ConfigurationReader.getConfigurations;

/**
 * Builds unique test data for Conduit users and articles, with Datafaker filling in passwords and article content.
 * <p>
 * {@link #RUN_TAG} is the class-load timestamp in upper-case base 36, so it is set once per JVM. Usernames and
 * article titles embed it as {@code test_user-<runTag>-<uuid8>} and {@code test_article-<runTag>-<uuid3>}, both
 * lower-cased, and a random UUID fragment keeps records within a run distinct. Conduit has no delete endpoint for
 * users, so nothing is cleaned up automatically. The tag only makes leftover records traceable to the run that
 * created them.
 */
public final class TestDataFactory {

    public static final String RUN_TAG = Long.toString(System.currentTimeMillis(), 36).toUpperCase();

    private static final Faker FAKER = new Faker();

    private TestDataFactory() {
    }

    public static User randomConduitUser() {
        String username = ("TEST_USER-" +RUN_TAG + "-" + UUID.randomUUID().toString().substring(0, 8)).toLowerCase();
        return User.builder()
                .username(username)
                .email(username + "@example.test")
                .password(FAKER.internet().password(12, 20, true, true, true))
                .build();
    }

    public static String uniqueArticleTitle(){
        return ("TEST_ARTICLE-" +RUN_TAG + "-" + UUID.randomUUID().toString().substring(0, 8)).toLowerCase();
    }

    public static Article createArticle() {
        return Article.builder()
                .title(uniqueArticleTitle())
                .body(FAKER.lorem().paragraph(2))
                .description(FAKER.lorem().sentence())
                .tagList(FAKER.lorem().words(3)).build();
    }

    public static User configuredUser(String userKey){
        UserCredentials userCredentials = getConfigurations().getUser(userKey);
        return User.builder()
                .email(userCredentials.getEmail())
                .password(userCredentials.getPassword())
                .build();
    }
}
