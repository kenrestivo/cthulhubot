(ns cthulhubot.bot
  (:require [utilza.repl :as urepl]
            [utilza.log :as ulog]
            [taoensso.timbre :as log]
            [clojure.edn :as edn]
            [cthulhubot.cthulhu :as cthulhu]
            [clj-http.util :as hutil]
            [matrix-client-server-api.api.session-management :as sess]
            [matrix-client-server-api.api.room-membership :as room-member]
            [matrix-client-server-api.api.user-data :as user-data]
            [matrix-client-server-api.api.room-discovery :as room-discovery]
            [matrix-client-server-api.api.room-directory :as room-directory]
            [matrix-client-server-api.api.presence :as presence]
            [matrix-client-server-api.core :as matrix]
            [cheshire.core :as json]
            [clj-http.client :as client]))




(defn toke
  [filename]
  (-> filename
      slurp
      edn/read-string
      :access_token))


(defn stream->mentions
  [stream]
  ;; could be a transducer maybe?
  (->> (for [[room evs] (some-> stream
                                :rooms
                                :join)]
         [room (some->> evs
                        :timeline
                        :events
                        (map #(some->> % :content :body))
                        (filter (fn [x]
                                  (and (string? x)
                                       (.contains (.toLowerCase x) "cthulhu"))))
                        count)])
       (filter (comp pos? second))
       (map first)
       (map name)))


(defn stream->invites
  [stream]
  ;; could be a transducer maybe?
  (->> (for [[room evs] (some-> stream
                                :rooms
                                :invite)]
         [room (some->> evs
                        :invite_state
                        :events
                        (filter #(some-> %
                                         :content
                                         :membership
                                         (= "invite")))
                        count)])
       (filter (comp pos? second))
       (map first)
       (map name)))

(defn sync
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


(defn process-stream!
  [token base-url stream]
  (log/trace "processing stream" base-url)
  (doseq [room (stream->invites stream)]
    (future (ulog/catcher
             (join-room! token base-url room))))
  (doseq [room (stream->mentions stream)]
    (future (ulog/catcher
             (log/debug "sending message to" room)
             (send-message! token base-url room (cthulhu/exclamation (+ 5 (rand-int 25))))))))

(defn run-sync
  [token base-url timeout initial-sync f]
  (log/info "starting loop" base-url timeout initial-sync f)
  (loop [nb initial-sync]
    (log/trace "next loop" nb)
    (let [{:keys [next_batch] :as stream} (ulog/catcher
                                           (:body (sync token base-url timeout nb)))]
      (log/trace "next batch is:" next_batch)
      (ulog/catcher
       (f token base-url stream))
      (recur (or next_batch nb)))))

