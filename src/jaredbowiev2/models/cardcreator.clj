(ns jaredbowiev2.models.cardcreator
  (:require [clj-time.core :as timecore]
            [clj-time.coerce :as timecoerce]
            [clojure.data.json :as json]
            [jaredbowiev2.models.cardcreatoredb :as ccdb :refer [display-all-cards-in-deck]]
            )
  )

;make-card (takes one text paragrap, one note paragraph, one audio path)
                                        ;-make card maps of all the cards
                                        ;--for each line of notes find the word in paragraph and highlight it
                                        ;--make that one line the top of notes

;-make sure every note line exists, otherwise fail

;add-card-maps-to-db (takes one more more maps of cards)
                                        ;-add cards to database

                                        ;export-db-of-cards
                                        ;-export all cards to a TSV


(defn export-deck [user-coll-name deckid]
                                        ;get deck from db
                                        ;process deck
  (println (ccdb/display-all-cards-in-deck user-coll-name deckid))
  (ccdb/display-all-cards-in-deck user-coll-name deckid)

  )

(defn- one-map-to-tsv
  [one-map]
  (let [char-seperator "\t"]
    (str (one-map :paragraph) char-seperator (one-map :reading-paragraph) char-seperator (one-map :notes) char-seperator (one-map :audio) "\r"))
  )

#_(comment (defn all-maps-to-tsv [db-name coll-name]
           (let [all-cards (view-all-cards-in-db-coll db-name coll-name)]
             (spit "tempcards.txt" (apply str (map #(one-map-to-tsv %) all-cards)))
             (apply str (map #(one-map-to-tsv %) all-cards))
             )
           ))

(defn- how-things-should-look []
  (slurp "/home/jared/clojureprojects/jaredbowie/jpdpartial")
 )

(defn- highlight-word
  "takes a word and a highlighting color, then bolds it and colors it
highlighting must be a string like \"#0000ff\""
  [word highlighting]
  (str "<font color=\"" highlighting "\"><b>" word "</b></font>")
)

(defn- make-reading-paragraph [paragraph all-notes]
  (loop [new-paragraph paragraph
         note-collection all-notes]
    (if (not (empty? note-collection))
      (do
        (let [the-word ((first note-collection) :word)
              the-reading ((first note-collection) :reading)
              to-replace (if (not (nil? the-reading))
                           (str the-word "[" the-reading "]")
                           the-word
                           )
              ]
          (recur (clojure.string/replace new-paragraph (re-pattern the-word) to-replace) (drop 1 note-collection))))
        new-paragraph
      )
    ;new-paragraph
    )
  )

(defn- make-one-note-line
  "returns a string of one line.  inserts furigana if it exists."
  [word translation reading]
  (if (nil? reading)
                        (str word "=" translation)
                        (str word "[" reading "]" "=" translation)))

(defn- all-notes-except-one [all-notes one-note]
  (let [without-note (filter #(not= one-note %) all-notes)]
    (apply str (map #(str (make-one-note-line (% :word) (% :translation) (% :reading)) "<br>") without-note))))


(defn- make-one-card-map [paragraph reading-paragraph highlighting-color one-note audio-path all-notes]
  (let [one-note-line (make-one-note-line (one-note :word) (one-note :translation) (one-note :reading)) ]
    (hash-map :paragraph
              (clojure.string/replace paragraph (re-pattern (one-note :word)) (highlight-word (one-note :word) highlighting-color))
              :reading-paragraph reading-paragraph
              :notes (str (highlight-word one-note-line highlighting-color) "<p>"
                          (all-notes-except-one all-notes one-note))
              :audio audio-path))
  )



(defn- test-make-reading-paragraph []
  (make-reading-paragraph "よし: 今日何しますか。\n\nたけ: 今日？天気を見てください！いよいよ夏が来ました。晴れで、暑くて、夏本番ですよ！今日は海に行きます。\n\nよし: 海ですか。あまり行きたくないです。"
                        [{:word "天気を見てください" :reading nil :translation "Please look at the weather."}
         {:word "いよいよ" :reading nil :translation "Finally, more and more"}
         {:word "晴れ" :reading "はれ" :translation "clear weather"}
         {:word "夏本番" :reading "なつほんばん" :translation "midsummer; height of summer"}
         {:word "あまり" :reading nil :translation "not much"}]
                          )
  )
