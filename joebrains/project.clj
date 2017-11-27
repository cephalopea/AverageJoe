(defproject joebrains "0.1.0-SNAPSHOT"
  :description "brains for MedianJoseph reddit bot"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
 				 [thinktopic/cortex "0.9.11"]
				 [thinktopic/experiment "0.9.11"]]
  :plugins [[lein-gorilla "0.4.0"]]
  :main ^:skip-aot joebrains.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
