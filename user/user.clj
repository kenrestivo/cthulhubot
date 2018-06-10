(ns user
  (:require [utilza.repl :as urepl]
            [utilza.log :as ulog]
            [taoensso.timbre :as log]
            [cthulhubot.matrix :as m]
            [clojure.edn :as edn]
            [cthulhubot.bot :as bot]
            [clojure.java.io :as jio]
            [clj-http.util :as hutil]
            [cheshire.core :as json]
            [clj-http.client :as client]))


(comment



  
  (def running
    (future
      (bot/run-sync (bot/toke "resources/cthulhu.edn")
                    "https://matrix.spaz.org"
                    60000
                    bot/process-stream!)))

  
  (future-cancel running)
  (future-done? running)


  (log/set-level! :debug)

  (log/trace :wtf)


  (ulog/spewer
   (let [stream (->> "/home/cust/spaz/logs/invite.edn"
                     slurp
                     edn/read-string)]
     (:next_batch stream)))






  
  )
