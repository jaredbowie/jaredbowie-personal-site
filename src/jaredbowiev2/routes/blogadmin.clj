(ns jaredbowiev2.routes.blogadmin
  (:require [compojure.core :refer :all]
            ;[jaredbowiev2.models.blog :refer [add-post]]
            [jaredbowiev2.views.layout :as layout]
            [jaredbowiev2.models.blog :as blog-models]
            [hiccup.form :refer [form-to label text-field submit-button]]
            [hiccup.page :refer [include-js]]
            [compojure.core :refer [defroutes GET POST]]
            [noir.util.route :refer [def-restricted-routes]] ))

(defn add-post-html []
  (layout/common
   [:div
    (include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js")
    (include-js "/js/blogadmin.js")
    [:div
     [:form
      {:id "blogpost"}
      [:label {:form "blogpost"} [:font {:class "string"} "\"New Post?\""]]
      [:input {:id "new-post-check" :type "checkbox" :checked "checked"}]
      [:p]
      [:label {:form "blogpost"} [:font {:class "string"} "\"Title\""]]
      [:textarea {:id "blog-title" :rows "3" :cols "20"}]
      [:p]
      [:label {:form "blogpost"} [:font {:class "string"} "\"Post\""]]
      [:textarea {:id "blog-content" :rows "10" :cols "50"}]
      [:button {:id "save-post-button" :type "button"} "save post"]
      ]]
    [:div {:id "edit-blog-post"}
     [:form
      [:label [:font {:class "string"} "\"Enter Long Data to Edit\""]]
      [:textarea {:id "long-data-to-edit" :width "300"}]
      [:button {:type "button" :id "data-edit-button"} "Load Post"]
      ]
     ]]


   ""
   )
  )

(def-restricted-routes blogadmin-routes
  (GET "/add-post" [] (add-post-html))
  ;(POST "/add-post" {params :params} (prn "params:" params))
  (POST "/add-post" [blogpost] (blog-models/add-post-direct blogpost "jared"))
  (GET "/edit-post" {params :params} (blog-models/get-title-and-post-json (params :longdate)))
  )
