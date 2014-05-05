(ns jaredbowiev2.handler
  (:require [compojure.core :refer [defroutes]]
            [jaredbowiev2.routes.home :refer [home-routes]]
            [jaredbowiev2.middleware :as middleware]
            [noir.util.middleware :refer [app-handler]]
            [noir.util.route :refer [restricted]]
            [noir.session :as session]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [jaredbowiev2.models.blog :as blog-models :refer [post-count]]
            [jaredbowiev2.routes.blog :refer [blog-routes]]
            [jaredbowiev2.routes.auth :refer [auth-routes]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.params :refer [wrap-params]]
            [jaredbowiev2.routes.blogadmin :refer [blogadmin-routes]]
            [jaredbowiev2.routes.cardcreator :refer [card-creator-routes]]
            ))

(defn user-access [request]
  (session/get :user))

(defn jared-access [request]
  (= (session/get :user) "jared")
  )

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "jaredbowiev2.log" :max-size (* 512 1024) :backlog 10})

  (if (env :dev) (parser/cache-off!))
  (timbre/info "jaredbowiev2 started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "jaredbowiev2 is shutting down..."))


(def app (app-handler
           ;; add your application routes here
          [card-creator-routes
           blogadmin-routes
           home-routes
           auth-routes
           blog-routes
          ; (other-blog-routes (blog-models/post-count) 10)
           app-routes]
           ;; add custom middleware here
          :middleware [;wrap-params
                       middleware/template-error-page
                       middleware/log-request]
          :session-options {:cookie-name "example-app-session"
                      :store (cookie-store)}
           ;; add access rules here
          :access-rules [{:uri "/card-creator/*"
                          :rule user-access}
                         {:uri "/add-post"
                          :rule jared-access}]
           ;; serialize/deserialize the following data formats
           ;; available formats:
           ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
           :formats [:json-kw :edn]))
