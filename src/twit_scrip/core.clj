(ns twit-scrip.core
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:gen-class))

(def app-consumer-key (System/getenv "KEY"))
(def app-consumer-secret (System/getenv "SECRET"))

(def token-url "https://api.twitter.com/oauth2/token")

(def search-base "https://api.twitter.com/1.1/search/tweets.json?q=")
(def search-query "6wunderkinder")
(def search-url (str search-base search-query "&result_type=recent&count=100"))

(defn get-token []
  (let [options {
          :basic-auth [app-consumer-key app-consumer-secret]
          :form-params {:grant_type "client_credentials"}}
        result (client/post token-url options)
        body-json (json/read-str (:body result) :key-fn keyword)]
    (:access_token body-json)))

(defn get-6w-tweets []
  (let [options {
          :headers {
            :authorization (str "Bearer " (get-token))
          }}
        result (client/get search-url options)
        body-json (json/read-str (:body result) :key-fn keyword)]
    (:statuses body-json)))

(defn print-status [status]
  (let [user (:user status)
        handle (:screen_name user)
        tweet (:text status)]
    (println (str "[" handle "] " tweet))))

(defn -main [& args]
  (let [statuses (get-6w-tweets)]
    (doseq [status statuses]
      (print-status status))))