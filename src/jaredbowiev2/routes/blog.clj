(ns jaredbowiev2.routes.blog
  (:require [compojure.core :refer :all]
            [jaredbowiev2.views.blog :as blog-layout]
            [hiccup.page :refer [include-css]]
            [jaredbowiev2.models.blog :as blog-models :refer [get-all-posts get-post post-count give-me-x-posts]]))


(defn blog [page-number number-of-posts-to-display]
  (let [posts-data-map (vec (give-me-x-posts page-number number-of-posts-to-display))]
    (blog-layout/blog page-number number-of-posts-to-display posts-data-map))
  )

(defn other-blog-routes
"post-count being the total amount of posts to display"
  [post-count number-of-posts-to-display]
  (let [pages (/ post-count 10)
        blog-page-numbers (range pages)]
    (apply routes (map #(GET (str "/blog/" %) [] (blog % number-of-posts-to-display)) blog-page-numbers))
    ))

(comment (defn one-post-html-hiccup [date-string date-long post-content page-number]
           [:div [:div {:class "left"} [:font {:class "stringdate"} (str date-string "&nbsp;")] [:font {:class "longdate"} (str ";" date-long)]]
            [:div {:class "right"} [:font {:class "string"} post-content (str page-number)]]]
           ))

(comment (defn blog [page-number number-of-posts-to-display]
           (let [posts-data-map (give-me-x-posts page-number number-of-posts-to-display)]
             (layout/common [:div
                             (include-css "/css/blog.css")
                             (map #(one-post-html-hiccup (% :date) (% :longdate) (% :post) page-number) posts-data-map)]))))
