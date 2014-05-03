(ns jaredbowiev2.routes.cardcreator
  (:require [jaredbowiev2.views.layout :as layout]
            [hiccup.form :refer [form-to label text-field submit-button]]
            [compojure.core :refer [defroutes GET POST]]
            [hiccup.element :refer [javascript-tag]]
            [hiccup.page :refer [include-js include-css]]
            ))

(defn card-creator []
  (layout/common
   [:div {:id "card-creator"}
    (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js")
    (include-js "/js/cardcreator.js")
    (include-css "/css/cardcreator.css")
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
   ""))

(defn card-creator2 [themap]
  (println themap)
  (layout/common
   [:div {:id "card-creator"}
    [:p (str themap)]
    (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js")
    (include-js "/js/cardcreator.js")
    (include-css "/css/cardcreator.css")
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
   ""))

(comment (defn card-creator []
           (layout/common
            [:div
             (include-js "/js/cardcreator.js")
             [:input {:type "text" :id "member" :name "member" :value ""} "Number of members"]
             [:br]
             [:a {:href "#" :id "filldetails" :onclick "addFields()"} "fill details"]
             [:div {:id "container"}]]
            ""
            )
           ))


(defroutes card-creator-routes
  (GET "/card-creator" [] (card-creator))
  (POST "/card-creator" [data] (card-creator2 data))
)
