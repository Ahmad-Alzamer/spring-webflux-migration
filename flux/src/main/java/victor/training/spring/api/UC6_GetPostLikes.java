package victor.training.spring.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import victor.training.spring.sql.Post;
import victor.training.spring.sql.PostRepo;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UC6_GetPostLikes {
    private final PostRepo postRepo;
    private final Map<Long, Integer> postLikes = new ConcurrentHashMap<>();
    private final Map<Long, Mono<String>> postTitles = new ConcurrentHashMap<>();
    Sinks.Many<LikeEvent> eventSink = Sinks.many().multicast().directBestEffort();
    Sinks.Many<LikedPosts> likedPostsSink = Sinks.many().multicast().directBestEffort();


  @GetMapping("posts/{postId}/likes")
  public Mono<Integer> getPostLikes(@PathVariable long postId) {

      return Mono.just(postLikes.getOrDefault(postId, 0));
  }
  public record LikeEvent(Long postId, int likes) {}

  @Bean
  public Function<Flux<LikeEvent>,Flux<LikedPosts>> onLikeEvent() {
    return likeEventFlux -> likeEventFlux
            .doOnNext(likeEvent ->  postLikes.put(likeEvent.postId(), likeEvent.likes()))
            .doOnNext(likeEvent ->  eventSink.tryEmitNext(likeEvent))

            .map(LikeEvent::postId)
            .buffer(Duration.ofSeconds(1))
            .map(HashSet::new)
            .doOnNext(postIds -> log.warn("will hit DB to get titles for IDs: {}",postIds))
            .flatMap(postIds -> postRepo.findAllById(postIds).map(Post::title).collectList())
            .map(LikedPosts::new)
            .doOnNext(likedPosts -> likedPostsSink.tryEmitNext(likedPosts))
            .onErrorContinue((error, element) -> log.error("failed for element: [{}], error: [{}]",element,error.getMessage()))
            ;
  }

  // TODO push live likes to browser for UX❤️ http://localhost:8081/posts/2/likes-live
  @CrossOrigin
   @GetMapping(value = "posts/{postId}/likes-live", produces = "text/event-stream")
   public Flux<Integer> getPostLikesLive(@PathVariable long postId) {
    log.info("received: posts/{}/likes-live",postId);
     return eventSink.asFlux()
             .filter(likeEvent -> likeEvent.postId == postId)
             .map(LikeEvent::likes);
   }

    @CrossOrigin
    @GetMapping(value = "posts/recently-liked", produces = "text/event-stream")
    public Flux<LikedPosts> getRecentlyLikedPosts() {
        return likedPostsSink.asFlux();
    }

  // TODO every 1 second emit titles of recently liked posts. Hard: keep listening despite failed messages
  public record LikedPosts(Collection<String> titles) {
  }
}
