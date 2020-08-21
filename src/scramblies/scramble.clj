(ns scramblies.scramble)

(defn- dec-or-dissoc [m k]
  "Given a map m with positive int values, decrements value for key k. Removes entry if 0 is reached."
  (case (m k)
    nil m
    1 (dissoc m k)
    (update m k dec)))

(defn scramble?
  "Returns true iff a portion of str1 characters can be rearranged to match str2"
  [str1 str2]
  (and (>= (count str1) (count str2)) ; optimization for trivial case
       (empty?
         (reduce
           (fn [freq-map ch]
             (if (empty? freq-map)
               (reduced freq-map)
               (dec-or-dissoc freq-map ch)))
           (frequencies str2) ; only traverses shorter string fully
           str1))))
