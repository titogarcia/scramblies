(ns scramblies.server
  (:require [org.httpkit.server :as server]
            [ring.middleware.params]
            [jumblerg.middleware.cors]
            [bidi.ring]
            [liberator.core :refer [resource defresource]]
            [scramblies.scramble :refer [scramble?]]))

(def handler
  (bidi.ring/make-handler
    ; A resource (obtained through GET) is appropriate for `scramble`,
    ; as it is a pure function of its args, i.e. it is cacheable.
    ["/scramble" (resource
                   :available-media-types ["application/json"]
                   :handle-ok (fn [context]
                                {:result
                                 (scramble?
                                   (get-in context [:request :query-params "str1"])
                                   (get-in context [:request :query-params "str2"]))}))]))

; A fast implementation of a middleware for logging request and response.
; Can be ignored; in a real development we'd have our own best practices around logging.
(defn wrap-req-resp-printer [handler]
  (fn [req]
    (println "Request:")
    (clojure.pprint/pprint req)
    (doto
      (handler req)
      (#(do
          (println "Response:")
          (clojure.pprint/pprint %))))))

(defn start-server [port]
  (server/run-server
    (-> #'handler
        (jumblerg.middleware.cors/wrap-cors #".*") ; accepting browser requests from any origin
        ;wrap-req-resp-printer
        ring.middleware.params/wrap-params)
    {:port port}))