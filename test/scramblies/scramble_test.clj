(ns scramblies.scramble-test
  (:require [clojure.test :refer :all]
            [scramblies.scramble :refer [scramble?]]
            [multiset.core :refer [multiset subset?]]
            [clojure.test.check :as check]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.results :refer [Result]]))

; Unit tests with manual samples

(deftest test-dec-or-dissoc
  (let [dec-or-dissoc (var scramblies.scramble/dec-or-dissoc)] ; it is private
    (is (= {} (dec-or-dissoc {} :a)))
    (is (= {} (dec-or-dissoc {:a 1} :a)))
    (is (= {:a 1} (dec-or-dissoc {:a 2} :a)))))

(deftest empty-source
  (is (true? (scramble? "" "")))
  (is (false? (scramble? "" "a")))
  (is (false? (scramble? "" "ab"))))

(deftest missing-chars
  (is (false? (scramble? "aa" "ab")))
  (is (false? (scramble? "a" "c"))))

(deftest not-enough-chars
  (is (false? (scramble? "ba" "aa")))
  (is (false? (scramble? "bba" "baa")))
  (is (false? (scramble? "aaab" "aabb"))))

(deftest successes
  (is (true? (scramble? "a" "a")))
  (is (true? (scramble? "aa" "a")))
  (is (true? (scramble? "aaa" "a")))
  (is (true? (scramble? "baaa" "a")))
  (is (true? (scramble? "ab" "ba")))
  (is (true? (scramble? "abc" "ba")))
  (is (true? (scramble? "abbcc" "ba")))
  (is (true? (scramble? "abbcc" "bba")))
  (is (true? (scramble? "abbcc" "bbac"))))

(deftest from-challenge-description
  (is (true? (scramble? "rekqodlw" "world")))
  (is (true? (scramble? "cedewaraaossoqqyt" "codewars")))
  (is (false? (scramble? "katas" "steak"))))

; Property-based testing comparing with less performant oracle function

(defn scramble-alt? [str1 str2]
  (subset? (apply multiset str2) (apply multiset str1)))

(defn gen-char-from [s]
  "Generates chars picking from the given string s"
  (gen/one-of (map gen/return s)))

(defn gen-string-from [s]
  "Generates string with chars picked from the given string s"
  (gen/fmap clojure.string/join (gen/vector (gen-char-from s))))

(def scramble-correct-prop
  ; Generate strings from 3 chars only, to obtain enough true cases for scramble?
  (prop/for-all [str1 (gen-string-from "abc")
                 str2 (gen-string-from "abc")]
    (let [expected (scramble-alt? str1 str2)]
      (reify Result
        (pass? [_] (= expected (scramble? str1 str2)))
        (result-data [_] {:expected expected})))))

(deftest check-with-alternative-implementation
  (check/quick-check 100 scramble-correct-prop))

(comment ; run manually to visually check generated tests
  (check/quick-check
    10
    scramble-correct-prop
    :reporter-fn (fn [result]
                   (println (select-keys result [:type
                                                 :args
                                                 :pass?
                                                 :result-data])))))

(comment ; run manually to visually check if enough true cases are generated
  (let [summary (atom {})]
    (check/quick-check
      100
      scramble-correct-prop
      :reporter-fn (fn [result]
                     (case (:type result)
                       :trial (let [k (-> result :result-data :expected)]
                                (swap! summary update k #(if % (inc %) 0)))
                       :complete (println "Number of cases checked:" @summary)
                       nil)))))
