(ns scramblies.server-test
  (:require [scramblies.server :as server]
            [clojure.test :refer :all]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(def port 18080)

(defn test-scramble []
  (let [response @(http/request
                    {:method :get
                     :url (str "http://localhost:" port "/scramble")
                     :query-params {:str1 "aa"
                                    :str2 "a"}
                     :headers {"Accept" "application/json"}})]
    (is (= 200 (:status response)))
    (let [body (json/read-str (:body response))]
      (is (= true (body "result"))))))

; Basic test, useful for regressions, to check that we're not breaking anything.
(deftest test-server
  (let [stop (server/start-server port)]
    (try
      (test-scramble)
      (finally
        (stop)))))
