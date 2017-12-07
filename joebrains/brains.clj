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

;A sample input list, formatted properly for use by the rest of the script.
(def samplewords
  '("the" "dog" "ran" "towards" "the" "big" "fat" "steak" "so" "he" "could" "eat" "it" "but" "the" "dog" "was" "eaten" "by" "a" "big" "bear" "towards" "the" "end" "of" "his" "life"))

;The filepath of the source file used by this script.
(def wordsource
  "/Users/Caiti/Documents/Github/AverageJoe/redditinput.txt")

(def exportsource
  "/Users/Caiti/Documents/Github/AverageJoe/joeoutput.txt")

(defn getwords
  "Retrieves contents of a file, splits its contents at spaces. Output is formatted like samplewords."
  [filepath]
  (let [words (slurp filepath)]
    (clojure.string/split words #" ")))

;Doesn't really need a def, but makes my life easier. Formatted output of source file.
(def words
  (getwords wordsource))

(defn findoneword
  "Takes a word and a list with at least 2 items. Returns a list of (next words)."
  [word container]
  (let [lamp '()
        index 0]
    (loop [lump lamp
           n index]
      (if (= (- (count container) 2) n)
        (if (= (nth container n) word)
          (cons (nth container (inc n)) lump)
          lump)
        (if (= (nth container n) word) 
          (recur (cons (nth container (inc n)) lump) (inc n))
          (recur lump (inc n)))))))

(defn useonewordmap
  "Takes a string (word) and a list (lump). Returns {:keyedword {:word word :next (lump) :nextnext (lomp)}}"
  [word lump passedmap]
  (let [wordmap (assoc {} :word word)]
    (let [nextmap (assoc wordmap :next lump)]
      (assoc passedmap (keyword word) nextmap))))

(defn processonecontainer
  "Processes a list of words, returns a big map of word maps from useonewordmap."
  [container]
  (let [usedwds '()
        startwd (first container)
        wordmup {}]
    (loop [word startwd
           used usedwds
           lumpleft container
           wordsmap wordmup]
      (if (= 10 (count lumpleft)) 
        wordsmap
        (if (empty? used) 
          (let [newmap (useonewordmap word (findoneword word container) wordsmap)]
            (recur (first (rest lumpleft)) (cons word used) (rest lumpleft) newmap))
          (if (contains? (set used) word) 
            (recur (first (rest lumpleft)) used (rest lumpleft) wordsmap)
            (let [newmap (useonewordmap word (findoneword word container) wordsmap)]
              (recur (first (rest lumpleft)) (cons word used) (rest lumpleft) newmap))))))))

;contains output of (processonecontainer words)
(def wordmap 
  (processonecontainer words))

(defn random-genome
  "Returns a random genome of ten words from a given container of words."
  [wordlist]
  (repeatedly 25 #(rand-nth wordlist)))

(defn badness
  "Badness function based on whether each next word in the genome is in the list of possible next words."
  [genome]
  (let [member 0
        badscore 1]
    (loop [n member
           bad badscore]
      (let [thisword (nth genome n)
            nextword (nth genome (inc n))]
        (let [thisnext (get-in wordmap [(keyword thisword) :next])]
          (if (= n (- (count genome) 2)) 
            bad
            (if (contains? (set thisnext) nextword)
              (if (< (count thisnext) 4)
                (recur (inc n) (- bad 1))
                (recur (inc n) bad))
              (recur (inc n) (+ 10 bad)))))))))

(defn mutate
  "Returns a mutated version of genome."
  [genome wordlist]
  (let [mutation-point (rand-int (dec (count genome)))]
    (let [newword (rand-nth wordlist)]
      (assoc (vec genome) mutation-point newword))))

(defn crossover
  "Returns the result of crossing over genome1 and genome2."
  [genome1 genome2]
  (let [crossover-point (rand-int (- (count genome1) 2))]
    (vec (concat (take crossover-point genome1)
                 (drop crossover-point genome2)))))

(defn select
  "Returns a best genome of a randomly selected 5 from the sorted population."
  [population]
  (let [pop-size (count population)]
    (nth population 
         (apply min (repeatedly 5 #(rand-int pop-size))))))

(defn evolve
  "Runs a genetic algorithm to generate reasonably acceptable response."
  [popsize generations]
  (println "Starting evolution...")
  (loop [generation 0
         population (sort-by badness (repeatedly popsize #(random-genome words)))]
    (let [best (first population)]
      (let [best-badness (badness best)]
        (if (> generation generations)
          (do 
            (println "======================")
            (println "Evolution complete.")
            (println "Best genome:" best)
            (println "Best badness:" best-badness)
            best)
          (do
            (println "======================")
            (println "Generation:" generation)
            (println "Best badness:" best-badness)
            (println "Best genome:" best)
            (println "Median badness:" (badness (nth population (int (/ popsize 2)))))
            (recur 
              (inc generation)
              (sort-by badness      
                       (concat
                         (repeatedly (* 1/2 popsize) #(mutate (select population) words))
                         (repeatedly (* 1/2 popsize) #(crossover (select population)
                                                                  (select population)))
                         (repeatedly (* 1/4 popsize) #(select population)))))))))))

(defn export
  "Exports best genome to .txt file."
  [genome]
  (do
    (spit exportsource "'")
  	(spit exportsource (clojure.string/join " " genome) :append true)
    (spit exportsource "'" :append true)))

(export (evolve 10 10))
;; @@
;; ->
;;; Starting evolution...
;;; ======================
;;; Generation: 0
;;; Best badness: 211
;;; Best genome: (in sleeve, what I college, pet and to the hills. to who actually. but thing. and though never and/or simple. happily years socialized of for)
;;; Median badness: 231
;;; ======================
;;; Generation: 1
;;; Best badness: 211
;;; Best genome: [well Millennials. donut dementia himself an don&#x27;t do to take to everyone is people here. hills. just at better rich I&#x27;m help to lose some]
;;; Median badness: 211
;;; ======================
;;; Generation: 2
;;; Best badness: 201
;;; Best genome: [in sleeve, what I college, pet and to the hills. to who actually. but thing. and though just better rich huge help to lose some]
;;; Median badness: 211
;;; ======================
;;; Generation: 3
;;; Best badness: 191
;;; Best genome: [in sleeve, what I college, pet think to the take to everyone is people here. hills. just just better rich huge help to lose some]
;;; Median badness: 211
;;; ======================
;;; Generation: 4
;;; Best badness: 181
;;; Best genome: [in the what I college, pet think to the take to everyone is people here. hills. just just better rich huge help to lose some]
;;; Median badness: 201
;;; ======================
;;; Generation: 5
;;; Best badness: 181
;;; Best genome: [in the what I college, pet think to the take to everyone of people here. hills. just just better rich huge help to lose some]
;;; Median badness: 191
;;; ======================
;;; Generation: 6
;;; Best badness: 181
;;; Best genome: [in the what I college, pet think to the take to everyone is people here. hills. just just any rich huge help to lose some]
;;; Median badness: 181
;;; ======================
;;; Generation: 7
;;; Best badness: 181
;;; Best genome: [in the what I college, pet think to the take to everyone is people here. hills. just just better rich huge of to lose some]
;;; Median badness: 181
;;; ======================
;;; Generation: 8
;;; Best badness: 181
;;; Best genome: [in the what I college, pet think to the take to everyone is people here. hills. just side better rich huge help to lose some]
;;; Median badness: 181
;;; ======================
;;; Generation: 9
;;; Best badness: 171
;;; Best genome: [in the what I college, pet think to the take to everyone is people here. hills. just just better rich it. things to lose some]
;;; Median badness: 181
;;; ======================
;;; Generation: 10
;;; Best badness: 161
;;; Best genome: [in the what I college, pet think to the take to everyone is people here. hills. just just better rich of things to lose some]
;;; Median badness: 181
;;; ======================
;;; Evolution complete.
;;; Best genome: [in the what I college, pet think to the take to everyone is people here. hills. just just (2 rich of things to lose some]
;;; Best badness: 161
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
 
;; @@
