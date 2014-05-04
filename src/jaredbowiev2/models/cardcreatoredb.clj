(ns jaredbowiev2.models.cardcreatoredb
    (:require
            [monger.core :as mgcore :refer [connect! connect set-db! get-db get-db-names]]
            [monger.collection :as mgcoll :refer [insert insert-batch]]
            [monger.operators :refer :all]
            [monger.db :as mgdb :refer [get-collection-names]]
            )
  (:import [org.bson.types ObjectId]
           )
  )


(comment
  "user will create a new deck"
  "-user will add cards to deck"
  "-user will remove cards from deck"
  "-user will view cards from deck"
  "-user will edit cards from deck"
  "-user will delete deck"
  "system will"
  "-display decks to user"
  )

(defn add-new-collection [username]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (if (mgcoll/exists? username)
    (println "Username already exists, unable to create")
    (mgcoll/create username {:max 10000}))
  )

(defn add-empty-deck-list [username]
  (mgcoll/insert username {:_id (ObjectId.) :all-decks []})
  )

(defn new-card-deck [collection-name deck-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [deck-document (mgcoll/find-one-as-map collection-name {:all-decks {$exists true}})
        deck-document-id (deck-document :_id)
        ]
    (mgcoll/update collection-name {:_id deck-document-id} {$push {:all-decks deck-name}})
    )
  )

(defn new-user [username]
  ;(mgcore/connect!)
  ;(mgcore/set-db! (mgcore/get-db "card-db"))
  (add-new-collection username)
  (add-empty-deck-list username)
  )

(defn user-has-decks? [username]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (if (mgcoll/exists? username)
    true
    false
    )
  )

(defn display-all-decks-in-collection [collection-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [deck-map (mgcoll/find-one-as-map collection-name {:all-decks {$exists true}})]
    (deck-map :all-decks)
    )
  )

;{objectid set-name paragraph notes audio highlighing-color}
(defn add-card-to-collection [card-map collection-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))

  )

(defn view-all-collections-in-db [db-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db db-name))
  (mgdb/get-collection-names)
  )


(defn view-all-dbes []
  (mgcore/connect!)
  (mgcore/get-db-names)
  )


(defn add-card-to-db [dbname card-collection card-map]
  (mgcore/connect!)
  (mgcore/use-db! dbname)
  (let [entire-map-to-insert (merge {:_id (ObjectId.)} card-map)]
    ;(println entire-map-to-insert)
    (mgcoll/insert card-collection entire-map-to-insert)
    )
  )
