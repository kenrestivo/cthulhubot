(ns cthulhubot.core
  (:gen-class)
  (:require [cthulhubot.bot :as bot]
            [cthulhubot.config :as config]))

(defn -main
  [config-file]
  (let [{:keys [base-url timeout creds]} (config/read-from-file config-file)
        {:keys [access_token]} creds]
    (bot/run-sync access_token
                  base-url
                  timeout
                  bot/process-stream!)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment



  )
