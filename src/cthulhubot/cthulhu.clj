(ns cthulhubot.cthulhu
  "Via TEttinger via  https://gist.github.com/stathissideris/f1bced0dfdebdaf488eb"
  (:require [utilza.repl :as urepl]
            [utilza.log :as ulog]
            [clojure.string :as st]
            [taoensso.timbre :as log]))


(def boundaries ["!"".""..."])
(def vowels ["a""i""o""e""u""a""i""o""e""u""ia""ai""ei"])
(def possible-syllables [1 2 1 2 1 2 3])
(def opening-consonants ["S""T""K""N""Y""P""K""L""G""Gl""Th""Sh""Ny""Ft""Hm""Zvr""Cth"])
(def opening-vowels (for [v (map st/capitalize vowels)
                          c (map st/lower-case opening-consonants)]
                      (str v c)))
(def closing-consonants ["l""p""s""t""n""k""g""x""rl""th""gg""gh""ts""lt""rk""kh""sh""ng""shk"])
(def mid-consonants (concat closing-consonants ["h""gl""gr""nd""mr""vr""kr"]))

(defn nested-str
  [& coll]
  (apply str (flatten coll)))

(defn rand-str
  [num-blanks pieces]
  (nested-str (rand-nth (concat
                         (repeat num-blanks "")
                         pieces))))

(defn vowel
  []
  (rand-str 0 vowels))

(defn boundary
  []
  (rand-str 6 boundaries))

(defn word
  []
  [(rand-str 0 (concat (apply concat (repeat (* 5/3 (count vowels)) opening-consonants)) opening-vowels))
   (repeatedly (dec (rand-nth possible-syllables))
               #(do [(vowel)
                     (rand-str 12 [(str \' (vowel)) (str \- (vowel))])
                     (rand-str 0 mid-consonants)]))
   [(vowel)
    (rand-str 9 [(str \' (vowel)) (str \- (vowel))])
    (rand-str 10 closing-consonants)]])

(defn phrase
  []
  (let [gap (boundary)
        w (word)]
    [gap  (if (seq gap)
            [" " w]
            [(rand-str 4 ",")" "(st/lower-case (nested-str w))])]))

(defn initial-capitalize
  [s]
  (apply str (st/upper-case (first s)) (rest s)))


(defn exclamation
  "Returns an exclamation of length words"
  [length]
  (initial-capitalize (re-find #"\p{L}.+" (nested-str [(repeatedly length phrase) "."]))))

(defn speak!
  []
  (println (exclamation 50)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment
  
  (exclamation)

  (speak!)

  
  )
