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
  "takes vector of note maps and one note's line to highlight
produces string for notes section"
  [all-card-notes to-highlight-note font-color]
  (let [all-notes-except-highlighted (apply str (for [one-note all-card-notes]
                                                  (if (not= to-highlight-note one-note)
                                                    (make-one-note-line one-note false font-color))))
        highlighted-note-line (make-one-note-line to-highlight-note true font-color)
        ]
    (if (not= highlighted-note-line "<div><font color=\"\"></font>=</div>")
      (str highlighted-note-line all-notes-except-highlighted)
      (str all-notes-except-highlighted)
      )
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
            (str paragraph "\t" "[sound:" audio-path "]" "\t" notes "\r")
            )
          )
        )
      )
    )
  )


(defn sound-cards-export
  "takes a user-coll-name and deckid as a string and makes a listening card for each"
  [user-coll-name deckid]
  (let [all-card-maps (ccdb/display-all-cards-in-deck user-coll-name deckid)
        all-cards (all-card-maps :cards)
        font-color (all-card-maps :font-color)]
    (apply str (for [one-card all-cards]
                 (let [paragraph (one-card :paragraph)
                       audio-path (one-card :audio-path)
                       notes (make-notes-string (one-card :notes) {:japanese "" :english "" :reading ""} font-color)
                       ]
                   (str paragraph "\t" "[sound:" audio-path "]" "\t" notes "\r")
                   )
                 ))
    )
  )

(defn- test-sound-cards-export []
  (sound-cards-export "jared" "53783afc31daa6e263c91ac0")
  )

(defn export-deck
  "main function taking entire deck and producing a tsv file for anki import"
  [user-coll-name deckid]
  (let [all-maps (one-map-to-tsv (ccdb/display-all-cards-in-deck user-coll-name deckid))]
    (apply str (for [one-map all-maps]
                 (apply str one-map)
                 ))
    )
  )


(defn- test-export-deck []
  (export-deck "jared" "53783afc31daa6e263c91ac0")
  )


(defn- test-one-map-to-tsv []
  (let [all-maps (one-map-to-tsv (ccdb/display-all-cards-in-deck "jared" "53783afc31daa6e263c91ac0"))]
    ;(clojure.pprint/pprint all-maps)
    (first all-maps)
    )
  ; (ccdb/display-all-cards-in-deck "jared" "53783afc31daa6e263c91ac0")
  )

(defn- test-notes-format []
  (let [whole-map (first ((ccdb/display-all-cards-in-deck "jared" "53783afc31daa6e263c91ac0") :cards))]
    (make-notes-string (whole-map :notes))
    ))
