(ns jaredbowiev2.routes.home
  (:require [compojure.core :refer :all]
            [jaredbowiev2.views.layout :as layout]))

(defn home []
  (let [home-page-text "\"blah\""]
    (layout/common [:div {:align "center"} [:iframe {:width "560" :height "315" :src "//www.youtube.com/embed/i6bNN4ENJIQ" :frameborder "0" :allowfullscreen ""}]] "" ) ))

;<iframe width="560" heiyght="315" src="//www.youtube.com/embed/LUOIvT9hzD8" frameborder="0" allowfullscreen></iframe>

(defroutes home-routes
  (GET "/" [] (home)))
