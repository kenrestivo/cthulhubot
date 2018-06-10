(ns cthulhubot.config
  (:require [utilza.repl :as urepl]
            [utilza.log :as ulog]
            [taoensso.timbre :as log]
            [clojure.edn :as edn]
            [cthulhubot.cthulhu :as cthulhu]
            [cthulhubot.matrix :as matrix]
            [clj-http.util :as hutil]
            [cheshire.core :as json]
            [clj-http.client :as client]))




(defn toke
  "Obtains a token saved in an EDN file map as :access_token"
  [filename]
  (-> filename
      slurp
      edn/read-string
      :access_token))




(defn read-from-file
  [filename]
  (-> filename
      slurp
      edn/read-string))
