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


(declare export-deck)
(declare one-map-to-tsv)
(declare make-one-note-line)
(declare make-notes-string)

(defn- make-one-note-line
  "takes one note map and produces string for notes section
if highlight-boolean true make the word the font-color"
  [one-note highlight-boolean font-color]
  (let [japanese-pre (one-note :japanese)
        japanese (if (true? highlight-boolean)
                   (str "<font color=\"" font-color "\">" japanese-pre "</font>")
                   japanese-pre
                   )
        reading-pre (one-note :reading)
        reading (if (not= "" reading-pre)
                  (str "[" reading-pre "]")
                  reading-pre
                  )
        english (one-note :english)]
    (str "<div>" japanese reading "=" english "</div>")
    )
  )

(defn- make-notes-string
  "takes vector of note maps and one notes line to highlight
produces string for notes section"
  [all-card-notes to-highlight-note font-color]
  (let [all-notes-except-highlighted (apply str (for [one-note all-card-notes]
                                                  (if (not= to-highlight-note one-note)
                                                    (make-one-note-line one-note false font-color))))
        highlighted-note-line (make-one-note-line to-highlight-note true font-color)
        ]
    (str highlighted-note-line all-notes-except-highlighted)
    ))

(defn- highlight-word-in-paragraph
  "take a word and replace it in a paragraph with a colored word depending on font-color"
  [paragraph word-to-highlight font-color]
  (let [front-font (str "<font color=\"" font-color "\">")
        back-font "</font>"
        replacement-word-highlighted (str front-font word-to-highlight back-font)
        ]
    (clojure.string/replace paragraph (re-pattern word-to-highlight) replacement-word-highlighted)
    )
  )

(defn- one-map-to-tsv
  "takes entire deck map and returns tsv of cards"
  [map-of-deck]
  (let [all-cards (map-of-deck :cards)]
    (for [one-card all-cards]
      (let [all-notes-one-card (one-card :notes)
            font-color (one-card :font-color)
            ]
        (for [one-note all-notes-one-card]
          (let [paragraph (highlight-word-in-paragraph (one-card :paragraph) (one-note :japanese) font-color)
                audio-path (one-card :audio-path)
                notes (make-notes-string all-notes-one-card one-note font-color)]
            (str paragraph "\t" audio-path "\t" notes "\r")
            )
          )
        )
      )
    )
  )

(defn- test-one-map-to-tsv []
  (spit "test.txt" (apply str (first (one-map-to-tsv (ccdb/display-all-cards-in-deck "jared" "53783afc31daa6e263c91ac0")))))
  ; (ccdb/display-all-cards-in-deck "jared" "53783afc31daa6e263c91ac0")
  )

(defn export-deck
  "main function taking entire deck and producing a tsv file for anki import"
  [user-coll-name deckid]
  (println (ccdb/display-all-cards-in-deck user-coll-name deckid))
  (let [one-deck (ccdb/display-all-cards-in-deck user-coll-name deckid)]
    (for [one-card-map one-deck]
      (one-map-to-tsv one-card-map)
      )
    )
  )

(defn- test-notes-format []
  (let [whole-map (first ((ccdb/display-all-cards-in-deck "jared" "53783afc31daa6e263c91ac0") :cards))]
    (make-notes-string (whole-map :notes))
    ))

(defn- test-export-deck []
  (export-deck "jared" "53783afc31daa6e263c91ac0"))


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
