(ns cthulhubot.core
  (:gen-class))

(defn -main
  [& args]
  ;; TODO: read config and run the sync
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment

  ;;; XXX example for what main will need to do
  (def running
    (future
      (bot/run-sync (bot/toke "resources/cthulhu.edn")
                    "https://matrix.spaz.org"
                    60000
                    "s48978_284186_3902_48241_32_21_246_1335"
                    bot/process-stream!)))

  )
