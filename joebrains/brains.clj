;; gorilla-repl.fileformat = 1

;; **
;;; # Gorilla REPL
;;; 
;;; Welcome to gorilla :-)
;;; 
;;; Shift + enter evaluates code. Hit ctrl+g twice in quick succession or click the menu icon (upper-right corner) for more commands ...
;;; 
;;; It's a good habit to run each worksheet in its own namespace: feel free to use the declaration we've provided below if you'd like.
;; **

;; @@
(ns harmonious-peak
  (:require [gorilla-plot.core :as plot]))

;;make predictive text thing
;;have joe predict next 100 words and reply with that
;;do something neat with genetic algorithms

;;sample list of words
(def samplewords
  '("the" "dog" "ran" "towards" "the" "big" "fat" "steak" "so" "he" "could" "eat" "it" "but" "the" "dog" "was" "eaten" "by" "a" "big" "bear" "towards" "the" "end" "of" "his" "life"))

;;takes a word and a list with at least 3 items
;;returns a tuple of ((next words) (2 words later))
(defn findword
  [word container]
  (let [lamp '()
        lomp '()
        index 0]
    (loop [lump lamp
           glomp lomp
           n index]
      (if 
        (= (- (count container) 2) n) (if 
                                        (= (nth container n) word) (cons (cons (nth container (+ 2 n)) glomp) (cons (cons (nth container (inc n)) lump) '()))
                                        (cons glomp (cons lump '())))
        (if 
          (= (nth container n) word) (recur (cons (nth container (inc n)) lump) (cons (nth container (+ 2 n)) glomp) (inc n))
          (recur lump glomp (inc n)))))))

;;takes a string (word) and two lists (lump and lomp), returns map
(defn usewordmap
  [word lump lomp passedmap]
  (let [wordmap (assoc {} :word word)]
    (let [nextmap (assoc wordmap :next (into [] lump))]
      (let [nextnextmap (assoc nextmap :nextnext (into [] lomp))]
        (assoc passedmap (keyword word) nextnextmap)))))

(usewordmap "the" (first (findword "the" samplewords)) (last (findword "the" samplewords)) {})

;;processes a list of words, returns a big map of each word with its next and nextnext words lists
;;output of (processcontainer samplewords) shown in cheatcode
(defn processcontainer
  [container]
  (let [usedwds '()
        startwd (first container)
        wordmup {}]
    (loop [word startwd
           used usedwds
           lumpleft container
           wordsmap wordmup]
      (if
        (= 2 (count lumpleft)) wordsmap
        (if
          (empty? used) (let [newmap (usewordmap word (first (findword word container)) (last (findword word container)) wordsmap)]
                          (recur (first (rest lumpleft)) (cons word used) (rest lumpleft) newmap))
          (if
            (contains? (set used) word) (recur (first (rest lumpleft)) used (rest lumpleft) wordsmap)
            (let [newmap (usewordmap word (first (findword word container)) (last (findword word container)) wordsmap)]
              (recur (first (rest lumpleft)) (cons word used) (rest lumpleft) newmap)
              )))))))

(processcontainer samplewords)

;;this is the map (processcontainer samplewords) produces
;;just a reference for me, not to be used
(def cheatcode
  {:but {:word "but", :next ["dog"], :nextnext ["the"]}
   :eaten {:word "eaten", :next ["a"], :nextnext ["by"]}
   :could {:word "could", :next ["it"], :nextnext ["eat"]}
   :was {:word "was", :next ["by"], :nextnext ["eaten"]}
   :bear {:word "bear", :next ["the"], :nextnext ["towards"]}
   :big {:word "big", :next ["towards" "steak"], :nextnext ["bear" "fat"]}
   :dog {:word "dog", :next ["eaten" "towards"], :nextnext ["was" "ran"]}
   :so {:word "so", :next ["could"], :nextnext ["he"]}
   :the {:word "the", :next ["of" "was" "fat" "ran"], :nextnext ["end" "dog" "big" "dog"]}
   :fat {:word "fat", :next ["so"], :nextnext ["steak"]}
   :it {:word "it", :next ["the"], :nextnext ["but"]}
   :by {:word "by", :next ["big"], :nextnext ["a"]}
   :he {:word "he", :next ["eat"], :nextnext ["could"]}
   :eat {:word "eat", :next ["but"], :nextnext ["it"]}
   :towards {:word "towards", :next ["end" "big"], :nextnext ["the" "the"]}
   :end {:word "end", :next ["his"], :nextnext ["of"]}
   :of {:word "of", :next ["life"], :nextnext ["his"]}
   :ran {:word "ran", :next ["the"], :nextnext ["towards"]}
   :steak {:word "steak", :next ["he"], :nextnext ["so"]}
   :a {:word "a", :next ["bear"], :nextnext ["big"]}})
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;harmonious-peak/cheatcode</span>","value":"#'harmonious-peak/cheatcode"}
;; <=

;; @@

;; @@
