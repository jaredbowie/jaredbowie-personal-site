(defproject jaredbowiev2 "0.1.7"
  :description "Jared Bowie's Personal site"
  :url "http://jaredbowie.com/"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [lib-noir "0.8.1"]
                 [compojure "1.1.6"]
                 [ring-server "0.3.1"]
                 [selmer "0.6.5"]
                 [com.taoensso/timbre "3.1.6"]
                 [com.taoensso/tower "2.0.2"]
                 [markdown-clj "0.9.41"]
                 [environ "0.4.0"]
                 [com.taoensso/carmine "2.6.0"]
                 [clj-time "0.6.0"]
                 [com.novemberain/monger "1.7.0"]
                 [hiccup "1.0.5"]
                 [hiccup-bootstrap "0.1.2"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/data.json "0.2.4"]
                 ]

  :repl-options {:init-ns jaredbowiev2.repl}
  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.4.0"]]
  :ring {:handler jaredbowiev2.handler/app
         :init    jaredbowiev2.handler/init
         :auto-reload? true
         :auto-refresh? true
         :destroy jaredbowiev2.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.2.2"]]
         :env {:dev true}}}
  :min-lein-version "2.0.0")
