# local run of Docker
```shell
docker compose up
```



# Devox talk
[From Web to Flux: Tackling the Challenges of Reactive Programming By Victor Rentea](https://www.youtube.com/watch?v=wsgJU5S1rRY&ab_channel=Devoxx)





# JS / Browser / fetch / event-stream
reference: [Event Streaming Made Easy with Event-Stream and JavaScript Fetch](https://medium.com/@bs903944/event-streaming-made-easy-with-event-stream-and-javascript-fetch-8d07754a4bed)
```javascript
fetch('http://localhost:8081/posts/3/likes-live').then(response => {
        // Get the readable stream from the response body
        const stream = response.body;
        // Get the reader from the stream
        const reader = stream.getReader();
        // Define a function to read each chunk
        const readChunk = () => {
            // Read a chunk from the reader
            reader.read()
                .then(({
                    value,
                    done
                }) => {
                    // Check if the stream is done
                    if (done) {
                        // Log a message
                        console.log('Stream finished');
                        // Return from the function
                        return;
                    }
                    // Convert the chunk value to a string
                    const chunkString = new TextDecoder().decode(value);
                    // Log the chunk string
                    console.log(chunkString);
                    // Read the next chunk
                    readChunk();
                })
                .catch(error => {
                    // Log the error
                    console.error(error);
                });
        };
        // Start reading the first chunk
        readChunk();
    })
    .catch(error => {
        // Log the error
        console.error(error);
    });
```



# TODOs
1. check why ``@ResponseStatus`` on the Exception is not reflected in the response