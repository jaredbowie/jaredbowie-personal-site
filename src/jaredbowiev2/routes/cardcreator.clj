(ns jaredbowiev2.routes.cardcreator
  (:require [jaredbowiev2.views.layout :as layout]
            [hiccup.form :refer [form-to label text-field submit-button]]
            [compojure.core :refer [defroutes GET POST]]
            [noir.util.route :refer [def-restricted-routes]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.page :refer [include-js include-css]]
            [jaredbowiev2.models.cardcreator :as model-cc :refer [export-deck]]
            [jaredbowiev2.models.cardcreatoredb :as ccdb :refer [display-all-decks-in-user-coll-with-id display-all-cards-in-deck user-coll-has-decks? display-all-cards-in-deck-object-as-string view-card-by-string-id-json add-card-to-deck add-empty-deck-to-user-coll-name delete-deck]]
            [noir.session :as session]
            [hiccup.core :refer [html]]
            ))

(defn one-card-request [user-coll-name deckid card-id]
  (view-card-by-string-id-json user-coll-name deckid card-id)
  )

(defn one-deck-link [deck-map]
  (println (deck-map :deck-id))
  (let [deck-name (deck-map :deck-name)]
    (html
     [:div {:class "deck-name" :id (deck-map :_id) :deck-name deck-name} [:a {:href "#"} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "deck"] " " [:font {:class "string"} "\"" deck-name "\""] [:font {:class "para1"} ")"]]]
     ))
  )

(defn one-card-link [one-card-map deck-id]
  (html
   [:div {:class "card-name" :id (one-card-map :_id) :deck deck-id} [:a {:href "#"} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "card"] " " [:font {:class "string"} "\""(take 10 (one-card-map :paragraph)) "\""] [:font {:class "para1"} ")"]]])
  )

(defn card-links [user-coll-name deck-id]
  (let [coll-of-cards (display-all-cards-in-deck-object-as-string (session/get :user) deck-id)]
    (println (str "coll-of-cards" coll-of-cards))
    (apply str (map #(one-card-link % deck-id) coll-of-cards))
    )
  )

(def add-deck-html
  [:div {:class "add-deck"} [:a {:href "#"} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "Add Deck"] [:font {:class "para1"} ")"]]]
  )


(def add-card-html
  [:div {:class "add-card"} [:a {:href "#"} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "Add Card"] [:font {:class "para1"} ")"]]]
  )

(defn deck-return [username-logged-in]
  (if (user-coll-has-decks? username-logged-in)
    (let [all-decks (display-all-decks-in-user-coll-with-id username-logged-in)]
      (apply str (map #(one-deck-link %) all-decks))
      )
    ""))

(defn card-creator []
  (let [username-logged-in (session/get :user)
        decks (deck-return username-logged-in)
        ]
    (layout/common
     [:div {:id "card-creator"}
      (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js")
      (include-js "/js/cardcreator.js")
      (include-css "/css/cardcreator.css")
      [:div {:id "deck-list"} add-deck-html [:font {:class "string"} (str decks)]]
      [:div {:class "deck-card-break"} [:font {:class "emacsfooter"} "|"]]
      [:div {:id "cards-list"} add-card-html [:font {:class "string" :id "font-cards-list"}]]
      [:div {:class "one-input" :id "one-card-id"}
       [:div {:class "left-label"} [:font {:class "string"} "\"Card\" "]]
       [:div {:class "right-label"} [:font {:class "string"} [:div {:id "card-name-area-input"}]]]
       ]
      [:div {:class "one-input"}
       [:div {:class "left-label"} [:font {:class "string"} "\"Deck\" "]]
       [:div {:class "right-label"} [:font {:class "string"} [:div {:id "deck-name-area-input"}]]]]
      [:div {:class "spacer"}]
      [:div {:class "one-input"}
       [:div {:class "left-label"} [:font {:class "string"} "\"Text\" "]]
       [:div {:class "right-label"} ;[:textarea {:name "text-chunk" :id "paragraph"}]
        [:div {:name "paragraph" :id "paragraph" :contenteditable "true"} [:div {:class "focus-word"} ""]]]]
      [:div {:class "spacer"}]
      [:div {:class "notes-input" :id "notes-section"}
       [:div {:class "left-label"}
        [:button {:class "add-rem-button" :id "add-notes-button" :type "button"} "Add One Note"]
        ]
       [:div {:class "right-label"} ""]]
      [:div {:class "spacer"}]
      [:div {:class "one-input"}
       [:div {:class "left-label"} [:font {:class "string"} "\"Audio Path\" "]]
       [:div {:class "right-label"} [:textarea {:name "audio-path" :id "audio-path"}]]]
      [:div {:class "spacer"}]
      [:div {:class "one-input"}
       [:div {:class "left-label"} [:font {:class "string"} "\"Font Color\" "]]
       [:div {:class "right-label"} [:textarea {:name "font-color" :id "font-color"} "#0000ff"]]]
      [:div {:class "spacer"}]
      [:div {:class "one-input"}
       [:div {:class "left-label"} [:font {:class "string"} "\"Submit Button\" "]]
       [:div {:class "right-label"}
        [:button {:class "submit-reset-button" :id "submit-button" :type "button"} "Save Card"]
        [:button {:class "submit-reset-button" :id "reset-button" :type "button"} "Reset Card"]
        [:button {:class "submit-reset-button" :id "export-button" :type "button"} "Export Deck"]
        ]
       ]
      ]
     "")))

(def-restricted-routes card-creator-routes
  (GET "/card-creator" [] (card-creator))
  (POST "/card-creator/save-card" [cardid deckid onecardmap] (ccdb/add-or-edit-card cardid (session/get :user) deckid onecardmap))
  (GET "/card-creator/return-cards" [deckid] (card-links (session/get :user) deckid))
  (GET "/card-creator/return-one-card" [deckid cardid] (one-card-request (session/get :user) deckid cardid))
  (POST "/card-creator/new-deck" [deckname] (ccdb/add-empty-deck-to-user-coll-name "jared" deckname))
  (POST "/card-creator/delete-deck" [deckid] (ccdb/delete-deck (session/get :user) deckid))
  (GET "/card-creator/get-deck-tsv" [deckid] (model-cc/export-deck (session/get :user) deckid))
  )
