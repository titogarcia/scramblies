# Scramblies Challenge - `scramble?` and web service

## `scramble?` function
The function can be found in `scramblies.scramble/scramble?`.

Unit tests can be found in namespace `scramblies.scramble-test`. Within those tests, there are property-based tests, based on an alternative implementation `scramble-alt?`, which is more declarative but less performant.

## Web service

I've included the web service in this repo as well.

### Running the server

No main function. It can be run manually from a REPL with...

    (def stop (scramblies.server/start-server 8080))
    
...and stopped with...

    (stop)

You can then access the scramble service at http://localhost:8080/scramble?str1=rekqodlw&str2=world

### Comments/assumptions

I'm using `bidi` for the routes as I like it's data-oriented, macro-less approach.

Regarding Flexiana's web page, you guys like using `liberator`, so I've put it in practice here. I hadn't used it before.

I have used JSON as the format for the response. We could accept EDN as well. That would imply some tweaking that I haven't found valuable for the exercise.

I have enabled CORS headers for the service to be accessible by browser requests from any origin.

There is a small automatic test of the web service to check that the whole infrastructure is correctly running. This is useful to check that nothing has broken in regression tests.