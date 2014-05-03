(ns jaredbowiev2.routes.blog
  (:require [compojure.core :refer :all]
            [jaredbowiev2.views.blog :as blog-layout]
            [hiccup.page :refer [include-css]]
            [jaredbowiev2.models.blog :as blog-models :refer [get-all-posts get-post post-count give-me-x-posts]]))


(defn blog [page-number number-of-posts-to-display]
  (let [posts-data-map (vec (give-me-x-posts page-number number-of-posts-to-display))]
    (blog-layout/blog page-number number-of-posts-to-display posts-data-map))
  )


(defroutes blog-routes
  (GET "/blog/:pagenumber" [pagenumber] (blog (read-string pagenumber) 10))
  )
