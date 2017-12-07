;; gorilla-repl.fileformat = 1

;; @@
(ns joebrains.core
  (:gen-class))

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
    (clojure.string/split words #"\s+")))

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

;this is finicky and i don't quite know why
;something about an indexoutofbounds error in java, mostly when run from python script
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
      (if (>= 2 (count lumpleft)) 
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
  (spit exportsource (clojure.string/join " " genome)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (export (evolve 200 50)))

;; @@
;; ->
;;; Starting evolution...
;;; ======================
;;; Generation: 0
;;; Best badness: 191
;;; Best genome: (but and is the off That but out. roam she mine is sold bisexual. only big friends for me. to can engage unhappy Youtube attention)
;;; Median badness: 211
;;; ======================
;;; Generation: 1
;;; Best badness: 190
;;; Best genome: [good thinking is. I like important awful my them definitely thing. you doing your only big friends for me. to can engage unhappy Youtube attention]
;;; Median badness: 200
;;; ======================
;;; Generation: 2
;;; Best badness: 190
;;; Best genome: [good thinking is. I like important awful my them definitely thing. you doing your only big friends for me. to can engage unhappy Youtube attention]
;;; Median badness: 191
;;; ======================
;;; Generation: 3
;;; Best badness: 190
;;; Best genome: [good thinking is. I like important awful my them definitely mine the sold bisexual. only big friends for me. to can engage unhappy Youtube attention]
;;; Median badness: 190
;;; ======================
;;; Generation: 4
;;; Best badness: 169
;;; Best genome: [good thinking is. I like important and my them definitely thing. you doing your only big friends for me. to can engage unhappy Youtube attention]
;;; Median badness: 190
;;; ======================
;;; Generation: 5
;;; Best badness: 169
;;; Best genome: [good thinking is. I like important and my them definitely thing. you doing your only big friends for me. to can engage unhappy Youtube attention]
;;; Median badness: 190
;;; ======================
;;; Generation: 6
;;; Best badness: 158
;;; Best genome: [good thinking is. I like important and my them definitely thing. you them bisexual. only big friends for me. to can engage unhappy and attention]
;;; Median badness: 169
;;; ======================
;;; Generation: 7
;;; Best badness: 158
;;; Best genome: [good thinking is. I like important and my them definitely thing. you doing your only big friends for me. to can engage unhappy and attention]
;;; Median badness: 169
;;; ======================
;;; Generation: 8
;;; Best badness: 158
;;; Best genome: [good thinking is. I like important and my them definitely thing. you doing your only big friends for me. to can aggression unhappy and attention]
;;; Median badness: 158
;;; ======================
;;; Generation: 9
;;; Best badness: 158
;;; Best genome: [good thinking is. I like important and would them definitely thing. you doing your only big friends for me. to can engage unhappy and attention]
;;; Median badness: 158
;;; ======================
;;; Generation: 10
;;; Best badness: 158
;;; Best genome: [good thinking is. I like important and my them definitely thing. you doing your world big friends for me. to can aggression unhappy and attention]
;;; Median badness: 158
;;; ======================
;;; Evolution complete.
;;; Best genome: [good thinking is. I like important and my them definitely thing. you I your only big friends for me. to I engage unhappy and attention]
;;; Best badness: 158
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;joebrains.core/-main</span>","value":"#'joebrains.core/-main"}
;; <=

;; @@

;; @@
