(ns jaredbowiev2.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.core :refer [html]]
            [noir.session :as session]))

(def domain "http://jaredbowie.com/")
(def domainhttps "https://jaredbowie.com/")
;(def domain "http://localhost:3000/")
;(def domainhttps "http://localhost:3000/")

(defn convert-to-html [link text]
  (html [:div {:align "left"} [:a {:href link} [:font {:class "para1"} "("] [:font {:class "functionbuiltin"} "link"] " " [:font {:class "string"} text] [:font {:class "para1"} ")"]]]))


(defn login-or-username
  "returns a login html button if user is not logged in and a username if the user is logged in"
  []
  (let [session-key (session/get :user)]
    (if session-key
      [:font {:class "string"} session-key]
      (convert-to-html (str domainhttps "login") "\"login\"")
      )
    )
  )

(defn header-links
  []
  [ ;{:text "\"articles\"" :link "http://jaredbowie.com/articles"}
   {:text "\"blog\"" :link (str domain "blog/0")}
   {:text "\"github\"" :link "https://github.com/jaredbowie"}
   {:text "\"home\"" :link domain}
  ; {:text "\"blog admin\"" :link (str domain "add-post")}
   {:text "\"card creator" :link (str domain "card-creator")}
                                        ;{:text "\"live projects\"" :link "http://jaredbowie.com/live-projects"}
                                        ;{:text "\"music\"" :link "http://jaredbowie.com/music"}
   ])


(defn header-links-html []
  (apply str (map #(convert-to-html (% :link) (% :text)) (header-links)))
  )

(defn common [body css-link
              ]
  (html5 [:head
     [:title "Welcome to jaredbowie"]
     (include-css "/css/screen.css")]
    [:body
     [:div {:id "wrapper"}
      [:div {:id "header"} [:div {:id "linkscontainer"} (header-links-html) (login-or-username)]]
      [:div {:id "content"} (include-css css-link) body]
      [:div {:id "footer"}]]
     ]))



(comment (defn blog-nav []
           (html [:nav {:class "blog-nav col-md-8"}
                  [:ul
                   [:li [:a {:class "blog-nav-item active" :href "#"} "Home"]]
                                        ; [:br]
                   [:li [:a {:class "blog-nav-item" :href "#"} "New features"]]
                                        ; [:br]
                   [:li [:a {:class "blog-nav-item" :href "#"} "Press"]]
                                        ; [:br]
                   [:li [:a {:class "blog-nav-item" :href "#"} "New Hires"]]
                                        ; [:br]
                   [:li [:a {:class "blog-nav-item" :href "#"} "about 1111111111111111111111111111111111111111"]]

                   [:li [:a {:class "blog-nav-item" :href "#"} "About"]]]

                  ])
           ))
