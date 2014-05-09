(ns jaredbowiev2.routes.cardcreator
  (:require [jaredbowiev2.views.layout :as layout]
            [hiccup.form :refer [form-to label text-field submit-button]]
            [compojure.core :refer [defroutes GET POST]]
            [noir.util.route :refer [def-restricted-routes]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.page :refer [include-js include-css]]
            [jaredbowiev2.models.cardcreator :refer [receive-card-from-post]]
            [jaredbowiev2.models.cardcreatoredb :refer [display-all-decks-in-user-coll display-all-cards-in-deck user-coll-has-decks?]]
            [noir.session :as session]
            [hiccup.core :refer [html]]
            ))

(defn deck-links [deck-map]
  (println (deck-map :deck-id))
  (html
   [:div {:class "deck-name" :id (deck-map :_id)} [:a {:href "#"} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "link"] " " [:font {:class "string"} (deck-map :deck-name)] [:font {:class "para1"} ")"]]]
   )
  )

(defn deck-return [username-logged-in]
  (if (user-coll-has-decks? username-logged-in)
    (let [all-decks (display-all-decks-in-user-coll username-logged-in)]
      (apply str (map #(deck-links %) all-decks))
      )
    ""
    )
  )

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
      [:form
       {:post "/card-creator"}
       [:div {:class "one-input"}
        [:div {:class "left-label"} [:font {:class "string"} "\"Text\" "]]
        [:div {:class "right-label"} [:textarea {:name "text-chunk" :id "paragraph"}]]]
       [:div {:class "spacer"}]
       [:div {:class "one-input" :id "notes-section"}
        [:div {:class "left-label"} [:button {:id "add-notes-button" :type "button"} "Add notes"]]
        [:div {:class "right-label"} "aaaaaaaaaaaaa"]]
       [:div {:class "spacer"}]
       [:div {:class "one-input"}
        [:div {:class "left-label"} [:font {:class "string"} "\"Audio Path\" "]]
        [:div {:class "right-label"} [:textarea {:name "audio-path" :id "audio-path"}]]]
       [:div {:class "spacer"}]
       [:div {:class "one-input"}
        [:div {:class "left-label"} [:font {:class "string"} "\"Submit Button\" "]]
        [:div {:class "right-label"} [:input {:id "submit-button" :type "button" :value "add card"}]]
        ]
       ]
      ]
     "")))


(def-restricted-routes card-creator-routes
  (GET "/card-creator" [] (card-creator))
  (POST "/card-creator" [onecard] (receive-card-from-post onecard))
  (POST "/card-creator/return-cards" [userid deckid] (display-all-cards-in-deck userid (session/get :user)))
)
