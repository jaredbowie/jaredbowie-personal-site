(ns jaredbowiev2.views.blog
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.core :refer [html]]
            [noir.session :as session]
            [jaredbowiev2.views.layout :as layout]
            ;[jaredbowiev2.views.layout :as layout :refer [blog-nav]]
            )
   )

(defn one-blog-post [blog-post-title blog-post-date blog-post-author blog-post-content]
  (html [:div {:class "blog-post"}
         [:div {:class "blogleft"}
          [:p {:class "blog-post-meta"} [:font {:class "stringdate"} blog-post-date] [:font {:class "string"} " by "  blog-post-author]]]
         [:div {:class "blogright"}
          [:h2 {:class "blog-post-title"} [:font {:class "string"} blog-post-title]]
          [:p {:class "blog-post-content"} [:font {:class "string"} blog-post-content]]
          ]
         ]
        ))

(defn all-blog-posts [maps-of-posts]
  (apply str (for [one-map maps-of-posts]
               (one-blog-post (one-map :post-title) (one-map :post-date) (one-map :post-author) (one-map :post-content))))
  )


(defn blog [page-number number-of-posts-to-display maps-of-posts]
  ;(layout/blog-nav)
  (layout/common (all-blog-posts maps-of-posts)
   "/css/blog.css"
   )
  )
