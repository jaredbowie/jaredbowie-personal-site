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


(defn add-deck-to-deck-list [user-coll-name deck-name]
  (mgcoll/insert user-coll-name {:deck-name deck-name
                                 :_id (ObjectId.)
                                 :cards []
                                 })
  )

(defn add-empty-deck-to-user-coll-name
  "add empty deck to coll-name so we can $push to it's array later"
  [user-coll-name deck-name]
  (let [deck-document (mgcoll/find-one-as-map user-coll-name {:all-decks {$exists true}})
        deck-document-id (deck-document :_id)
        deck-object-id (ObjectId.)
        deck-map {:deck-name deck-name :_id deck-object-id}
        ]
    (mgcoll/update user-coll-name {:_id deck-document-id} {$push {:all-decks deck-map}})
    )
  )

(defn new-card-deck
  "the username is the collection name.  each user has a list of decks with a unique id (so that duplicate deck names don't cause a problem"
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
    (mgcoll/update-by-id user-coll-name deck-document-id {$pull {:all-decks {:deck-id deck-id-to-delete}}})
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
  (let [deck-id-object (ObjectId. deck-id)]
    (mgcoll/update-by-id user-coll-name deck-id-object {$push {}}))

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


(defn display-all-cards-in-deck [user-coll-name deck-id]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [])
  (mgcoll/find-map-by-id user-coll-name deck-id)
  )

(defn display-all-decks-in-user-coll [user-coll-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (let [deck-map (mgcoll/find-one-as-map user-coll-name {:all-decks {$exists true}})]
    (deck-map :all-decks)
    )
  )

;good
(defn display-all-deck-names-from-deck-list [user-coll-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
   (let [deck-map (mgcoll/find-one-as-map user-coll-name {:all-decks {$exists true}})]
     (map #(% :deck-name) (deck-map :all-decks))
    )
  )

;good
(defn display-all-decks-that-really-exist [user-coll-name]
  (mgcore/connect!)
  (mgcore/set-db! (mgcore/get-db "card-db"))
  (map #(% :deck-name) (mgcoll/find-maps user-coll-name {:deck-name {$exists true}}))

  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;END View of Decks / User-coll
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



;{objectid set-name paragraph notes audio highlighing-color}


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
