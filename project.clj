(defproject cthulhubot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-http "3.9.0"]
                 [org.slf4j/log4j-over-slf4j "1.7.25"]
                 [org.slf4j/slf4j-simple "1.7.25"]
                 [utilza "0.1.98"]
                 [com.taoensso/timbre "4.10.0"]
                 [clj-time "0.14.3"]
                 [org.clojure/tools.trace "0.7.9"]
                 [cheshire "5.8.0"]]
  :main ^:skip-aot cthulhubot.core
  :target-path "target/%s"
  :aliases {"tr" ["with-profile" "+user,+dev,+server"
                  "do" "trampoline" "repl" ":headless"]
            "slamhound" ["run" "-m" "slam.hound" "clj-src/"]
            }
  :profiles {:uberjar {:aot :all}
             :repl {:repl-options {:port 7777
                                   }}
             :user {:dependencies [[lein-ancient "0.6.15" 
                                    :exclusions [com.fasterxml.jackson.core/jackson-annotations]]
                                   ]}})
