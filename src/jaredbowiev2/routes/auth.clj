(ns jaredbowiev2.routes.auth
  (:require [compojure.core :refer :all]
            [jaredbowiev2.views.layout :as layout]
            [noir.session :as session]
            [hiccup.form :refer [form-to label text-field password-field submit-button]]
            [hiccup.page :refer [html5 include-css] ]
            [compojure.core :refer [defroutes GET POST]]
            [noir.response :refer [redirect]]
            [noir.validation :refer [rule errors? has-value? on-error]]
            [noir.util.crypt :as crypt]
            [jaredbowiev2.models.auth :as authmodels]
            ))

(defn format-error [[error]]
  [:p.error error])

(defn control [field name text]
  (list (on-error name format-error)
        (label name text)
        (field name)
        [:br]))

(defn login-page []
  (layout/common
   (form-to [:post "/login"]
            [:div {:class "left"}
             [:font {:class "string"}
              (control text-field :id "\"Username\"")
              (control password-field :pass "\"Password\"")
              (submit-button "login")]])
   ""))

(defn registration-page []
  (layout/common
   (form-to
    [:post "/register"]
    [:font {:class "string"}
     (control text-field :id "\"Username\"")
     (control password-field :pass "\"Password\"")
     (control password-field :pass1 "\"Password\"")]
    [:button {:type "submit"} [:font {:class "stringdate"} "click here"]]
    )
   "/css/forms.css"
   )
  )

(defn handle-registration [username pass pass1]
  (rule (false? (authmodels/is-username? username))
        [:id "invalid username"]
        )
  (rule (= pass pass1)
        [:pass "password was not retyped correctly"])
  (if (errors? :id :pass)
    (registration-page)
    (do
      (authmodels/add-user username (crypt/encrypt pass))
      (redirect "/login")))
  )

(defn handle-login [id pass]
  (rule (has-value? id)
        [:id "\"Username Required\""])
  (rule (authmodels/is-username? id)
        [:id "\"Unknown User\""])
  (rule (has-value? pass)
        [:pass "\"Password Required\""])
  (rule (authmodels/valid-password id pass)
        [:pass "\"Invalid Password\""])
  (if (errors? :id :pass)
    (login-page)
    (do
      (println id)
      (session/put! :user id)
      (redirect "/add-post"))))

(defroutes auth-routes
 (GET "/register" [] (registration-page))
(POST "/register" [id pass pass1]
(handle-registration id pass pass1))
  (GET "/login" [] (login-page))
  (POST "/login" [id pass]
        (doall (handle-login id pass)))
  (GET "/logout" []
       (layout/common
        (form-to [:post "/logout"]
                 (submit-button "logout")) ""))
  (POST "/logout" []
        (session/clear!)
        (redirect "/"))












  )
