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

(def wordsource
  "/Users/Caiti/Documents/Github/AverageJoe/redditinput.txt")

(defn getwords
  [filepath]
  (let [words (slurp filepath)]
    (clojure.string/split words #" ")
    ))

(def words
  (getwords wordsource))

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

;(findword "Boop!" words)

;;takes a string (word) and two lists (lump and lomp), returns map
(defn usewordmap
  [word lump lomp passedmap]
  (let [wordmap (assoc {} :word word)]
    (let [nextmap (assoc wordmap :next (into [] lump))]
      (let [nextnextmap (assoc nextmap :nextnext (into [] lomp))]
        (assoc passedmap (keyword word) nextnextmap)))))

;(usewordmap "Hello" (first (findword "Hello" words)) (last (findword "Hello" words)) {})

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
        (= 10 (count lumpleft)) wordsmap
        (if
          (empty? used) (let [newmap (usewordmap word (first (findword word container)) (last (findword word container)) wordsmap)]
                          (recur (first (rest lumpleft)) (cons word used) (rest lumpleft) newmap))
          (if
            (contains? (set used) word) (recur (first (rest lumpleft)) used (rest lumpleft) wordsmap)
            (let [newmap (usewordmap word (first (findword word container)) (last (findword word container)) wordsmap)]
                (recur (first (rest lumpleft)) (cons word used) (rest lumpleft) newmap))))))))

(def wordmap 
  (processcontainer words))

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

;;returns a random genome with 4 random integers and 2 fibonacci numbers at the end
(defn random-genome
  []
  (repeatedly 10 #(rand-nth words)))

;(random-genome)

;;returns a number indicating how bad the genome is at being an ascending fibonaccish trio
(defn badness
  [genome]
  (let [member 0
        badscore 0]
    (loop [n member
           bad badscore]
      (let [thisword (nth genome n)
            nextword (nth genome (inc n))]
        (let [thisnext (get-in wordmap [(keyword thisword) :next])]
          (if
            (= n (- (count genome) 2)) bad
            (if
              (contains? (set (thisnext)) nextword) (recur (inc n) bad)
              (recur (inc n) (+ 5 bad)))))))))

(badness ("Boop!" "Boop!" "world!" "Doing" "world!" "world!" "Something!" "Boop!" "world!" "Boop!"))

 (comment
(defn mutate
  "Returns a mutated version of genome."
  [genome wordlist]
  (let [mutation-point (rand-int 9)]
    (let [newword (rand-nth wordlist)]
      (assoc (vec wordlist) mutation-point newword))))

(defn crossover
  "Returns the result of crossing over genome1 and genome2."
  [genome1 genome2]
  (let [crossover-point (rand-int 8)]
    (vec (concat (take crossover-point genome1)
                 (drop crossover-point genome2)))))

(defn select
  "Returns a best genome of a randomly selected 5 from the sorted population."
  [population]
  (let [pop-size (count population)]
    (nth population
         (apply min (repeatedly 5 #(rand-int pop-size))))))

(defn evolve
  "Runs a genetic algorithm to solve the silly problem."
  [pop-size]
  (println "Starting evolution...")
  (loop [generation 0
         population (sort-by badness (repeatedly pop-size (random-genome)))]
    (let [best (first population)]
      (if (> generation 100)
        (do 
          (println "Warning: suboptimal result.")
          (println "Best genome:" best))
        (let [best-badness (badness best)]
          (println "======================")
          (println "Generation:" generation)
          (println "Best badness:" best-badness)
          (println "Best genome:" best)
          (println "Median badness:" (badness (nth population (int (/ pop-size 2)))))
          (if (= 0 best-badness) ;; success!
            (println "Success:" best)
            (recur 
              (inc generation)
              (sort-by badness      
                       (concat
                         (repeatedly (* 1/2 pop-size) #(mutate (select population)))
                         (repeatedly (* 1/4 pop-size) #(crossover (select population)
                                                                  (select population)))
                         (repeatedly (* 1/4 pop-size) #(select population)))))))))))

(evolve 100)
 )

;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@

;; @@
