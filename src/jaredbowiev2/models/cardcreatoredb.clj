(ns jaredbowiev2.models.cardcreatoredb
    (:require
            [monger.core :as mgcore :refer [connect! connect set-db! get-db get-db-names]]
            [monger.collection :as mgcoll :refer [insert insert-batch]]
            [monger.operators :refer :all]
            [monger.db :as mgdb :refer [get-collection-names]]
            [monger.result :as mgresult :refer [ok?]]
            [clojure.data.json :as json]
            )
  (:import [org.bson.types ObjectId])
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

(comment
  "Deck Model"
  "DB -> Usernames (collections)"
  "Username [{:deck-name \"deck1\"
:_id ObjectId
:cards [
{:paragraph \"some text\" :notes [{:japanese \"japanese word\" :reading \"reading\" :english \"english\"}]]"

  )


(comment "coll-name" [ {:all-decks [
                                  {:deck-name "deck-name" :_id ObjectId.}
                                  {:deck-name "deck-name" :_id ObjectId.}
                                  ]
                      :_id ObjectId.
                      }
                     ]

        "coll-name" [
                    {:deck-name "deck1"
                     :_id ObjectId.
                     :cards [
                             {:_id ObjectId.
                              :paragraph " some text"
                              :notes [
                                      {:japanese "japanese word"
                                       :reading "reading"
                                       :english "english"
                                       }
                                      {:japanese "japanese word"
                                       :reading "reading"
                                       :english "english"
                                       }
                                      ]
                              :audio-path "audio-file-name.mp3"
                              :font-color "#000000"
                              }
                             {:_id ObjectId.
                              :paragraph "some text"
                          :notes [
                                      {:japanese "japanese word"
                                       :reading "reading"
                                       :english "english"
                                       }
                                      {:japanese "japanese word"
                                       :reading "reading"
                                       :english "english"
                                       }
                                      ]
                              :audio-path "audio-file-name.mp3"
                              :font-color "#000000"}
                             ]
                     }
                    ])

;;;;;;;;;;;;;;
;;NEW USER-COLL
;;;;;;;;;;;;;;

(defn add-new-user-coll-name [user-coll-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (if (mgcoll/exists? user-coll-name)
    (println "Username already exists, unable to create")
    (mgcoll/create user-coll-name {:max 10000}))
  )

(defn add-empty-deck-list [user-coll-name]
  (mgcoll/insert user-coll-name {:_id (ObjectId.) :all-decks []})
  )

(defn new-user-coll-name [user-coll-name]
  ;(mgcore/connect!)
  ;(mgcore/set-db! (mgcore/get-db "card-db"))
  (add-new-user-coll-name user-coll-name)
  (add-empty-deck-list user-coll-name)
  )

;;;;;;;;;;;;;;;;;;;;;;
;;;;End New User-Coll
;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;
;;;;Add Delete Edit Deck
;;;;;;;;;;;;;;;;;;;;;


(defn add-deck-to-deck-list
  ""
  [user-coll-name deck-name]
  (let [deck-document (mgcoll/find-one-as-map user-coll-name {:all-decks {$exists true}})
        deck-document-id (deck-document :_id)
        deck-object-id (ObjectId.)
        deck-map {:deck-name deck-name :_id deck-object-id}
        ]
    (mgcoll/update user-coll-name {:_id deck-document-id} {$push {:all-decks deck-map}})
    )
  )

(defn add-empty-deck-to-user-coll-name
  "add empty deck to coll-name so we can $push to it's array later"
 [user-coll-name deck-name]
 (mgcoll/insert user-coll-name {:deck-name deck-name
                                :_id (ObjectId.)
                                :cards []
                                })
  )

(defn new-card-deck
  "the username is the collection name.  each user has a list of decks with a unique id (so that duplicate deck names don't cause a problem) this will both add a empty deck and a the deck-name to the deck-list to make it easier to list decks later"
; card-db - > username -> {:all-decks [{deck-name "deck1" deck-id "deck-id"}]
  [user-coll-name deck-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (add-deck-to-deck-list user-coll-name deck-name)
  (add-empty-deck-to-user-coll-name user-coll-name deck-name)
  )

(defn delete-deck
  "delete all deck data and reference to deck in all-decks
deck-id is a string"
  [user-coll-name deck-id]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [deck-document (mgcoll/find-one-as-map user-coll-name {:all-decks {$exists true}})
        deck-document-id (deck-document :_id)
        deck-id-to-delete (ObjectId. deck-id)
        ]
    (mgcoll/remove-by-id user-coll-name deck-id-to-delete)
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;End Add Delete Edit Deck
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;Add Edit Delete Cards in Deck
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn add-card-to-deck [user-coll-name deck-id card-map]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [deck-id-object (ObjectId. deck-id)
        deck-map-not-json (json/read-str card-map)
        card-id (ObjectId.)
        card-map-with-id (assoc deck-map-not-json :_id card-id)
        ]
    (if (mgresult/ok? (mgcoll/update-by-id user-coll-name deck-id-object {$push {:cards card-map-with-id}}))
      "ok"
      "fail"
      )))


(defn test-cardcreatoredb-add-card-to-deck []
  (let [card-map {
                  :paragraph "watashi blah blah"
                  :notes [
                          {:japanese "japanese word"
                           :reading "reading"
                           :english "english"
                           }
                          {:japanese "japanese word"
                           :reading "reading"
                           :english "english"
                           }
                          ]
                  :audio-path "audio-file-name.mp3"
                  :font-color "#000000"
                  }]
    (add-card-to-deck "jared" "5366348744aebe1b4f9d44aa" card-map))
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;End Add Edit Delete Cards
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;Views of Decks / User-Coll
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn user-coll-has-decks? [user-coll-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (if (mgcoll/exists? user-coll-name)
    true
    false
    )
  )

(defn display-all-decks-in-user-coll-name-by-id [user-coll-name])



(defn display-all-cards-in-deck
"takes user-coll-name as string and deck-id as a string"
  [user-coll-name deck-id]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [deck-id-object (ObjectId. deck-id)
        all-cards-in-deck (mgcoll/find-map-by-id user-coll-name deck-id-object)]
    ;(println (str "all-cards-in-deck" (json/read-str all-cards-in-deck)))
    all-cards-in-deck)
  )

(defn display-all-cards-in-deck-object-as-string
  [user-coll-name deck-id]
  (let [map-of-cards (display-all-cards-in-deck user-coll-name deck-id)
        map-of-cards-no-objects (map #(hash-map :_id (str (% :_id)) :paragraph (% :paragraph) :notes (% :notes) :font-color (% :font-color) :audio-path (% :audio-path)) (map-of-cards :cards))
        ]
    ;(println (str "map-of-cards-no-objects" map-of-cards-no-objects))
    map-of-cards-no-objects
    ;(json/write-str map-of-cards)
    )
  )


(defn display-all-decks-in-user-coll-with-id
  "display all users decks from deck list"
  [user-coll-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [deck-map (mgcoll/find-maps user-coll-name {:deck-name {$exists true}})]
    deck-map
    ;(hash-map :deck-name (deck-map :deck-name) :_id (deck-map :_id))
    (map #(hash-map :deck-name (% :deck-name) :_id (str (% :_id))) deck-map)
    )
  )

;good, but think i don't need a deck list
(comment (defn display-all-deck-names-from-deck-list [user-coll-name]
           (mgcore/connect!)
           (mgcore/set-db! (mgcore/get-db "card-db"))
           (let [deck-map (mgcoll/find-one-as-map user-coll-name {:all-decks {$exists true}})]
             (map #(% :deck-name) (deck-map :all-decks))
             )
           ))

;good, dont think i need a deck list
(comment (defn display-all-decks-that-really-exist [user-coll-name]
           (mgcore/connect!)
           (mgcore/set-db! (mgcore/get-db "card-db"))
           (map #(% :deck-name) (mgcoll/find-maps user-coll-name {:deck-name {$exists true}}))
           ))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;END View of Decks / User-coll
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;View Cards ;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn view-card-by-string-id [user-coll-name deck-id card-id]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [all-cards-map ((mgcoll/find-one-as-map user-coll-name {:_id (ObjectId. deck-id)}) :cards)
        ]
    (first (filter #(= (ObjectId. card-id) (% :_id)) all-cards-map))
    )
  )

(defn view-card-by-string-id-json [user-coll-name deck-id card-id]
  (println user-coll-name)
  (println deck-id)
  (println card-id)
  (let [one-card-map (view-card-by-string-id user-coll-name deck-id card-id)
        one-card-map-no-id (dissoc one-card-map :_id)
        one-card-map-object-string (assoc one-card-map :_id (str (one-card-map :_id)))
        ]
    (json/write-str one-card-map-object-string)
    )
  )

(defn test-view-card-by-string-id-json []
  (view-card-by-string-id-json "jared" "5366348744aebe1b4f9d44aa" "536cd30131da9361979ee760")
  )

(defn test-view-card-by-string-id []
  (view-card-by-string-id "jared" "5366348744aebe1b4f9d44aa" "536cd30131da9361979ee760")
  )


;;;;;;;;;;;;;;;;;;
;;;; DB Functions
;;;;;;;;;;;;;;;;;;;

(defn view-all-collections-in-db [db-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db db-name))
  (mgdb/get-collection-names)
  )


(defn view-all-dbes []
  (mgcore/connect!)
  (mgcore/get-db-names)
  )


;;;;;;;;;;;;;;;;;;;;;;;
;;;;;End DB Functions
;;;;;;;;;;;;;;;;;;;;;;
