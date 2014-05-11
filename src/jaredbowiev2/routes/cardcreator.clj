(ns jaredbowiev2.routes.cardcreator
  (:require [jaredbowiev2.views.layout :as layout]
            [hiccup.form :refer [form-to label text-field submit-button]]
            [compojure.core :refer [defroutes GET POST]]
            [noir.util.route :refer [def-restricted-routes]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.page :refer [include-js include-css]]
            [jaredbowiev2.models.cardcreator :as model-cc :refer [receive-card-from-post]]
            [jaredbowiev2.models.cardcreatoredb :refer [display-all-decks-in-user-coll-with-id display-all-cards-in-deck user-coll-has-decks? display-all-cards-in-deck-object-as-string view-card-by-string-id-json]]
            [noir.session :as session]
            [hiccup.core :refer [html]]
            ))

(defn one-card-request [user-coll-name deckid card-id]
  (view-card-by-string-id-json user-coll-name deckid card-id)
  )

(defn one-deck-link [deck-map]
  (println (deck-map :deck-id))
  (html
   [:div {:class "deck-name" :id (deck-map :_id)} [:a {:href "#"} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "link"] " " [:font {:class "string"} (deck-map :deck-name)] [:font {:class "para1"} ")"]]]
   )
  )

(defn one-card-link [one-card-map deck-id]
  (html
   [:div {:class "card-name" :id (one-card-map :_id) :deck deck-id} [:a {:href "#"} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "link"] " " [:font {:class "string"} (take 10 (one-card-map :paragraph))] [:font {:class "para1"} ")"]]])
  )

(defn card-links [user-coll-name deck-id]
  (let [coll-of-cards (display-all-cards-in-deck-object-as-string (session/get :user) deck-id)]
    (println coll-of-cards)
    (apply str (map #(one-card-link % deck-id) coll-of-cards))
    )
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
      [:div {:id "deck-list"} [:font {:class "string"} (str decks)]]
      [:div {:id "cards-list"} [:font {:class "string" :id "font-cards-list"}]]
      [:div {:class "one-input"}
       [:div {:class "left-label"} [:font {:class "string"} "\"Text\" "]]
       [:div {:class "right-label"} [:textarea {:name "text-chunk" :id "paragraph"}]]]
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
        [:button {:class "submit-reset-button" :id "reset-button" :type "button"} "Reset Card"]]
       ]
      ]
     "")))

(def-restricted-routes card-creator-routes
  (GET "/card-creator" [] (card-creator))
  (POST "/card-creator" [onecard] (receive-card-from-post onecard))
  (GET "/card-creator/return-cards" [deckid] (card-links (session/get :user) deckid))
  (GET "/card-creator/return-one-card" [deckid cardid] (one-card-request (session/get :user) deckid cardid))
)
