(ns cthulhubot.bot
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


(defn stream->mentions
  "Takes a stream, returns all the room id's in which the bot was mentioned."
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
                                       ;; TODO: make this configurable?  maybe cthulhu is taken on this homeserver?
                                       (.contains (.toLowerCase x) "cthulhu"))))
                        count)])
       (filter (comp pos? second))
       (map first)
       (map name)))


(defn stream->invites
  "Takes a stream, returns all the room id's to which the bot was invited."
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


(defn process-stream!
  "Takes a token, a matrix base url, and a stream.
   Processes each event on the stream, and does side-effecting things like spin off replies."
  [token base-url stream]
  (log/trace "processing stream" base-url)
  (doseq [room (stream->invites stream)]
    (future (ulog/catcher
             (matrix/join-room! token base-url room))))
  (doseq [room (stream->mentions stream)]
    (future (ulog/catcher
             (log/debug "sending message to" room)
             (matrix/send-message! token base-url room (cthulhu/exclamation (+ 5 (rand-int 25))))))))



(defn run-sync*
  "Takes a token, base-url to the matrix server, timout in milliseconds, 
    an initial sync timestamp in matrix format, and a function to run on every stream returned from sync, 
    which takes a token, base-url and the stream map.
    This is essentially the main loop, does not exit."
  [token base-url timeout initial-sync f]
  (log/info "starting loop" base-url timeout initial-sync f)
  (loop [nb initial-sync]
    (log/trace "next loop" nb)
    (let [{:keys [next_batch] :as stream} (ulog/catcher
                                           (:body (matrix/sync token base-url timeout nb)))]
      (log/trace "next batch is:" next_batch)
      (ulog/catcher
       (f token base-url stream))
      (recur (or next_batch nb)))))

(defn run-sync
  "Takes a token, base-url to the matrix server, timout in milliseconds, 
    and a function to run on every stream returned from sync, 
    which takes a token, base-url and the stream map. Gets initial sync,
    calls the main loop, and does not exit."
  [token base-url timeout f]
  (log/info "getting initial sync")
  (let [initial-sync (-> (matrix/initial-sync token base-url timeout)
                         :body
                         :next_batch)]
    (log/debug "initial sync found:" initial-sync)
    (run-sync* token base-url timeout initial-sync f)))


