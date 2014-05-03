(ns jaredbowiev2.models.blog
  (:require
   [taoensso.carmine :as car :refer (wcar)]
   [clj-time.core :as timecore :refer [now]]
   [clj-time.coerce :as timecoerce :refer [to-long from-long]]
   [clj-time.format :as timeformat :refer [show-formatters formatter unparse]]
   [clojure.data.json :as json :refer [write-str]]
   )
  )

(def server1-conn {:pool {} :spec {:host "127.0.0.1" :port 6379}}) ; See `wcar` docstring for opts
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

                                        ;add post
                                        ;edit post
                                        ;remove post
                                        ;a view to deal with it all
                                        ;login logout


(defn get-all-posts
 "Get all blog posts"
  []
  (wcar* (car/select "3"))
  (wcar* (car/keys "*"))
  )

(defn get-post
  "Get a single blog post"
  [post-name]
  (wcar* (car/select "3"))
  (wcar* (car/get post-name))
  )

(defn get-post-title
  "Get the title of a single blog post"
  [post-name]
  ((get-post post-name) :post-title)
  )

(defn get-post-content
  "Get the post-content of a single blog post aka the actual post.  post-name is a longdate"
  [post-name]
  ((get-post post-name) :post-content)
  )

(defn get-post-author
  "Get the post author of a single blog post"
  [post-name]
  ((get-post post-name) :post-author)
  )

(defn add-post [title content-string author]
  (let [time-now (timecore/now)
        time-long (timecoerce/to-long time-now)
        ]
    (wcar* (car/select "3"))
    (wcar* (car/set time-long {:post-title title :post-content content-string :post-author author}))
    (str time-long " : " (wcar* (car/get time-long)))
    )
  )

(defn edit-post [post-name post-new-content])

(defn delete-post [post-name]
  (wcar* (car/select "3"))
  (wcar* (car/del post-name))
  (println (str post-name " deleted"))
  )

(defn delete-all-posts []
  (let [all-keys (get-all-posts)]
    (wcar* (car/select "3"))
    (map #(wcar* (car/del %)) all-keys)
    )
  )

(defn long-to-date [long-date]
  (let [custom-formatter (timeformat/formatter "MMMM-dd-yyyy")
        long-date-to-date (timecoerce/from-long long-date)
        ]
    (timeformat/unparse custom-formatter long-date-to-date)
    )
  )

(defn post-count []
  (count (get-all-posts))
  )

                                        ;example
                                        ;starting-x 0 aka starting page
                                        ;amount 10
                                        ; total posts are 100
                                        ;so we'd return the last 10 posts

(defn give-me-x-posts [starting-x amount]
  (let [all-post-titles (reverse (sort (get-all-posts)))
        starting-post (* starting-x 10)
        drop-the-other-posts (drop starting-post all-post-titles)
        then-take-the-amount (take amount drop-the-other-posts)
        ]
    (map #(hash-map :post-date (str (long-to-date (read-string %)) " " %) :post-title (get-post-title %) :post-content (get-post-content %) :post-author (get-post-author %)) then-take-the-amount)
    )
  )

(comment (defn give-me-x-posts [starting-x amount]
           (let [all-post-titles (reverse (sort (get-all-posts)))
                 starting-post (* starting-x 10)
                 drop-the-other-posts (drop starting-post all-post-titles)
                 then-take-the-amount (take amount drop-the-other-posts)
                 ]
             (map #(hash-map :longdate % :date (long-to-date (read-string %)) :post (get-post %)) then-take-the-amount)
             )
           ))


(defn get-title-and-post-json [long-title]
  (let [map-of-title-post {:post-title (get-post-title long-title) :post-content (get-post-content long-title)}
        json-string (json/write-str map-of-title-post)]
    (println json-string)
    json-string
    )
  )
