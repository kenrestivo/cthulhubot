(ns user
  (:require [utilza.repl :as urepl]
            [utilza.log :as ulog]
            [taoensso.timbre :as log]
            [clojure.edn :as edn]
            [cthulhubot.bot :as bot]
            [clojure.java.io :as jio]
            [clj-http.util :as hutil]
            [cheshire.core :as json]
            [clj-http.client :as client]))


(comment

  ;; TODO: Initial poll

  (def running
    (future
      (bot/run-sync (bot/toke "resources/cthulhu.edn")
                    "https://matrix.spaz.org"
                    60000
                    "s48978_284186_3902_48241_32_21_246_1335"
                    bot/process-stream!)))

  
  (future-cancel running)
  (future-done? running)


  (log/set-level! :trace)

  (log/trace :wtf)


  (ulog/spewer
   (let [stream (->> "/home/cust/spaz/logs/invite.edn"
                     slurp
                     edn/read-string)]
     (:next_batch stream)))


  
  )
