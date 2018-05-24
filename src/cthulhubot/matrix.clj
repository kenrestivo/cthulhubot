(ns cthulhubot.matrix
  (:require [utilza.repl :as urepl]
            [utilza.log :as ulog]
            [taoensso.timbre :as log]
            [clojure.edn :as edn]
            [cthulhubot.cthulhu :as cthulhu]
            [clj-http.util :as hutil]
            [cheshire.core :as json]
            [clj-http.client :as client]))




(defn sync
  "Takes token, matrix base url, a timeout in milliseconds, 
   and a since (next_batch) stamp in matrix format.
   Returns a stream of events from matrix"
  [token base-url timeout_ms since]
  (client/get (format "%s/_matrix/client/r0/sync" base-url) 
              {:content-type :json
               :as :json
               :accept :json
               :query-params {:since since
                              :timeout timeout_ms
                              :access_token token}
               :throw-exceptions false
               :form-params {}}))


(defn join-room!
  "Given a token, base url, and room id, attempts to join it."
  [token base-url rheum-id ]
  (log/debug "joining" base-url rheum-id)
  (client/post (format "%s/_matrix/client/r0/rooms/%s/join"
                       base-url
                       (hutil/url-encode rheum-id)) 
               {:content-type :json
                :as :json
                :accept :json
                :query-params {:access_token token}
                :throw-exceptions false
                :form-params {}}))



(defn send-message!
  "Takes a token, matrix server base url, a room  id, and the text of the message.
   Sends it to that room. Returns whatever matrix replies."
  [token base-url   rheum-id  msg]
  (log/trace "sending" base-url rheum-id msg)
  (client/post (format "%s/_matrix/client/r0/rooms/%s/send/m.room.message"
                       base-url
                       (hutil/url-encode rheum-id)) 
               {:content-type :json
                :as :json
                :accept :json
                :query-params {:access_token token}
                :throw-exceptions false
                :form-params {:msgtype "m.text"
                              :body msg}}))

