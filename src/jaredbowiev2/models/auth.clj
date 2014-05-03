(ns jaredbowiev2.models.auth
  (:require [taoensso.carmine :as car :refer (wcar)]
            [noir.util.crypt :as crypt])
  )


(def server1-conn {:pool {} :spec {:host "127.0.0.1" :port 6379}}) ; See `wcar` docstring for opts
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn add-user [username password]
  (wcar* (car/select "5"))
  (wcar* (car/set username password))
  )

(defn get-all-users []
  (wcar* (car/select "5"))
  (wcar* (car/keys "*"))
  )

(defn get-user [username]
  (wcar* (car/select "5"))
  (wcar* (car/get username))
  )

(defn is-username? [username]
  (wcar* (car/select "5"))
  (if (= 0 (wcar* (car/exists username)))
    false
    true
    )
  )

(defn valid-password [username password]
  (wcar* (car/select "5"))
  (if (is-username? username)
    (let [encrypted-pass (wcar* (car/get username))]
      (crypt/compare password encrypted-pass)
      )
    false
    )
  )
