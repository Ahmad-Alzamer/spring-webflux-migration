package victor.training.spring.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.spring.sql.Comment;
import victor.training.spring.sql.CommentRepo;
import victor.training.spring.sql.Post;
import victor.training.spring.sql.PostRepo;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UC5_CreateComment {
  private final PostRepo postRepo;
  private final CommentRepo commentRepo;
  private final RestTemplate restTemplate;

  public record CreateCommentRequest(String comment, String name) {
  }

  @PostMapping("posts/{postId}/comments")
  public void createComment(@PathVariable Long postId, @RequestBody CreateCommentRequest request) {
    Post post = postRepo.findById(postId).orElseThrow();
    boolean offensive = checkOffensive(post.body(), request.comment());
    boolean unlocked = checkAuthorAllowsComments(post.authorId());
    if (offensive && unlocked) {
      commentRepo.save(new Comment(post.id(), request.comment(), request.name()));
    } else {
      throw new IllegalArgumentException("Comment Rejected");
    }
  }

  private boolean checkAuthorAllowsComments(Long authorId) {
    String url = "http://localhost:9999/author/" + authorId + "/comments";
    String result = restTemplate.getForObject(url, String.class);
    return Boolean.parseBoolean(result);
  }

  private boolean checkOffensive(String body, String comment) {
    record Request(String body, String comment) {
    }
    String url = "http://localhost:9999/safety-check";
    String result = restTemplate.postForObject(url, new Request(body, comment), String.class);
    return "OK".equals(result);
  }
}
