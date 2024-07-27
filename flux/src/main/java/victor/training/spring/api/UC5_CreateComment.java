package victor.training.spring.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import victor.training.spring.sql.Comment;
import victor.training.spring.sql.CommentRepo;
import victor.training.spring.sql.Post;
import victor.training.spring.sql.PostRepo;

import java.util.function.Function;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UC5_CreateComment {
  private final PostRepo postRepo;
  private final CommentRepo commentRepo;
  private final WebClient webClient;

  public record CreateCommentRequest(String comment, String name) {
  }

  @PostMapping("posts/{postId}/comments")
  public Mono<Void> createComment(@PathVariable long postId, @RequestBody CreateCommentRequest request) {
    return postRepo.findById(postId)
            .filterWhen(post -> isSafe(post.body(), request.comment()))
            .filterWhen(post-> isUnlocked(post.authorId()))
            .flatMap(post -> commentRepo.save(new Comment(post.id(), request.comment(), request.name())))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Comment Rejected")))
            .then();
  }

  private Mono<Boolean> isUnlocked(long authorId) {
    String url = "http://localhost:9999/author/" + authorId + "/comments";
    var resultMono = webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class);
    return resultMono.map(Boolean::parseBoolean);
  }

  private Mono<Boolean> isSafe(String postBody, String comment) {
    record Request(String body, String comment) {
    }
    String url = "http://localhost:9999/safety-check";
    var resultMono = webClient
            .post()
            .uri(url)
//            .body(BodyInserters.fromValue())
            .bodyValue(new Request(postBody, comment)) //short hand for the above
            .retrieve()
            .bodyToMono(String.class);
    return resultMono.map("OK"::equals);
  }
}
